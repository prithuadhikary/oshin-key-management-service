package com.opabs.tenent.management.repository;

import com.opabs.tenent.management.domain.Tenant;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TenantRepository extends PagingAndSortingRepository<Tenant, UUID> {
}
