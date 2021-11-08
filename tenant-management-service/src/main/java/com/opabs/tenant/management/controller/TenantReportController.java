package com.opabs.tenant.management.controller;

import com.opabs.common.security.Permissions;
import com.opabs.tenant.management.controller.response.TenantCountResponse;
import com.opabs.tenant.management.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tenant-report")
@RequiredArgsConstructor
public class TenantReportController {

    private final TenantService tenantService;

    @Secured(Permissions.TENANT_REPORT_VIEW)
    @GetMapping("total")
    public TenantCountResponse total() {
        return tenantService.count();
    }
}
