package com.opabs.trustchain.controller;

import com.opabs.common.model.CertificateReportInfo;
import com.opabs.trustchain.service.CertificateReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("certificate-report")
public class CertificateReportController {

    private final CertificateReportService certificateReportService;

    @RequestMapping("{tenantId}")
    public ResponseEntity<CertificateReportInfo> certificateReport(@PathVariable("tenantId") UUID tenantId) {
        return ResponseEntity.ok(certificateReportService.certificateReport(tenantId));
    }

}
