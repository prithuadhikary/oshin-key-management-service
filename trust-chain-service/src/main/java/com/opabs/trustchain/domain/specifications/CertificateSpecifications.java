package com.opabs.trustchain.domain.specifications;

import com.opabs.trustchain.domain.Certificate;
import com.opabs.trustchain.domain.Certificate_;
import com.opabs.trustchain.domain.TrustChain;
import com.opabs.trustchain.domain.TrustChain_;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CertificateSpecifications {

    public static Specification<Certificate> searchSpecification(String searchText, UUID parentCertificateId, UUID tenantId) {
        return (root, query, criteriaBuilder) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(searchText)) {
                String lowerSearchText = searchText.toLowerCase();
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get(Certificate_.certificateFingerprint)),
                                "%" + lowerSearchText + "%"
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get(Certificate_.publicKeyFingerprint)),
                                "%" + lowerSearchText + "%"
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get(Certificate_.subjectDistinguishedName)),
                                "%" + lowerSearchText + "%"
                        )
                ));
            }
            if (tenantId != null) {
                Join<Certificate, TrustChain> trustChainJoin = root.join(Certificate_.trustChain);
                predicates.add(criteriaBuilder.equal(trustChainJoin.get(TrustChain_.tenantExtId), tenantId));
            }
            if (parentCertificateId != null) {
                Join<Certificate, Certificate> joinParent = root.join(Certificate_.parentCertificate);
                predicates.add(criteriaBuilder.equal(joinParent.get(Certificate_.id), parentCertificateId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
