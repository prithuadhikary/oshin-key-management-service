package com.opabs.trustchain.controller;

import com.opabs.common.security.Permissions;
import com.opabs.trustchain.controller.responses.TrustChainCountResponse;
import com.opabs.trustchain.service.TrustChainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("trust-chain-report")
public class TrustChainReportController {

    private final TrustChainService trustChainService;

    @Secured(Permissions.TRUST_CHAIN_REPORT_VIEW)
    @GetMapping("total")
    public ResponseEntity<TrustChainCountResponse> total(Principal userPrincipal) {
        return ResponseEntity.ok(trustChainService.count(userPrincipal));
    }

}
