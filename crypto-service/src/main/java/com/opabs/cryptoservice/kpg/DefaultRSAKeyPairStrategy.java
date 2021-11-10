package com.opabs.cryptoservice.kpg;

import com.cavium.key.parameter.CaviumRSAKeyGenParameterSpec;
import com.opabs.common.model.KeyType;
import com.opabs.cryptoservice.exception.KeyLabelMissingException;
import com.opabs.cryptoservice.exception.KeyPairGenerationFailureException;
import com.opabs.cryptoservice.exception.KeySizeMissingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.*;
import java.util.Map;

@Slf4j
@Component
@Profile("cloud")
public class DefaultRSAKeyPairStrategy implements KeyPairStrategy {

    public static final String KEY_SIZE = "keySize";

    public static final String KEY_LABEL = "keyLabel";
    public static final String AWS_HSM_JCE_PROVIDER_NAME = "Cavium";

    @Override
    public KeyPair generate(Map<String, Object> params) {
        if (!params.containsKey(KEY_SIZE)) {
            throw new KeySizeMissingException();
        }
        if (!params.containsKey(KEY_LABEL)) {
            throw new KeyLabelMissingException();
        }
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("rsa", AWS_HSM_JCE_PROVIDER_NAME);
            int keySize = (int) params.get(KEY_SIZE);
            String keyLabel = (String) params.get(KEY_LABEL);
            CaviumRSAKeyGenParameterSpec spec = new CaviumRSAKeyGenParameterSpec(keySize, new BigInteger("65537"), keyLabel + ":public", keyLabel + ":private", true, false);
            keyPairGen.initialize(spec);
            return keyPairGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException ex) {
            log.error("Error occurred while generating key pair using cloudHSM.", ex);
            throw new KeyPairGenerationFailureException();
        }
    }

    @Override
    public PrivateKey loadPrivateKey(byte[] privateKeyBytes) throws Exception {
        return null;
    }

    @Override
    public KeyType supportedKeyType() {
        return KeyType.RSA;
    }
}
