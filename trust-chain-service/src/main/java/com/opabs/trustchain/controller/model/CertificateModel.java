package com.opabs.trustchain.controller.model;

import com.opabs.common.enums.KeyUsages;
import com.opabs.common.model.KeyType;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CertificateModel {

    private UUID id;

    private String subjectDistinguishedName;

    private String issuerDistinguishedName;

    private List<KeyUsages> keyUsages;

    private boolean isAnchor;

    private String publicKeyFingerprint;

    private String certificateFingerprint;

    private UUID parentCertificateId;

    private UUID trustChainId;

    private KeyType keyType;

    private Integer keyLength;

    private OffsetDateTime dateCreated;

    private OffsetDateTime dateUpdated;

    private OffsetDateTime validFrom;

    private OffsetDateTime validUpto;

    private boolean isExpired;

    private boolean notYetValid;

    private String namedCurve;

    private Integer pathLengthConstraint;

    private Integer serial;

}
