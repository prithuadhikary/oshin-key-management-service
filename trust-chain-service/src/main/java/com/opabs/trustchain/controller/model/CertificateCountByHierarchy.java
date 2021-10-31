package com.opabs.trustchain.controller.model;

import lombok.Data;

import java.util.List;

@Data
public class CertificateCountByHierarchy {

    private List<CertificateCountByLevel> countsByLevel;

    private int totalCount;

}
