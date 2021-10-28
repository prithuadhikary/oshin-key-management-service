package com.opabs.trustchain.controller.command;

import com.opabs.common.enums.KeyUsages;
import com.opabs.common.model.SigningAlgorithm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateTrustChainCommand extends GenerateCSRBase {

    @NotEmpty
    private String name;

    private String description;

    @NotNull
    private OffsetDateTime validFrom;

    @NotNull
    private UUID tenantExtId;

    @NotNull
    private Integer validityInYears;

    @NotEmpty
    private List<KeyUsages> keyUsages;

    @NotNull
    private SigningAlgorithm signatureAlgorithm;

}
