package com.opabs.trustchain.controller.model;

import com.opabs.common.model.CertificateCountInfo;
import lombok.Data;

import java.util.List;

@Data
public class CertificateReportByKeyType {

    private int totalCertificateCount;

    private List<CertificateCountInfo> certificateCountInfos;

}
