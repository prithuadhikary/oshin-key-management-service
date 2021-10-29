package com.opabs.trustchain.service;

import com.opabs.common.model.CertificateCountInfo;
import com.opabs.common.model.CertificateReportInfo;
import com.opabs.trustchain.exception.NotFoundException;
import com.opabs.trustchain.feign.TenantManagementService;
import com.opabs.trustchain.repository.CertificateRepository;
import com.opabs.trustchain.repository.CountByKeyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateReportService {

    private final TenantManagementService tenantManagementService;

    private final CertificateRepository certificateRepository;

    public CertificateReportInfo certificateReport(UUID tenantId) {
        //1. Check if tenant id is valid.
        //2. Fetch trustChains with tenantExtId matching the tenantId.
        //3. Add all certificate counts by keyType.

        validateTenantId(tenantId);

        List<CountByKeyType> certCountsByKeyType = certificateRepository.countByTenantExtId(tenantId);

        CertificateReportInfo info = new CertificateReportInfo();
        info.setCertificateCountInfos(new ArrayList<>());
        info.setTenantId(tenantId);
        certCountsByKeyType.forEach(countByKeyType -> {
            info.getCertificateCountInfos().add(new CertificateCountInfo(countByKeyType.getKeyType(), countByKeyType.getCount()));
            info.setTotalCertificateCount(info.getTotalCertificateCount() + countByKeyType.getCount());
        });
        return info;
    }

    private void validateTenantId(UUID tenantId) {
        try {
            tenantManagementService.getTenantInfo(tenantId);
        } catch (Exception ex) {
            log.error("Invalid tenant id found.", ex);
            throw new NotFoundException("tenant", tenantId);
        }
    }

}
