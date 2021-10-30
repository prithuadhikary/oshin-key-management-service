package com.opabs.trustchain.model;

import lombok.Data;

import java.util.Date;

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

}
