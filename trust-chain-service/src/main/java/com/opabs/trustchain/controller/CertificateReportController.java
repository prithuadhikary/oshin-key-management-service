package com.opabs.trustchain.controller;

import com.opabs.common.security.Permissions;
import com.opabs.trustchain.controller.model.CertificateCount;
import com.opabs.trustchain.controller.model.CertificateCountByHierarchy;
import com.opabs.trustchain.controller.model.CertificateReportByKeyType;
import com.opabs.trustchain.controller.responses.CountByMonthResponse;
import com.opabs.trustchain.service.CertificateReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("certificate-report")
public class CertificateReportController {

    private final CertificateReportService certificateReportService;

    @Secured(Permissions.CERTIFICATE_REPORT_VIEW)
    @GetMapping("by-key-type")
    public ResponseEntity<CertificateReportByKeyType> certificateReportByKeyType(Principal userPrincipal) {
        return ResponseEntity.ok(certificateReportService.certificateReportByKeyType(userPrincipal));
    }

    @Secured(Permissions.CERTIFICATE_REPORT_VIEW)
    @GetMapping("by-tenant/{tenantId}")
    public ResponseEntity<CertificateReportByKeyType> certificateReportByKeyTypeForTenant(@PathVariable("tenantId") UUID tenantId) {
        return ResponseEntity.ok(certificateReportService.certificateReportByTenantId(tenantId));
    }

    @Secured(Permissions.CERTIFICATE_REPORT_VIEW)
    @GetMapping("by-trust-chain/{trustChainId}")
    public ResponseEntity<CertificateReportByKeyType> certificateReportByTrustChainForTrustChain(@PathVariable("trustChainId") UUID trustChainId) {
        return ResponseEntity.ok(certificateReportService.certificateReportByTrustChain(trustChainId));
    }

    @Secured(Permissions.CERTIFICATE_REPORT_VIEW)
    @GetMapping("by-hierarchy/{trustChainId}")
    public ResponseEntity<CertificateCountByHierarchy> certificateReportByHierarchy(@PathVariable("trustChainId") UUID trustChainId) {
        return ResponseEntity.ok(certificateReportService.certificateReportByHierarchy(trustChainId));
    }

    @Secured(Permissions.CERTIFICATE_REPORT_VIEW)
    @GetMapping("total")
    public ResponseEntity<CertificateCount> total(Principal userPrincipal) {
        return ResponseEntity.ok(certificateReportService.certificateCount(userPrincipal));
    }

    @Secured(Permissions.CERTIFICATE_REPORT_VIEW)
    @GetMapping("count-by-month")
    public ResponseEntity<List<CountByMonthResponse>> countByMonthBetween(Principal userPrincipal,
                                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
                                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate
    ) {
        return ResponseEntity.ok(certificateReportService.countByMonth(userPrincipal, startDate, endDate));
    }

}
