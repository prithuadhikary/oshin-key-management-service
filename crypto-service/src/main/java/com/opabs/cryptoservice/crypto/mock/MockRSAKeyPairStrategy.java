package com.opabs.cryptoservice.crypto.mock;

import com.opabs.common.model.KeyType;
import com.opabs.cryptoservice.crypto.kpg.KeyPairStrategy;
import com.opabs.cryptoservice.exception.KeyPairGenerationFailureException;
import com.opabs.cryptoservice.exception.KeySizeMissingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Profile("local")
public class MockRSAKeyPairStrategy implements KeyPairStrategy {

    public static final String KEY_SIZE = "keySize";

    private final Map<String, PrivateKey> privateKeyMap = new ConcurrentHashMap<>();

    @Override
    public KeyPair generate(Map<String, Object> params, String privateKeyAlias) {
        if (!params.containsKey(KEY_SIZE)) {
            throw new KeySizeMissingException();
        }
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize((Integer) params.get(KEY_SIZE));
            KeyPair keyPair = kpg.generateKeyPair();
            privateKeyMap.put(privateKeyAlias, keyPair.getPrivate());
            return keyPair;
        } catch (Exception ex) {
            log.error("Error occurred while generating RSA key.", ex);
            throw new KeyPairGenerationFailureException();
        }
    }

    @Override
    public KeyType supportedKeyType() {
        return KeyType.RSA;
    }
}
