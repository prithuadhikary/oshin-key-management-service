package com.opabs.cryptoservice.facade;

import com.opabs.common.model.*;
import com.opabs.cryptoservice.service.aes.AesService;
import com.opabs.cryptoservice.service.certificate.CertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultCryptoFacade implements CryptoFacade {

    private final CertificateService certificateService;

    private final AesService aesService;

    @Override
    public Mono<AesEncryptResponse> encrypt(AesEncryptRequest request) {
        return aesService.encrypt(request);
    }

    @Override
    public Mono<AesDecryptResponse> decrypt(AesDecryptRequest request) {
        return aesService.decrypt(request);
    }

    @Override
    public Mono<GenerateCSRResponse> generateCertificateSigningRequest(GenerateCSRRequest request) {
        return certificateService.generateCertificateSigningRequest(request);
    }

    @Override
    public Mono<CertificateSigningResponse> sign(CertificateSigningRequest request) {
        return certificateService.sign(request);
    }

    @Override
    public Mono<AesCreateKeyResponse> createKey(AesCreateKeyRequest request) {
        return aesService.createKey(request.getKeySize(), request.getLabel());
    }

}
