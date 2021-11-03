package com.opabs.trustchain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opabs.common.model.KeyType;
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
    private UUID id;

    @Lob
    private byte[] content;

    @Lob
    @JsonIgnore
    private byte[] wrappedPrivateKey;

    private boolean isAnchor;

    private String subjectDistinguishedName;

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
