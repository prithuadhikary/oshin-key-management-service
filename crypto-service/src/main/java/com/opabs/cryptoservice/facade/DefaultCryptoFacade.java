package com.opabs.cryptoservice.facade;

import com.opabs.common.model.*;
import com.opabs.cryptoservice.service.aes.MockAesService;
import com.opabs.cryptoservice.service.certificate.MockCertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Profile("local")
@Component
@RequiredArgsConstructor
public class DefaultCryptoFacade implements CryptoFacade {

    private final MockCertificateService certificateService;

    private final MockAesService aesService;

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

}
