package com.opabs.cryptoservice.service.aes;

import com.opabs.common.model.AesDecryptRequest;
import com.opabs.common.model.AesDecryptResponse;
import com.opabs.common.model.AesEncryptRequest;
import com.opabs.common.model.AesEncryptResponse;
import com.opabs.common.model.AesCreateKeyResponse;
import reactor.core.publisher.Mono;

public interface AesService {

    /**
     * Will perform AES encryption as per the parameters defined in the {@link AesEncryptRequest} object.
     *
     * @param request the {@link AesEncryptRequest}.
     * @return an instance of populated {@link AesEncryptResponse}.
     */
    Mono<AesEncryptResponse> encrypt(AesEncryptRequest request);

    /**
     * Will perform AES decryption as per the parameters defined in the {@link AesDecryptRequest} object.
     *
     * @param request the {@link AesDecryptRequest}.
     * @return an instance of populated {@link AesDecryptResponse}.
     */
    Mono<AesDecryptResponse> decrypt(AesDecryptRequest request);

    /**
     * Will generate AES key and store it in the HSM.
     * @param keyLength The length of the key to generate.
     * @param label The label to associate with the key.
     * @return An {@link AesCreateKeyResponse} instance containing the key specifics.
     */
    Mono<AesCreateKeyResponse> createKey(int keyLength, String label);

}
