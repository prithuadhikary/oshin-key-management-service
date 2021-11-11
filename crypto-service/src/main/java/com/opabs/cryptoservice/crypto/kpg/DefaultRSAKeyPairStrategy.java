package com.opabs.cryptoservice.crypto.kpg;

import com.cavium.key.parameter.CaviumRSAKeyGenParameterSpec;
import com.opabs.common.model.KeyType;
import com.opabs.cryptoservice.exception.KeyPairGenerationFailureException;
import com.opabs.cryptoservice.exception.KeySizeMissingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.*;
import java.util.Map;

import static com.opabs.cryptoservice.constants.Constants.AWS_HSM_JCE_PROVIDER_NAME;
import static com.opabs.cryptoservice.constants.Constants.KEY_SIZE;

@Slf4j
@Component
@Profile("cloud")
public class DefaultRSAKeyPairStrategy implements KeyPairStrategy {

    @Override
    public KeyPair generate(Map<String, Object> params, String privateKeyAlias) {
        if (!params.containsKey(KEY_SIZE)) {
            throw new KeySizeMissingException();
        }
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("rsa", AWS_HSM_JCE_PROVIDER_NAME);
            int keySize = (int) params.get(KEY_SIZE);
            CaviumRSAKeyGenParameterSpec spec =
                    new CaviumRSAKeyGenParameterSpec(
                            keySize,
                            new BigInteger("65537"),
                            privateKeyAlias + ":public",
                            privateKeyAlias,
                            false, true);
            keyPairGen.initialize(spec);
            return keyPairGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException ex) {
            log.error("Error occurred while generating key pair using cloudHSM.", ex);
            throw new KeyPairGenerationFailureException();
        }
    }

    @Override
    public KeyType supportedKeyType() {
        return KeyType.RSA;
    }
}
