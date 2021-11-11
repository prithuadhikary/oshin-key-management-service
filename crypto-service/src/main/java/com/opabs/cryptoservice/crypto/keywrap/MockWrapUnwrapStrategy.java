package com.opabs.cryptoservice.crypto.keywrap;

import com.opabs.cryptoservice.config.MockConfig;
import com.opabs.cryptoservice.exception.KeyUnwrappingException;
import com.opabs.cryptoservice.exception.KeyWrappingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;

@Slf4j
@Profile("local")
@Component
@RequiredArgsConstructor
public class MockWrapUnwrapStrategy implements KeyWrapUnwrapStrategy {

    private final MockConfig mockConfig;

    private SecretKey aesKey;

    @PostConstruct
    public void setup() {
        this.aesKey = new SecretKeySpec(Base64.getDecoder().decode(mockConfig.getAesKey()), "AES");
    }

    @Override
    public String wrapKey(PrivateKey privateKey, String keyAlias) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, this.aesKey);
            byte[] wrappedKey = cipher.doFinal(privateKey.getEncoded());
            return Base64.getEncoder().encodeToString(wrappedKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            log.error("Error occurred while wrapping key", ex);
            throw new KeyWrappingException();
        }
    }

    @Override
    public Key unwrapKey(String wrappedKey, String keyAlias) {
        try {
            byte[] wrappedKeyDecoded = Base64.getDecoder().decode(wrappedKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, this.aesKey);
            return new SecretKeySpec(cipher.doFinal(wrappedKeyDecoded), "RSA");
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException ex) {
            log.error("Error occurred while unwrapping key.");
            throw new KeyUnwrappingException();
        }
    }
}
