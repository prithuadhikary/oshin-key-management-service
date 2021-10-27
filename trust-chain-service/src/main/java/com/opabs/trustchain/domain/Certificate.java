package com.opabs.trustchain.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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
    private byte[] wrappedPrivateKey;

    private boolean isAnchor;

    private String publicKeyFingerprint;

    private String certificateFingerprint;

    @OneToOne
    private Certificate parentCertificate;

    @OneToOne
    private TrustChain trustChain;

}
