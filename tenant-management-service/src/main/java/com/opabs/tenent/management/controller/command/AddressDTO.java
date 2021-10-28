package com.opabs.tenent.management.controller.command;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddressDTO {

    @NotEmpty
    private String addressLine1;

    private String addressLine2;

    @NotEmpty
    private String city;

    @NotEmpty
    private String stateOrProvince;

    @NotEmpty
    private String country;

    @NotEmpty
    private String zipCode;

}
