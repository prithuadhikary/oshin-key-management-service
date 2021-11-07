package com.opabs.tenant.management.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Data
@Entity
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
