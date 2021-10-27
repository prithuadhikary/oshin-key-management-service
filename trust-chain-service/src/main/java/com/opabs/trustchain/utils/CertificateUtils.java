package com.opabs.trustchain.utils;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.StringReader;

@Slf4j
public class CertificateUtils {

    public static byte[] parsePemCertificate(String certificateAsPem) {
        PemReader pemReader = new PemReader(new StringReader(certificateAsPem));
        try {
            PemObject certificate = pemReader.readPemObject();
            return certificate.getContent();
        } catch (Exception ex) {
            //TODO: Move exceptions to common.
        }
        return null;
    }

    private CertificateUtils() {
    }
}
