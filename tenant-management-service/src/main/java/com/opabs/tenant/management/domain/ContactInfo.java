package com.opabs.tenant.management.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(exclude = "tenant")
@Entity
public class ContactInfo {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    private String phone;

    private String emailAddress;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contactInfo")
    private List<Address> addresses;

    @JsonIgnore
    @OneToOne
    private Tenant tenant;

}
