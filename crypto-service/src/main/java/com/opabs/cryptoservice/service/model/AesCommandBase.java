package com.opabs.cryptoservice.service.model;

import com.opabs.common.enums.ModeOfOperation;
import lombok.Data;

@Data
public class AesCommandBase {

    private ModeOfOperation modeOfOperation;

    private byte[] additionalAuthData;

    private byte[] iv;

}
