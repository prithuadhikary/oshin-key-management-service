package com.opabs.common.model;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CertificateReportInfo {

    private UUID tenantId;

    private int totalCertificateCount;

    private List<CertificateCountInfo> certificateCountInfos;

}
