package com.opabs.trustchain.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile("!cloud")
@FeignClient(value = "tenant-management-service", url = "http://localhost:8085")
public interface TenantManagementServiceDev extends TenantManagementService {
}
