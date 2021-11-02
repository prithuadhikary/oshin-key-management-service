package com.opabs.trustchain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicKeyInfo {

    private int keyLength;

    private String namedCurve;

}
