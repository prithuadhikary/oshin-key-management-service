package com.opabs.trustchain.controller.model;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TrustChainModel {

    private UUID id;

    private String name;

    private String description;

    private boolean deleted;

    private CertificateModel rootCertificate;

    /**
     * A trust chain will belong to a tenant. The tenant is managed by the tenant management service.
     */
    private UUID tenantExtId;

    private OffsetDateTime dateCreated;

    private OffsetDateTime dateUpdated;

}
