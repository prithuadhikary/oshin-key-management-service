package com.opabs.trustchain.repository;

import com.opabs.trustchain.domain.TrustChain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrustChainRepository extends PagingAndSortingRepository<TrustChain, UUID> {

    Page<TrustChain> findAllByDeleted(boolean b, PageRequest pageRequest);

    Optional<TrustChain> findByIdAndDeleted(UUID id, boolean b);
}
