package com.opabs.trustchain.service;

import com.opabs.common.model.CertificateCountInfo;
import com.opabs.common.model.CertificateReportByHierarchy;
import com.opabs.common.model.CertificateReportByKeyType;
import com.opabs.trustchain.domain.TrustChain;
import com.opabs.trustchain.exception.NotFoundException;
import com.opabs.trustchain.feign.TenantManagementService;
import com.opabs.trustchain.repository.CertificateRepository;
import com.opabs.trustchain.repository.CountByHierarchy;
import com.opabs.trustchain.repository.CountByKeyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateReportService {

    private final TenantManagementService tenantManagementService;

    private final CertificateRepository certificateRepository;

    private final TrustChainService trustChainService;

    public CertificateReportByKeyType certificateReportByTenantId(UUID tenantId) {
        //1. Check if tenant id is valid.
        //2. Fetch trustChains with tenantExtId matching the tenantId.
        //3. Add all certificate counts by keyType.

        validateTenantId(tenantId);

        List<CountByKeyType> certCountsByKeyType = certificateRepository.countByTenantExtId(tenantId);

        return getCertificateReportInfo(certCountsByKeyType);
    }

    public CertificateReportByKeyType certificateReportByTrustChain(UUID trustChainId) {
        // 1. Validate trust chain id
        // 2. Calculate certificate count by key type for trust chain id.
        Optional<TrustChain> existing = trustChainService.findById(trustChainId);
        TrustChain trustChain = existing.orElseThrow(() -> new NotFoundException("trust chain", trustChainId));

        List<CountByKeyType> countByKeyTypes = certificateRepository.countByTrustChain(trustChain);

        return getCertificateReportInfo(countByKeyTypes);
    }

    public CertificateReportByHierarchy certificateReportByHierarchy(UUID trustChainId) {
        // 1. Validate trust chain id
        // 2. Calculate certificate count by root/non root for trust chain id.
        Optional<TrustChain> existing = trustChainService.findById(trustChainId);
        TrustChain trustChain = existing.orElseThrow(() -> new NotFoundException("trust chain", trustChainId));

        List<CountByHierarchy> countByHierarchies = certificateRepository.countByHierarchy(trustChain);

        CertificateReportByHierarchy reportByHierarchy = new CertificateReportByHierarchy();
        countByHierarchies.forEach(countByHierarchy -> {
            reportByHierarchy.setTotalCertificateCount(reportByHierarchy.getTotalCertificateCount() + countByHierarchy.getCount());
            if (countByHierarchy.getIsAnchor()) {
                reportByHierarchy.setAnchorCertificateCount(countByHierarchy.getCount());
            } else {
                reportByHierarchy.setNonAnchorCertificateCount(countByHierarchy.getCount());
            }
        });

        return reportByHierarchy;
    }

    private CertificateReportByKeyType getCertificateReportInfo(List<CountByKeyType> certCountsByKeyType) {
        CertificateReportByKeyType info = new CertificateReportByKeyType();
        info.setCertificateCountInfos(new ArrayList<>());
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
