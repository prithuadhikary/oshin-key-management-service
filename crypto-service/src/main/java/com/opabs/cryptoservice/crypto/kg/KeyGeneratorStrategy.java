package com.opabs.cryptoservice.crypto.kg;

public interface KeyGeneratorStrategy {

    /**
     * Implementations will generate symmetric keys.
     *
     * @param keyLength The length of the key in bits to generate.
     * @param keyLabel  The key label to associate the generated key with.
     * @return An instance of {@link KeyDetails} containing the symmetric key and associated data.
     */
    KeyDetails generate(int keyLength, String keyLabel);

}
