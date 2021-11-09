package com.opabs.trustchain.repository;

import com.opabs.trustchain.domain.Certificate;
import com.opabs.trustchain.domain.TrustChain;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
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

    @Query(value = "SELECT count(c.id) as count, cast(c.parent_certificate_id as varchar) parentCertificateId from trust_chain.certificate c WHERE c.trust_chain_id = uuid(?1) group by c.parent_certificate_id", nativeQuery = true)
    List<CountByCertificateForLevel> findCountByCertForLevel(UUID trustChainId);

    @Query("SELECT count(c) from Certificate c WHERE c.trustChain.tenantExtId = ?1")
    Long countByTenantExtId(UUID tenantExtId);

    @Query(value = "SELECT " +
            "       DATE_TRUNC('month',date_issued) " +
            "         AS  date_issued, " +
            "       COUNT(id) AS count " +
            "FROM certificate " +
            "WHERE date_issued BETWEEN ?1 AND ?2 " +
            "GROUP BY DATE_TRUNC('month',date_issued);", nativeQuery = true)
    List<CountByMonth> issuedCountByMonthBetween(Date startDate, Date endDate);

    @Query(value = "SELECT " +
            "       DATE_TRUNC('month',date_issued)" +
            "         AS  date_issued," +
            "       COUNT(id) AS count " +
            "FROM certificate c " +
            "WHERE c.trust_chain_id = (SELECT tc.id from trust_chain tc where tenant_ext_id = ?1) AND " +
            "date_issued BETWEEN ?2 AND ?3 " +
            "GROUP BY DATE_TRUNC('month',date_issued);", nativeQuery = true)
    List<CountByMonth> issuedCountByMonthBetweenWithTenantId(UUID tenantId, Date startDate, Date endDate);
}
