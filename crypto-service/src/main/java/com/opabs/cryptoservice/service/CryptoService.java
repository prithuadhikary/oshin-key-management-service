package com.opabs.cryptoservice.service;

import com.opabs.common.model.*;
import com.opabs.cryptoservice.facade.CryptoFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CryptoService {

    private final CryptoFacade cryptoFacade;

    public Mono<AesEncryptResponse> encrypt(AesEncryptRequest request) {
        return cryptoFacade.encrypt(request);
    }

    public Mono<AesDecryptResponse> decrypt(AesDecryptRequest request) {
        return cryptoFacade.decrypt(request);
    }

    public Mono<GenerateCSRResponse> generatePrivateKeyAndCSR(GenerateCSRRequest request) {
        return cryptoFacade.generateCertificateSigningRequest(request);
    }

    public Mono<CertificateSigningResponse> signCSR(CertificateSigningRequest request) {
        return cryptoFacade.sign(request);
    }
}
