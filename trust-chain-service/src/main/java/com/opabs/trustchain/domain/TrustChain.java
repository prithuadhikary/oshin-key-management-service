package com.opabs.trustchain.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.opabs.trustchain.views.TrustChainViews;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
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

    @NotEmpty
    private String name;

    private String description;

    private boolean deleted;

    @OneToOne
    private Certificate rootCertificate;

    /**
     * A trust chain will belong to a tenant. The tenant is managed by the trust management service.
     */
    @NotNull
    private UUID tenantExtId;

    @CreationTimestamp
    private OffsetDateTime dateCreated;

    @UpdateTimestamp
    private OffsetDateTime dateUpdated;

}
