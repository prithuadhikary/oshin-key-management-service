package com.opabs.trustchain.service;

import com.opabs.common.model.CertificateCountInfo;
import com.opabs.common.security.JWTAuthToken;
import com.opabs.trustchain.controller.model.CertificateCount;
import com.opabs.trustchain.controller.model.CertificateCountByHierarchy;
import com.opabs.trustchain.controller.model.CertificateCountByLevel;
import com.opabs.trustchain.controller.model.CertificateReportByKeyType;
import com.opabs.trustchain.domain.Certificate;
import com.opabs.trustchain.domain.TrustChain;
import com.opabs.trustchain.exception.InternalServerErrorException;
import com.opabs.trustchain.exception.NotFoundException;
import com.opabs.trustchain.feign.TenantManagementService;
import com.opabs.trustchain.repository.CertificateRepository;
import com.opabs.trustchain.repository.CountByCertificateForLevel;
import com.opabs.trustchain.repository.CountByKeyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.security.Principal;
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

        List<CountByKeyType> certCountsByKeyType = certificateRepository.countByTenantExtIdGroupByKeyType(tenantId);

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

    public CertificateCountByHierarchy certificateReportByHierarchy(UUID trustChainId) {
        Optional<TrustChain> existing = trustChainService.findById(trustChainId);
        TrustChain trustChain = existing.orElseThrow(() -> new NotFoundException("trust chain", trustChainId));

        List<CountByCertificateForLevel> certificateCountsByParentCert = certificateRepository.findCountByCertForLevel(trustChain.getId());

        List<CertificateCountByLevel> countByLevels = new ArrayList<>();
        CertificateCountByHierarchy countByHierarchy = new CertificateCountByHierarchy();
        countByHierarchy.setCountsByLevel(countByLevels);
        certificateCountsByParentCert.forEach(countByParentCert -> {
            Optional<Certificate> parentCertificateOpt = Optional.empty();
            if (countByParentCert.getParentCertificateId() != null) {
                parentCertificateOpt = certificateRepository.findById(countByParentCert.getParentCertificateId());
            }
            CertificateCountByLevel countByLevel = new CertificateCountByLevel();
            countByLevel.setCount(countByParentCert.getCount());
            if (parentCertificateOpt.isEmpty()) {
                countByLevel.setLevel("Level 0");
            } else {
                countByLevel.setLevel("Level " + findLevel(parentCertificateOpt.get()));
            }
            countByLevels.add(countByLevel);
            countByHierarchy.setTotalCount(countByHierarchy.getTotalCount() + countByParentCert.getCount());
        });

        return countByHierarchy;
    }

    private int findLevel(Certificate certificate) {
        return levelOf(certificate, 1);
    }

    private int levelOf(Certificate certificate, int level) {
        if (certificate.isAnchor() || certificate.getParentCertificate() == null)
            return level;
        else {
            return levelOf(certificate.getParentCertificate(), level + 1);
        }
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

    public CertificateCount certificateCount(Principal userPrincipal) {
        if (userPrincipal instanceof JWTAuthToken) {
            JWTAuthToken user = (JWTAuthToken) userPrincipal;
            CertificateCount count = new CertificateCount();
            switch (user.getGroup()) {
                case OPABS_ADMIN:
                    count.setTotal(certificateRepository.count());
                    break;
                case TENANT_ADMIN:
                    count.setTotal(certificateRepository.countByTenantExtId(user.getAccessToken().getTenantIdentifier()));
                    break;
            }
            return count;
        } else {
            throw new InternalServerErrorException();
        }
    }
}
