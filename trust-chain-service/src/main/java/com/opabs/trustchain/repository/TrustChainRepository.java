package com.opabs.trustchain.repository;

import com.opabs.trustchain.domain.TrustChain;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrustChainRepository extends PagingAndSortingRepository<TrustChain, UUID> {
}
