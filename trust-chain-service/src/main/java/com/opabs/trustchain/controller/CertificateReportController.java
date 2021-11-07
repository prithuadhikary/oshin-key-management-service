package com.opabs.trustchain.controller;

import com.opabs.common.security.Permissions;
import com.opabs.trustchain.controller.model.CertificateCountByHierarchy;
import com.opabs.trustchain.controller.model.CertificateReportByKeyType;
import com.opabs.trustchain.service.CertificateReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("certificate-report")
public class CertificateReportController {

    private final CertificateReportService certificateReportService;

    @Secured(Permissions.CERTIFICATE_REPORT_VIEW)
    @RequestMapping("by-tenant/{tenantId}")
    public ResponseEntity<CertificateReportByKeyType> certificateReportByTenant(@PathVariable("tenantId") UUID tenantId) {
        return ResponseEntity.ok(certificateReportService.certificateReportByTenantId(tenantId));
    }

    @Secured(Permissions.CERTIFICATE_REPORT_VIEW)
    @RequestMapping("by-trust-chain/{trustChainId}")
    public ResponseEntity<CertificateReportByKeyType> certificateReportByTrustChain(@PathVariable("trustChainId") UUID trustChainId) {
        return ResponseEntity.ok(certificateReportService.certificateReportByTrustChain(trustChainId));
    }

    @Secured(Permissions.CERTIFICATE_REPORT_VIEW)
    @RequestMapping("by-hierarchy/{trustChainId}")
    public ResponseEntity<CertificateCountByHierarchy> certificateReportByHierarchy(@PathVariable("trustChainId") UUID trustChainId) {
        return ResponseEntity.ok(certificateReportService.certificateReportByHierarchy(trustChainId));
    }

}
