package com.opabs.common.model;

import lombok.Data;

import java.util.List;

@Data
public class CertificateReportByKeyType {

    private int totalCertificateCount;

    private List<CertificateCountInfo> certificateCountInfos;

}
