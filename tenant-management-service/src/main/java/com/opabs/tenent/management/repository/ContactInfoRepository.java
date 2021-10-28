package com.opabs.tenent.management.repository;

import com.opabs.tenent.management.domain.ContactInfo;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContactInfoRepository extends PagingAndSortingRepository<ContactInfo, UUID> {
}
