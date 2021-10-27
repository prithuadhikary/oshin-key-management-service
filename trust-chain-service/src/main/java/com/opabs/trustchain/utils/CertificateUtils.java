package com.opabs.trustchain.utils;

import com.opabs.common.model.GenerateCSRRequest;
import com.opabs.common.model.KeyType;
import com.opabs.trustchain.controller.command.GenerateCSRBase;
import com.opabs.trustchain.exception.InternalServerErrorException;
import com.opabs.trustchain.model.CertificateInfo;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Map;

@Slf4j
public class CertificateUtils {

    public static byte[] fromPemCertificate(String certificateAsPem) {
        PemReader pemReader = new PemReader(new StringReader(certificateAsPem));
        try {
            PemObject certificate = pemReader.readPemObject();
            return certificate.getContent();
        } catch (Exception ex) {
            //TODO: Move exceptions to common.
        }
        return null;
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
            Certificate certificateObject = getCertificateObject(certDer);
            String publicKeyFingerprint = getFingerprint(certificateObject.getPublicKey().getEncoded());
            String certificateFingerprint = getFingerprint(certificateObject.getEncoded());

            CertificateInfo certificateInfo = new CertificateInfo();
            certificateInfo.setCertificateFingerprint(certificateFingerprint);
            certificateInfo.setPublicKeyFingerprint(publicKeyFingerprint);
            return certificateInfo;
        } catch (Exception ex) {
            log.error("Error occurred while reading certificate info.", ex);
            throw new InternalServerErrorException();
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

    private static Certificate getCertificateObject(byte[] certificateDer) throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return certificateFactory.generateCertificate(new ByteArrayInputStream(certificateDer));
    }

    public static <T extends GenerateCSRBase> GenerateCSRRequest createCSRRequest(T command) {
        GenerateCSRRequest request = new GenerateCSRRequest();
        request.setKeyType(command.getKeyType());
        //TODO: Implement symmetric key alias generation per tenant/organization.
        request.setWrappingKeyAlias("aes-key-alias");
        request.setSubjectDN(command.getSubjectDistinguishedName());
        if (command.getKeyType() == KeyType.RSA) {
            request.setKeyGenParams(
                    Map.of("keySize", command.getKeySize().getLength())
            );
        } else {
            request.setKeyGenParams(
                    Map.of("namedCurve", command.getNamedCurve().name())
            );
        }
        return request;
    }

    private CertificateUtils() {
    }
}
