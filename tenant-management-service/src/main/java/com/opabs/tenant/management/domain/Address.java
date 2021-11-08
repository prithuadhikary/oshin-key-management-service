package com.opabs.tenant.management.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(schema = "tenant")
public class Address {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String stateOrProvince;

    private String country;

    private String zipCode;

    @JsonIgnore
    @ManyToOne
    private ContactInfo contactInfo;

}
