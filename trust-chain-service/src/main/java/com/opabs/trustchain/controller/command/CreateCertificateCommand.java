package com.opabs.trustchain.controller.command;

import com.opabs.common.enums.KeyUsages;
import com.opabs.common.model.SigningAlgorithm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateCertificateCommand extends GenerateCSRBase {

    private UUID parentCertificateId;

    private String subjectDistinguishedName;

    private List<KeyUsages> keyUsages;

    private Integer validityInYears;

    private OffsetDateTime validFrom;

    private SigningAlgorithm signatureAlgorithm;

}
