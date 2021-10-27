package com.opabs.cryptoservice.kpg;

import com.opabs.common.model.KeyType;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Map;

/**
 * KeyPair generator. Implementations will generate different types of keys based on key types.
 */
public interface KeyPairStrategy {

    /**
     * Generates key pair for {@link KeyType} with {@link KeyPairGenParams}.
     *
     * @param params key pair gen parameters.
     * @return A keypair of type {@link KeyType}.
     */
    KeyPair generate(Map<String, Object> params);

    PrivateKey loadPrivateKey(byte[] privateKeyBytes) throws Exception;

    KeyType supportedKeyType();

}
