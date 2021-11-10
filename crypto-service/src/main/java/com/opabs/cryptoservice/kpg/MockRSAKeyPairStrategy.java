package com.opabs.cryptoservice.kpg;

import com.opabs.common.model.KeyType;
import com.opabs.cryptoservice.exception.KeyPairGenerationFailureException;
import com.opabs.cryptoservice.exception.KeySizeMissingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Map;

@Slf4j
@Component
@Profile("local")
public class MockRSAKeyPairStrategy implements KeyPairStrategy {

    public static final String KEY_SIZE = "keySize";

    @Override
    public KeyPair generate(Map<String, Object> params) {
        if (!params.containsKey(KEY_SIZE)) {
            throw new KeySizeMissingException();
        }
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize((Integer) params.get(KEY_SIZE));
            return kpg.generateKeyPair();
        } catch (Exception ex) {
            log.error("Error occurred while generating RSA key.", ex);
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
