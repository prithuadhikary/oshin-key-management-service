package com.opabs.cryptoservice.service.certificate;

import com.opabs.common.enums.KeyUsages;
import com.opabs.common.model.*;
import com.opabs.cryptoservice.config.MockConfig;
import com.opabs.cryptoservice.exception.*;
import com.opabs.cryptoservice.kpg.KeyPairStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.*;
import org.bouncycastle.operator.bc.BcECContentSignerBuilder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultCertificateService implements CertificateService {

    public static final String PROVIDER_NAME = "BC";
    public static final String RSA = "RSA";
    public static final String EC = "EC";

    private final MockConfig mockConfig;

    private final Set<KeyPairStrategy> keyPairStrategies;

    private SecretKey aesKey;

    @PostConstruct
    public void setup() {
        this.aesKey = new SecretKeySpec(Base64.getDecoder().decode(mockConfig.getAesKey()), "AES");
    }

    public Mono<GenerateCSRResponse> generateCertificateSigningRequest(GenerateCSRRequest request) {
        return Mono.fromCallable(() -> {
            try {
                X500Name subjectDN = new X500Name(request.getSubjectDN());
                KeyPair keyPair = generateKeyPair(request.getKeyType(), request.getKeyGenParams());
                ContentSigner contentSigner = new JcaContentSignerBuilder(request.getKeyType().getCertificateSignatureAlgo()).build(keyPair.getPrivate());
                final PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(
                        subjectDN, keyPair.getPublic());
                PKCS10CertificationRequest csr = builder.build(contentSigner);
                String csrPem = writeCSRAsPem(csr);
                String wrappedKey = wrapKey(keyPair.getPrivate());
                GenerateCSRResponse response = new GenerateCSRResponse();
                response.setPkcs10CSR(csrPem);
                response.setWrappedKey(wrappedKey);
                return response;
            } catch (OperatorCreationException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | IOException e) {
                log.error("Error occurred while generating key pair.", e);
                throw new KeyPairGenerationFailureException();
            }
        });
    }

    public Mono<CertificateSigningResponse> sign(CertificateSigningRequest request) {
        return Mono.fromCallable(() -> {

            //1. Read PKCS10 Certificate Signing Request
            PEMParser reader = new PEMParser(new StringReader(request.getPkcs10CSR()));

            PKCS10CertificationRequest pkcs10req = new PKCS10CertificationRequest(reader.readPemObject().getContent());
            // Validate CSR signature.
            validateCSRSignature(pkcs10req);

            String issuerDN;
            X509Certificate issuerCertificate = null;
            Optional<KeyType> issuerKeyType = Optional.empty();
            if (request.isSelfSigned()) {
                //3. If self signed, the issuer and the subject dn will be the same.
                issuerDN = pkcs10req.getSubject().toString();
            } else {
                //3. Read issuer certificate and fetch out the issuer distinguished name.
                issuerCertificate = readIssuerCertificate(request.getIssuerCertificate());
                issuerDN = issuerCertificate.getIssuerDN().getName();
                String pubKeyAlgo = issuerCertificate.getPublicKey().getAlgorithm();
                if (RSA.equals(pubKeyAlgo)) {
                    issuerKeyType = Optional.of(KeyType.RSA);
                } else if (EC.equals(pubKeyAlgo)) {
                    issuerKeyType = Optional.of(KeyType.ELLIPTIC_CURVE);
                } else {
                    throw new UnsupportedIssuerKeyTypeException(pubKeyAlgo);
                }
            }

            //4. Read CA private key.
            IssuerPrivateKeyInfo caPrivateKeyInfo = readIssuerPrivateKey(request.getWrappedIssuerPrivateKey(), issuerKeyType);

            //5. TODO: Validate if wrapped private key matches the issuer certificate's public key.

            validateSignatureAlgorithm(caPrivateKeyInfo, request.getSignatureAlgorithm());

            //6. Create certificate builder.
            X509v3CertificateBuilder certificateGenerator = new X509v3CertificateBuilder(
                    new X500Name(issuerDN), new BigInteger("1"),
                    Date.from(request.getValidFrom().toInstant()),
                    Date.from(request.getValidFrom().plus(request.getValidityInYears(), ChronoUnit.YEARS).toInstant()),
                    pkcs10req.getSubject(), pkcs10req.getSubjectPublicKeyInfo());

            //7. Add extensions.
            populateExtensions(request, pkcs10req, issuerCertificate, certificateGenerator);

            ContentSigner sigGen = new JcaContentSignerBuilder(request.getSignatureAlgorithm().name())
                    .setProvider("Cavium").build(caPrivateKeyInfo.getPrivateKey());

            X509CertificateHolder holder = certificateGenerator.build(sigGen);

            Certificate eeX509CertificateStructure = holder.toASN1Structure();

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            CertificateSigningResponse response = new CertificateSigningResponse();

            java.security.cert.Certificate certificate = cf.generateCertificate(new ByteArrayInputStream(eeX509CertificateStructure.getEncoded()));

            StringWriter writer = new StringWriter();

            try (JcaPEMWriter pemWriter = new JcaPEMWriter(writer)) {
                pemWriter.writeObject(certificate);
            }
            response.setCertificate(writer.toString());
            return response;

        });
    }

    private void validateCSRSignature(PKCS10CertificationRequest pkcs10req) {
        boolean isSignatureValid = false;
        try {
            ContentVerifierProvider contentVerifierProvider = new JcaContentVerifierProviderBuilder().setProvider(PROVIDER_NAME).build(pkcs10req.getSubjectPublicKeyInfo());
            isSignatureValid = pkcs10req.isSignatureValid(contentVerifierProvider);
        } catch (OperatorCreationException | PKCSException exception) {
            log.error("Error occurred while validating the signature ok the certificate signing request.", exception);
        }
        if (!isSignatureValid) {
            throw new CSRSignatureInvalidException();
        }
    }

    private void validateSignatureAlgorithm(IssuerPrivateKeyInfo caPrivateKeyInfo, SigningAlgorithm signatureAlgorithm) {
        if (caPrivateKeyInfo.getKeyType() != signatureAlgorithm.getSupportedIssuerKeyType()) {
            throw new SigningAlgorithmAndSigningKeyMismatchException(signatureAlgorithm, caPrivateKeyInfo.getKeyType());
        }
    }

    private void populateExtensions(CertificateSigningRequest request, PKCS10CertificationRequest pkcs10req, X509Certificate issuerCertificate, X509v3CertificateBuilder certificateGenerator) throws NoSuchAlgorithmException, CertIOException, CertificateEncodingException {
        JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();

        List<KeyUsages> keyUsages = request.getKeyUsages();

        Optional<KeyUsage> keyUsage = getKeyUsages(keyUsages);

        if (keyUsage.isPresent()) {
            certificateGenerator.addExtension(Extension.keyUsage, false, keyUsage.get());
        }

        if (request.isSelfSigned()) {
            //Possibly for the generation of root CA certificate.
            //In case of self signed certificate, the subject and the authority key identifier will be same.
            certificateGenerator.addExtension(Extension.subjectKeyIdentifier, false,
                    extensionUtils.createSubjectKeyIdentifier(pkcs10req.getSubjectPublicKeyInfo()));

            certificateGenerator.addExtension(Extension.authorityKeyIdentifier, false,
                    extensionUtils.createAuthorityKeyIdentifier(pkcs10req.getSubjectPublicKeyInfo()));
        } else {
            certificateGenerator.addExtension(Extension.subjectKeyIdentifier, false,
                    extensionUtils.createSubjectKeyIdentifier(pkcs10req.getSubjectPublicKeyInfo()));

            certificateGenerator.addExtension(Extension.authorityKeyIdentifier, false,
                    extensionUtils.createAuthorityKeyIdentifier(issuerCertificate));
        }

        // If the keyusages contain keyCertSign and CRL sign, then it is a CA certificate for a trust chain.
        // And should include the BasicConstraints for cA and the path length constraint.
        if (keyUsage.isPresent() && keyUsages.contains(KeyUsages.KEY_CERT_SIGN) && keyUsages.contains(KeyUsages.CRL_SIGN)) {
            // CA Certificate
            setCABasicConstraints(request, certificateGenerator, issuerCertificate);
        } else {
            // Adding extension specifying cA flag to be false.
            certificateGenerator.addExtension(Extension.basicConstraints, true,
                    new BasicConstraints(false));
        }
    }

    private void setCABasicConstraints(CertificateSigningRequest request, X509v3CertificateBuilder certificateGenerator, X509Certificate issuerCertificate) throws CertIOException {
        // 1. If CA certificate and not root, and if path length constraint present in issuer certificate,
        //    Fetch out the issuer path length and decrement it by 1 and set in the current certificate.
        // 2. If a self signed root CA, set basic constraints based on what has been passed in the request,
        if (request.isSelfSigned()) {
            if (request.getPathLengthConstraint() != null) {
                certificateGenerator.addExtension(Extension.basicConstraints, true,
                        new BasicConstraints(request.getPathLengthConstraint()));
            } else {
                certificateGenerator.addExtension(Extension.basicConstraints, true,
                        new BasicConstraints(true));
            }
        } else {
            int issuerBasicConstraint = issuerCertificate.getBasicConstraints();
            // If path length constraint is zero, no new certificate can be issued with a lesser path length constraint.
            // Hence throwing BasicConstraintViolationException.
            if (issuerBasicConstraint == 0) {
                throw new BasicConstraintViolationException();
            }
            // If issuer basic constraint is int max, the path length is unlimited.
            if (issuerBasicConstraint == Integer.MAX_VALUE) {
                certificateGenerator.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
            } else {
                // Decrementing path length constraint obtained from the issuer certificate by 1, zero is the min value.
                // If zero, then it must be the last intermediate CA certificate in the trust chain that can be issued.
                certificateGenerator.addExtension(Extension.basicConstraints, true, new BasicConstraints(issuerBasicConstraint - 1));
            }
        }

    }

    private Optional<KeyUsage> getKeyUsages(List<KeyUsages> keyUsages) {
        int finalKeyUsageInt = 0;
        if (keyUsages != null && !keyUsages.isEmpty()) {
            for (KeyUsages keyUsage : keyUsages) {
                finalKeyUsageInt |= keyUsage.getKeyUsage();
            }
        }
        if (finalKeyUsageInt > 0) {
            return Optional.of(new KeyUsage(finalKeyUsageInt));
        } else {
            return Optional.empty();
        }
    }

    private X509Certificate readIssuerCertificate(String issuerCertificate) throws Exception {
        try (InputStream inputStream = new ByteArrayInputStream(issuerCertificate.getBytes(StandardCharsets.UTF_8))) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(inputStream);
        }
    }

    private IssuerPrivateKeyInfo readIssuerPrivateKey(String wrappedPrivateKey, Optional<KeyType> issuerKeyType) throws Exception {
        byte[] unwrappedKey = unwrapKey(wrappedPrivateKey);
        IssuerPrivateKeyInfo info = new IssuerPrivateKeyInfo();
        if (issuerKeyType.isPresent()) {
            Optional<KeyPairStrategy> keyPairStrategy = keyPairStrategies.stream().filter(kps -> kps.supportedKeyType() == issuerKeyType.get()).findFirst();
            if (keyPairStrategy.isEmpty()) {
                throw new InternalServerErrorException();
            }
            info.setPrivateKey(keyPairStrategy.get().loadPrivateKey(unwrappedKey));
            info.setKeyType(keyPairStrategy.get().supportedKeyType());
        } else {
            PrivateKey privateKey;
            for (KeyPairStrategy strategy : keyPairStrategies) {
                try {
                    privateKey = strategy.loadPrivateKey(unwrappedKey);
                    info.setPrivateKey(privateKey);
                    info.setKeyType(strategy.supportedKeyType());
                    break;
                } catch (Exception ex) {
                    log.debug("Strategy failed to load issuer private key.");
                }
            }
        }
        return info;
    }

    private KeyPair generateKeyPair(KeyType keyType, Map<String, Object> keyGenParams) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Optional<KeyPairStrategy> kpgStrategy = keyPairStrategies.stream().filter(keyPairStrategy -> keyPairStrategy.supportedKeyType() == keyType).findFirst();
        if (kpgStrategy.isEmpty()) {
            throw new InternalServerErrorException();
        } else {
            return kpgStrategy.get().generate(keyGenParams);
        }
    }

    private String writeCSRAsPem(PKCS10CertificationRequest csr) throws IOException {
        PemObject csrPemObject = new PemObject("CERTIFICATE REQUEST", csr.getEncoded());
        StringWriter stringWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
        pemWriter.writeObject(csrPemObject);
        pemWriter.flush();
        return stringWriter.toString();
    }

    private String wrapKey(PrivateKey keyToWrap) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, this.aesKey);
        byte[] wrappedKey = cipher.doFinal(keyToWrap.getEncoded());
        return Base64.getEncoder().encodeToString(wrappedKey);
    }

    private byte[] unwrapKey(String wrappedKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] wrappedKeyDecoded = Base64.getDecoder().decode(wrappedKey);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, this.aesKey);
        return cipher.doFinal(wrappedKeyDecoded);
    }
}
