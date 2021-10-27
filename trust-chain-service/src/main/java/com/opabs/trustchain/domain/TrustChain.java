package com.opabs.trustchain.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.opabs.trustchain.views.TrustChainViews;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.UUID;

@Data
@Entity
@JsonView(TrustChainViews.CertificateWithoutTrustChain.class)
public class TrustChain {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    private String name;

    private String description;

    @OneToOne
    private Certificate rootCertificate;

}
