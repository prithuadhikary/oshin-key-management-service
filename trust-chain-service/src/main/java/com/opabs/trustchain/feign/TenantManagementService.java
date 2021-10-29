package com.opabs.trustchain.feign;

import com.opabs.trustchain.model.TenantInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface TenantManagementService {

    @GetMapping("/api/tenant-management-service/tenant/{id}")
    TenantInfo getTenantInfo(@PathVariable("id") UUID id);

}
