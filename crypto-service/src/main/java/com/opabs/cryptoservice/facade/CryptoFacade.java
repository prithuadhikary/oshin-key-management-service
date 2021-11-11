package com.opabs.cryptoservice.facade;

import com.opabs.common.model.*;
import reactor.core.publisher.Mono;

/**
 * Will contain all the crypto functions.
 */
public interface CryptoFacade {

    /**
     * Will perform AES encryption as per the parameters defined in the {@link AesEncryptRequest} object.
     * @param request the {@link AesEncryptRequest}.
     * @return an instance of populated {@link AesEncryptResponse}.
     */
    Mono<AesEncryptResponse> encrypt(AesEncryptRequest request);

    /**
     * Will perform AES decryption as per the parameters defined in the {@link AesDecryptRequest} object.
     * @param request the {@link AesDecryptRequest}.
     * @return an instance of populated {@link AesDecryptResponse}.
     */
    Mono<AesDecryptResponse> decrypt(AesDecryptRequest request);


    /**
     * Generates a Public/Private key pair and generates a self signed CSR containing the public key.
     * @param request the {@link GenerateCSRRequest} request.
     * @return response {@link GenerateCSRResponse} object containing the wrapped private key and certificate
     * signing request.
     */
    Mono<GenerateCSRResponse> generateCertificateSigningRequest(GenerateCSRRequest request);


    /**
     * Sign certificate signing request and generate an X509 certificate.
     * @param request {@link CertificateSigningRequest} containing the PKCS10 certificate signing request.
     * @return A certificate signing response containing the signed certificate.
     */
    Mono<CertificateSigningResponse> sign(CertificateSigningRequest request);

    /**
     * Will generate an AES key and store in the hsm.
     * @param request The request containing the key length and label to associate with the key.
     * @return An {@link AesCreateKeyResponse} instance containing the specifics of the key.
     */
    Mono<AesCreateKeyResponse> createKey(AesCreateKeyRequest request);
}
