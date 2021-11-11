package com.opabs.cryptoservice.crypto.kg;

import com.cavium.cfm2.CFM2Exception;
import com.cavium.cfm2.Util;
import com.cavium.key.CaviumKey;
import com.cavium.key.parameter.CaviumAESKeyGenParameterSpec;
import com.opabs.cryptoservice.exception.KeyGenerationFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static com.opabs.cryptoservice.constants.Constants.AWS_HSM_JCE_PROVIDER_NAME;

@Slf4j
@Component
@Profile("cloud")
public class DefaultAESKeyGeneratorStrategy implements KeyGeneratorStrategy {
    @Override
    public KeyDetails generate(int keyLength, String keyLabel) {

        boolean isExtractable = false;
        boolean isPersistent = true;

        KeyGenerator keyGen;
        try {
            keyGen = KeyGenerator.getInstance("AES", AWS_HSM_JCE_PROVIDER_NAME);
            CaviumAESKeyGenParameterSpec aesSpec = new CaviumAESKeyGenParameterSpec(keyLength, keyLabel, isExtractable, isPersistent);
            keyGen.init(aesSpec);
            CaviumKey key = (CaviumKey) keyGen.generateKey();
            KeyDetails keyDetails = new KeyDetails();
            keyDetails.setKeyHandle(key.getHandle());
            keyDetails.setLabel(keyLabel);
            keyDetails.setSecretKey(key);
            Util.persistKey(key);
            return keyDetails;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | CFM2Exception ex) {
            log.error("Error occurred while generating AES key.", ex);
            throw new KeyGenerationFailureException();
        }
    }
}
