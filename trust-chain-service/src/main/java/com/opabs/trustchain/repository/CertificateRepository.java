package com.opabs.trustchain.repository;

import com.opabs.trustchain.domain.Certificate;
import com.opabs.trustchain.domain.TrustChain;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CertificateRepository extends PagingAndSortingRepository<Certificate, UUID>, JpaSpecificationExecutor<Certificate> {

    @Query("SELECT c.keyType as keyType, count(c) as count from Certificate c WHERE c.trustChain.tenantExtId = ?1 group by c.keyType")
    List<CountByKeyType> countByTenantExtIdGroupByKeyType(UUID tenantId);

    @Query("SELECT c.keyType as keyType, count(c) as count from Certificate c WHERE c.trustChain = ?1 group by c.keyType")
    List<CountByKeyType> countByTrustChain(TrustChain trustChain);

    @Query("SELECT c.isAnchor as isAnchor, count(c) as count from Certificate c WHERE c.trustChain = ?1 group by c.isAnchor")
    List<CountByHierarchy> countByHierarchy(TrustChain trustChain);

    @Query(value = "SELECT count(c.id) as count, c.parent_certificate_id as parentCertificateId from Certificate c WHERE c.trust_chain_id = ?1 group by c.parent_certificate_id", nativeQuery = true)
    List<CountByCertificateForLevel> findCountByCertForLevel(UUID trustChainId);

    @Query("SELECT count(c) from Certificate c WHERE c.trustChain.tenantExtId = ?1")
    Long countByTenantExtId(UUID tenantExtId);
}
