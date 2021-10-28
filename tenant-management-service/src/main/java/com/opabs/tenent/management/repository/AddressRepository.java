package com.opabs.tenent.management.repository;

import com.opabs.tenent.management.domain.Address;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AddressRepository extends PagingAndSortingRepository<Address, UUID> {
}
