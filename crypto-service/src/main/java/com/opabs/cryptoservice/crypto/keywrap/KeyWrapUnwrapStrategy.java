package com.opabs.cryptoservice.crypto.keywrap;

import java.security.Key;
import java.security.PrivateKey;

public interface KeyWrapUnwrapStrategy {

    /**
     * Will wrap private key with tenant specific aes key alias.
     * @param privateKey the private key to wrap.
     * @return A base64 encoded string containing the wrapped key.
     */
    String wrapKey(PrivateKey privateKey, String keyAlias);

    /**
     * Will unwrap the key with tenant specific key alias.
     * @param wrappedKey
     * @return
     */
    Key unwrapKey(String wrappedKey, String keyAlias);

}
