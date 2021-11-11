package com.opabs.cryptoservice.service.certificate;

import com.opabs.common.enums.KeyUsages;
import com.opabs.common.model.*;
import com.opabs.cryptoservice.exception.*;
import com.opabs.cryptoservice.service.KeyManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultCertificateService implements CertificateService {

    public static final String RSA = "RSA";
    public static final String EC = "EC";

    private final KeyManagementService keyManagementService;

    private final Provider activeProvider;

    public Mono<GenerateCSRResponse> generateCertificateSigningRequest(GenerateCSRRequest request) {
        return Mono.fromCallable(() -> {
            try {
                X500Name subjectDN = new X500Name(request.getSubjectDN());
                KeyPair keyPair = keyManagementService.generateKeyPair(request.getKeyType(), request.getPrivateKeyAlias(), request.getKeyGenParams());
                ContentSigner contentSigner = new JcaContentSignerBuilder(request.getKeyType().getCertificateSignatureAlgo())
                        .setProvider(activeProvider).build(keyPair.getPrivate());
                final PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(
                        subjectDN, keyPair.getPublic());
                PKCS10CertificationRequest csr = builder.build(contentSigner);
                String csrPem = writeCSRAsPem(csr);
                GenerateCSRResponse response = new GenerateCSRResponse();
                response.setPkcs10CSR(csrPem);
                response.setPrivateKeyAlias(request.getPrivateKeyAlias());
                return response;
            } catch (OperatorCreationException | IOException e) {
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
            if (request.isSelfSigned()) {
                //3. If self signed, the issuer and the subject dn will be the same.
                issuerDN = pkcs10req.getSubject().toString();
                log.info("Issuer DN for self signed certificate: {}", issuerDN);
            } else {
                //3. Read issuer certificate and fetch out the issuer distinguished name.
                issuerCertificate = readIssuerCertificate(request.getIssuerCertificate());
                issuerDN = issuerCertificate.getIssuerDN().getName();
            }

            //4. Read CA private key.
            IssuerPrivateKeyInfo caPrivateKeyInfo = readIssuerPrivateKey(request.getIssuerPrivateKeyAlias());

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
                    .setProvider(activeProvider).build(caPrivateKeyInfo.getPrivateKey());

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

    private Optional<KeyType> getKeyType(String pubKeyAlgo) {
        Optional<KeyType> issuerKeyType = Optional.empty();
        log.info("Public key algo: {}", pubKeyAlgo);
        if (RSA.equals(pubKeyAlgo)) {
            issuerKeyType = Optional.of(KeyType.RSA);
        } else if (EC.equals(pubKeyAlgo)) {
            issuerKeyType = Optional.of(KeyType.ELLIPTIC_CURVE);
        }
        return issuerKeyType;
    }

    private void validateCSRSignature(PKCS10CertificationRequest pkcs10req) {
        boolean isSignatureValid = false;
        try {
            ContentVerifierProvider contentVerifierProvider = new JcaContentVerifierProviderBuilder()
                    .build(pkcs10req.getSubjectPublicKeyInfo());
            isSignatureValid = pkcs10req.isSignatureValid(contentVerifierProvider);
        } catch (OperatorCreationException | PKCSException exception) {
            log.error("Error occurred while validating the signature of the certificate signing request.", exception);
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

    private IssuerPrivateKeyInfo readIssuerPrivateKey(String privateKeyAlias) {
        IssuerPrivateKeyInfo info = new IssuerPrivateKeyInfo();
        PrivateKey issuerPrivateKey = (PrivateKey) keyManagementService.getKeyForKeyAlias(privateKeyAlias);
        info.setPrivateKey(issuerPrivateKey);
        log.info("Issuer private key algorithm: {}", issuerPrivateKey.getAlgorithm());
        Optional<KeyType> keyType = getKeyType(issuerPrivateKey.getAlgorithm());
        info.setKeyType(keyType.orElseThrow(() -> new UnsupportedIssuerKeyTypeException(issuerPrivateKey.getAlgorithm())));
        return info;
    }


    private String writeCSRAsPem(PKCS10CertificationRequest csr) throws IOException {
        PemObject csrPemObject = new PemObject("CERTIFICATE REQUEST", csr.getEncoded());
        StringWriter stringWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
        pemWriter.writeObject(csrPemObject);
        pemWriter.flush();
        return stringWriter.toString();
    }

}
