package com.opabs.trustchain.repository;

import com.opabs.trustchain.domain.Certificate;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CertificateRepository extends PagingAndSortingRepository<Certificate, UUID> {
}
