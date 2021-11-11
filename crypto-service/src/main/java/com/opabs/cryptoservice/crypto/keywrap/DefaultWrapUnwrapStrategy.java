package com.opabs.cryptoservice.crypto.keywrap;

import com.cavium.cfm2.CFM2Exception;
import com.opabs.cryptoservice.constants.Constants;
import com.opabs.cryptoservice.exception.KeyWrappingException;
import com.opabs.cryptoservice.util.CryptoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Base64;

@Slf4j
@Component
@Profile("cloud")
public class DefaultWrapUnwrapStrategy implements KeyWrapUnwrapStrategy {

    @Override
    public String wrapKey(PrivateKey privateKey, String keyAlias) {
        try {
            Key wrappingKey = CryptoUtils.getKeyForAlias(keyAlias);
            Cipher cipher = Cipher.getInstance("AESWrap/ECB/PKCS5Padding", Constants.AWS_HSM_JCE_PROVIDER_NAME);
            cipher.init(Cipher.WRAP_MODE, wrappingKey);
            return Base64.getEncoder().encodeToString(cipher.wrap(privateKey));
        } catch (CFM2Exception | NoSuchAlgorithmException | NoSuchPaddingException |
                NoSuchProviderException | InvalidKeyException | IllegalBlockSizeException ex) {
            log.error("Error occurred while wrapping the private key.", ex);
            throw new KeyWrappingException();
        }
    }

    @Override
    public Key unwrapKey(String wrappedKey, String keyAlias) {
        try {
            Key wrappingKey = CryptoUtils.getKeyForAlias(keyAlias);
            Cipher cipher = Cipher.getInstance("AESWrap/ECB/PKCS5Padding", Constants.AWS_HSM_JCE_PROVIDER_NAME);
            cipher.init(Cipher.UNWRAP_MODE, wrappingKey);
            byte[] wrappedKeyBytes = Base64.getDecoder().decode(wrappedKey);
            return cipher.unwrap(wrappedKeyBytes, "RSA", Cipher.PRIVATE_KEY);
        } catch (CFM2Exception | NoSuchAlgorithmException | NoSuchPaddingException |
                NoSuchProviderException | InvalidKeyException ex) {
            log.error("Error occurred while wrapping the private key.", ex);
            throw new KeyWrappingException();
        }
    }
}
