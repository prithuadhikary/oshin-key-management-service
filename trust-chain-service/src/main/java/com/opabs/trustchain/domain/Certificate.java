package com.opabs.trustchain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.opabs.common.model.KeyType;
import com.opabs.trustchain.views.TrustChainViews;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
public class Certificate {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @JsonView(TrustChainViews.CertificateWithoutTrustChain.class)
    private UUID id;

    @Lob
    private byte[] content;

    @Lob
    @JsonIgnore
    private byte[] wrappedPrivateKey;

    private boolean isAnchor;

    //TODO: save public key fingerprint and certificate fingerprint.
    private String publicKeyFingerprint;

    private String certificateFingerprint;

    @OneToOne
    private Certificate parentCertificate;

    @OneToOne
    private TrustChain trustChain;

    @Enumerated(EnumType.STRING)
    private KeyType keyType;

    @CreationTimestamp
    private OffsetDateTime dateCreated;

    @UpdateTimestamp
    private OffsetDateTime dateUpdated;

}
