package com.opabs.cryptoservice.service;

import com.opabs.common.model.KeyType;
import com.opabs.cryptoservice.crypto.kpg.KeyPairStrategy;
import com.opabs.cryptoservice.exception.InternalServerErrorException;
import com.opabs.cryptoservice.service.storage.KeyStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.KeyPair;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class KeyManagementService {

    private final Set<KeyPairStrategy> keyPairStrategies;

    private final KeyStoreService keyStoreService;

    public KeyPair generateKeyPair(KeyType keyType, String privateKeyAlias, Map<String, Object> keyGenParams) {
        Optional<KeyPairStrategy> kpgStrategy = keyPairStrategies.stream().filter(keyPairStrategy -> keyPairStrategy.supportedKeyType() == keyType).findFirst();
        if (kpgStrategy.isEmpty()) {
            throw new InternalServerErrorException();
        } else {
            KeyPair generatedKeyPair = kpgStrategy.get().generate(keyGenParams, privateKeyAlias);
            keyStoreService.persistKey(privateKeyAlias, generatedKeyPair.getPrivate());
            return generatedKeyPair;
        }
    }

    public Key getKeyForKeyAlias(String keyAlias) {
        return keyStoreService.findPrivateKey(keyAlias);
    }

}
