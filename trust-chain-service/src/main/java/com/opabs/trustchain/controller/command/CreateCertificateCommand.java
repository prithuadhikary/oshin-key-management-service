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
public class CreateCertificateCommand extends GenerateCSRBase {

    @NotNull
    private UUID parentCertificateId;

    @NotEmpty
    private List<KeyUsages> keyUsages;

    @NotNull
    private Integer validityInYears;

    @NotNull
    private OffsetDateTime validFrom;

    @NotNull
    private SigningAlgorithm signatureAlgorithm;

}
