package com.opabs.trustchain.model;

import lombok.Data;

@Data
public class CertificateInfo {

    private String publicKeyFingerprint;

    private String certificateFingerprint;

}
