package com.opabs.trustchain.utils;

import com.opabs.common.enums.KeyUsages;
import com.opabs.common.model.GenerateCSRRequest;
import com.opabs.common.model.KeyType;
import com.opabs.trustchain.controller.command.GenerateCSRBase;
import com.opabs.trustchain.exception.CurveNameMissingException;
import com.opabs.trustchain.exception.InternalServerErrorException;
import com.opabs.trustchain.exception.KeySizeMissingException;
import com.opabs.trustchain.model.CertificateInfo;
import com.opabs.trustchain.model.PublicKeyInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.jce.provider.JCEECPublicKey;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.*;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class CertificateUtils {

    public static byte[] fromPemCertificate(String certificateAsPem) {
        PemReader pemReader = new PemReader(new StringReader(certificateAsPem));
        try {
            PemObject certificate = pemReader.readPemObject();
            return certificate.getContent();
        } catch (Exception ex) {
            log.error("Error occurred while parsing pem encoded certificate.", ex);
            throw new InternalServerErrorException();
        }
    }

    public static String toPemCertificate(byte[] certificateDer) {
        StringWriter certificatePem = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(certificatePem);
        try {
            Certificate x509Cert = getCertificateObject(certificateDer);
            pemWriter.writeObject(x509Cert);
            pemWriter.flush();
            return certificatePem.toString();
        } catch (Exception ex) {
            log.error("Error occurred while converting certificate in der to pem.");
            throw new InternalServerErrorException();
        }
    }

    public static CertificateInfo getCertificateInfo(String pemCertificate) {
        try {
            byte[] certDer = fromPemCertificate(pemCertificate);
            return getCertificateInfo(certDer);
        } catch (Exception ex) {
            log.error("Error occurred while reading certificate info.", ex);
            throw new InternalServerErrorException();
        }
    }

    public static CertificateInfo getCertificateInfo(byte[] certDer) throws CertificateException {
        X509Certificate certificateObject = getCertificateObject(certDer);
        String publicKeyFingerprint = getFingerprint(certificateObject.getPublicKey().getEncoded());
        String certificateFingerprint = getFingerprint(certificateObject.getEncoded());

        CertificateInfo certificateInfo = new CertificateInfo();
        certificateInfo.setCertificateFingerprint(certificateFingerprint);
        certificateInfo.setPublicKeyFingerprint(publicKeyFingerprint);
        certificateInfo.setIssuerDistinguishedName(certificateObject.getIssuerDN().getName());
        certificateInfo.setSubjectDistinguishedName(certificateObject.getSubjectDN().getName());
        certificateInfo.setValidFrom(certificateObject.getNotBefore());
        certificateInfo.setValidUpto(certificateObject.getNotAfter());
        PublicKeyInfo keyInfo = getKeyLength(certificateObject.getPublicKey());
        certificateInfo.setKeyLength(keyInfo.getKeyLength());
        certificateInfo.setNamedCurve(keyInfo.getNamedCurve());
        List<KeyUsages> keyUsages = getKeyUsages(certificateObject);
        certificateInfo.setKeyUsages(keyUsages);
        certificateInfo.setDateIssued(certificateObject.getNotBefore());
        certificateInfo.setExpiryDate(certificateObject.getNotAfter());
        if (keyUsages.contains(KeyUsages.KEY_CERT_SIGN) && keyUsages.contains(KeyUsages.CRL_SIGN)) {
            certificateInfo.setPathLengthConstraint(certificateObject.getBasicConstraints());
        }

        populateValidity(certificateObject, certificateInfo);
        return certificateInfo;
    }

    public static List<KeyUsages> getKeyUsages(X509Certificate certificateObject) {
        List<KeyUsages> certKeyUsagesList = new ArrayList<>();
        boolean[] keyUsages = certificateObject.getKeyUsage();
        for (KeyUsages keyUsage : KeyUsages.values()) {
            if (keyUsages[keyUsage.getKeyUsageByteIndex()]) {
                certKeyUsagesList.add(keyUsage);
            }
        }
        return certKeyUsagesList;
    }

    public static List<KeyUsages> getKeyUsages(byte[] certDer) {
        try {
            X509Certificate certificate = getCertificateObject(certDer);
            return getKeyUsages(certificate);
        } catch (CertificateException ex) {
            log.error("Error occurred while parsing certificate.", ex);
            throw new InternalServerErrorException();
        }
    }

    /**
     * This calculates the public key length given the public key.
     *
     * @param pk the public key to find the key length of.
     * @return the length of the public key.
     */
    public static PublicKeyInfo getKeyLength(final PublicKey pk) {
        int len = -1;
        String namedCurve = null;
        if (pk instanceof RSAPublicKey) {
            final RSAPublicKey rsapub = (RSAPublicKey) pk;
            len = rsapub.getModulus().bitLength();
        } else if (pk instanceof JCEECPublicKey) {
            final JCEECPublicKey ecpriv = (JCEECPublicKey) pk;
            final org.bouncycastle.jce.spec.ECParameterSpec spec = ecpriv.getParameters();
            if (spec != null) {
                len = spec.getN().bitLength();
            } else {
                // We support the key, but we don't know the key length
                len = 0;
            }
        } else if (pk instanceof ECPublicKey) {
            final ECPublicKey ecpriv = (ECPublicKey) pk;
            final java.security.spec.ECParameterSpec spec = ecpriv.getParams();
            namedCurve = spec.toString();
            len = spec.getOrder().bitLength(); // does this really return something we expect?
        } else if (pk instanceof DSAPublicKey) {
            final DSAPublicKey dsapub = (DSAPublicKey) pk;
            if (dsapub.getParams() != null) {
                len = dsapub.getParams().getP().bitLength();
            } else {
                len = dsapub.getY().bitLength();
            }
        }
        return PublicKeyInfo.builder().keyLength(len).namedCurve(namedCurve).build();
    }

    private static void populateValidity(X509Certificate certificateObject, CertificateInfo certificateInfo) {
        try {
            certificateObject.checkValidity(new Date());
            certificateInfo.setExpired(false);
        } catch (CertificateExpiredException exception) {
            certificateInfo.setExpired(true);
        } catch (CertificateNotYetValidException exception) {
            certificateInfo.setNotYetValid(true);
        }
    }

    private static String getFingerprint(byte[] content) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            return new String(Hex.encode(messageDigest.digest(content)));
        } catch (NoSuchAlgorithmException ex) {
            log.error("Error occurred while computing fingerprint.", ex);
            throw new InternalServerErrorException();
        }
    }

    public static X509Certificate getCertificateObject(byte[] certificateDer) throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certificateDer));
    }

    public static byte[] getP7b(List<byte[]> certificateDerList) throws CertificateException, CMSException, IOException {
        List<X509Certificate> certificateChain = new ArrayList<>();
        for (byte[] certificateContent : certificateDerList) {
            certificateChain.add(getCertificateObject(certificateContent));
        }
        CMSProcessableByteArray msg = new CMSProcessableByteArray("".getBytes());
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        JcaCertStore store = new JcaCertStore(certificateChain);
        gen.addCertificates(store);
        CMSSignedData signedData = gen.generate(msg);
        return signedData.getEncoded();
    }

    public static <T extends GenerateCSRBase> GenerateCSRRequest createCSRRequest(T command, UUID tenantId) {
        GenerateCSRRequest request = new GenerateCSRRequest();
        request.setKeyType(command.getKeyType());
        if (StringUtils.isEmpty(command.getPrivateKeyAlias())) {
            String generatedKeyAlias = generateKeyAlias(command.getKeyType(), tenantId);
            request.setPrivateKeyAlias(generatedKeyAlias);
        } else {
            request.setPrivateKeyAlias(command.getPrivateKeyAlias());
        }
        request.setSubjectDN(command.getSubjectDistinguishedName());
        if (command.getKeyType() == KeyType.RSA) {
            if (command.getKeySize() == null) {
                throw new KeySizeMissingException();
            }
            request.setKeyGenParams(
                    Map.of("keySize", command.getKeySize().getLength())
            );
        } else {
            if (command.getNamedCurve() == null) {
                throw new CurveNameMissingException();
            }
            request.setKeyGenParams(
                    Map.of("namedCurve", command.getNamedCurve().name())
            );
        }
        return request;
    }

    private static String generateKeyAlias(KeyType keyType, UUID tenantId) {
        StringBuilder keyAlias = new StringBuilder();
        keyAlias.append(keyType.name().toLowerCase());
        keyAlias.append("-");
        SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddhhmmss");
        keyAlias.append(dt.format(new Date()));
        keyAlias.append(tenantId);
        return keyAlias.toString();
    }

    private CertificateUtils() {
    }
}
