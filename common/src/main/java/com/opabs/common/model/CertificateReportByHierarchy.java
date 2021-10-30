package com.opabs.common.model;

import lombok.Data;

@Data
public class CertificateReportByHierarchy {

    private int totalCertificateCount;

    private int anchorCertificateCount;

    private int nonAnchorCertificateCount;

}
