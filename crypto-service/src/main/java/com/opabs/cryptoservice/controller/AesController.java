package com.opabs.cryptoservice.controller;

import com.opabs.common.model.AesDecryptRequest;
import com.opabs.common.model.AesDecryptResponse;
import com.opabs.common.model.AesEncryptRequest;
import com.opabs.common.model.AesEncryptResponse;
import com.opabs.cryptoservice.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("aes")
public class AesController {

    @Autowired
    private CryptoService cryptoService;

    @PostMapping("encrypt")
    public Mono<AesEncryptResponse> aesEncrypt(@RequestBody @Validated AesEncryptRequest encryptRequest) {
        return cryptoService.encrypt(encryptRequest);
    }

    @PostMapping("decrypt")
    public Mono<AesDecryptResponse> aesDecrypt(@RequestBody @Validated AesDecryptRequest decryptRequest) {
        return cryptoService.decrypt(decryptRequest);
    }

}
