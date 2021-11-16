package com.opabs.tenant.management.repository;

import com.opabs.tenant.management.domain.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends PagingAndSortingRepository<Tenant, UUID>, JpaSpecificationExecutor<Tenant> {

    Page<Tenant> findAllByDeleted(Boolean isDeleted, Pageable pageable);

    Page<Tenant> findAllByDeletedAndId(Boolean isDeleted, UUID id, Pageable pageable);

    Optional<Tenant> findByIdAndDeleted(UUID id, boolean deleted);

    Long countByDeleted(Boolean isDeleted);
}
