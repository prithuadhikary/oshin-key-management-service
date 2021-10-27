package com.opabs.cryptoservice.controller;

import com.opabs.common.model.CertificateSigningRequest;
import com.opabs.common.model.CertificateSigningResponse;
import com.opabs.common.model.GenerateCSRRequest;
import com.opabs.common.model.GenerateCSRResponse;
import com.opabs.cryptoservice.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("certificate")
@RequiredArgsConstructor
public class CertificateController {

    private final CryptoService cryptoService;

    @PostMapping("generate-csr")
    public Mono<GenerateCSRResponse> generatePrivateKeyAndCSR(@RequestBody @Validated GenerateCSRRequest request) {
        return cryptoService.generatePrivateKeyAndCSR(request);
    }

    @PostMapping("sign")
    public Mono<CertificateSigningResponse> signCSR(@RequestBody @Validated CertificateSigningRequest request) {
        return cryptoService.signCSR(request);
    }

}
