package com.opabs.trustchain.controller.responses;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateCertificateResponse {

    private UUID id;

    private String certificate;

    private UUID trustChainId;

    private UUID parentCertificateId;

    private String publicKeyFingerprint;

    private String certificateFingerprint;

}
