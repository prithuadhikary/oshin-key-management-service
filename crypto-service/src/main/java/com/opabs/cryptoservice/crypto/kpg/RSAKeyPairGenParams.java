package com.opabs.cryptoservice.crypto.kpg;

import lombok.Data;

@Data
public class RSAKeyPairGenParams extends KeyPairGenParams {

    private Integer keySize;

}
