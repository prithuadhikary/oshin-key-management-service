package com.opabs.cryptoservice.crypto.kpg;

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
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Map;

import static com.opabs.cryptoservice.constants.Constants.*;

@Slf4j
@Component
@Profile("cloud")
public class DefaultRSAKeyPairStrategy implements KeyPairStrategy {

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
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return factory.generatePrivate(pkcs8EncodedKeySpec);
    }

    @Override
    public KeyType supportedKeyType() {
        return KeyType.RSA;
    }
}
