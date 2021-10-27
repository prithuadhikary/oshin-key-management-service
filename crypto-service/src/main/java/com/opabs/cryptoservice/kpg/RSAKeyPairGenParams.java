package com.opabs.cryptoservice.kpg;

import lombok.Data;

@Data
public class RSAKeyPairGenParams extends KeyPairGenParams {

    private Integer keySize;

}
