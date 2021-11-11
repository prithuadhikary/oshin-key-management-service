package com.opabs.cryptoservice.service.storage;

import com.opabs.cryptoservice.exception.KeyNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.PrivateKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Profile("local")
public class MockKeyStoreService implements KeyStoreService {

    private final Map<String, Key> privateKeyMap = new ConcurrentHashMap<>();

    @Override
    public Key findPrivateKey(String keyAlias) {
        Key privateKey = privateKeyMap.get(keyAlias);
        if (privateKey == null) {
            throw new KeyNotFoundException(keyAlias);
        }
        return privateKey;
    }

    @Override
    public void persistKey(String keyAlias, PrivateKey privateKey) {
        privateKeyMap.put(keyAlias, privateKey);
    }
}
