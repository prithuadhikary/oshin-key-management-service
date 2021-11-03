package com.opabs.trustchain.domain.specifications;

import com.opabs.trustchain.domain.Certificate;
import com.opabs.trustchain.domain.Certificate_;
import org.springframework.data.jpa.domain.Specification;

public class CertificateSpecifications {

    public static Specification<Certificate> searchSpecification(String searchText) {
        return (root, query, criteriaBuilder) ->
        {
            String lowerSearchText = searchText.toLowerCase();
            return criteriaBuilder.or(
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
            );
        };
    }

}
