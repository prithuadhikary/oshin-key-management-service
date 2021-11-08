package com.opabs.trustchain.controller;

import com.opabs.trustchain.controller.model.TrustChainCount;
import com.opabs.trustchain.service.TrustChainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("trust-chain-report")
public class TrustChainReportController {

    private final TrustChainService trustChainService;

    @GetMapping("total")
    public ResponseEntity<TrustChainCount> total(Principal userPrincipal) {
        return ResponseEntity.ok(trustChainService.count(userPrincipal));
    }

}
