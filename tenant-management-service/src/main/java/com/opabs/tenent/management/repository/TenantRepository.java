package com.opabs.tenent.management.repository;

import com.opabs.tenent.management.domain.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends PagingAndSortingRepository<Tenant, UUID> {

    Page<Tenant> findAllByDeleted(Boolean isDeleted, Pageable pageable);

    Optional<Tenant> findByIdAndDeleted(UUID id, boolean deleted);
}
