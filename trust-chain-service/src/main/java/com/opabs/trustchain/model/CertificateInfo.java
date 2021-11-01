package com.opabs.trustchain.model;

import com.opabs.common.enums.KeyUsages;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CertificateInfo {

    private String subjectDistinguishedName;

    private String issuerDistinguishedName;

    private String publicKeyFingerprint;

    private String certificateFingerprint;

    private Date validFrom;

    private Date validUpto;

    private boolean isExpired;

    private boolean notYetValid;

    private List<KeyUsages> keyUsages;

}
