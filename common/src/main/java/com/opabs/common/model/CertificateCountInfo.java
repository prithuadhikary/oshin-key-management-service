package com.opabs.common.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CertificateCountInfo {

    private final KeyType keyType;

    private final Integer certificateCount;

}
