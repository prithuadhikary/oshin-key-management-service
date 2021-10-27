package com.opabs.trustchain.controller.command;

import com.opabs.common.enums.KeyUsages;
import com.opabs.common.model.SigningAlgorithm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateTrustChainCommand extends GenerateCSRBase {

    private String name;

    private String description;

    private OffsetDateTime validFrom;

    private Integer validityInYears;

    private List<KeyUsages> keyUsages;

    private SigningAlgorithm signatureAlgorithm;

}
