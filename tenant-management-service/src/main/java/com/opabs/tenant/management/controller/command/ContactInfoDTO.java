package com.opabs.tenant.management.controller.command;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ContactInfoDTO {

    private String phone;

    private String emailAddress;

    @Valid
    @Size(min = 1, max = 4)
    List<AddressDTO> addresses;

}
