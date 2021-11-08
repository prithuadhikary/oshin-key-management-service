package com.opabs.tenant.management.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@Table(schema = "tenant")
public class Tenant {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    private String name;

    private boolean deleted;

    @CreationTimestamp
    private OffsetDateTime dateCreated;

    @UpdateTimestamp
    private OffsetDateTime dateUpdated;

    @OneToOne(mappedBy = "tenant")
    private ContactInfo contactInfo;

}
