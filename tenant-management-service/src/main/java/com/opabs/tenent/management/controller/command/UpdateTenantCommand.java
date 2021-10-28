package com.opabs.tenent.management.controller.command;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Valid
public class UpdateTenantCommand {

    @NotEmpty
    private String name;

    @Valid
    @NotNull
    private ContactInfoDTO contactInfo;

}
