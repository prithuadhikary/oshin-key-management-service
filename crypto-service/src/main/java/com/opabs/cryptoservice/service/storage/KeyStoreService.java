package com.opabs.cryptoservice.service.storage;

import java.security.Key;
import java.security.PrivateKey;

public interface KeyStoreService {

    Key findPrivateKey(String keyAlias);

    void persistKey(String keyAlias, PrivateKey privateKey);

}
