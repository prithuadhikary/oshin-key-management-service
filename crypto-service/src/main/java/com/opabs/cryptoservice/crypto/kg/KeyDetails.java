package com.opabs.cryptoservice.crypto.kg;

import lombok.Data;

import java.security.Key;

@Data
public class KeyDetails {

    private long keyHandle;

    private String label;

    private Key secretKey;

}
