package com.opabs.cryptoservice.service.model;

import com.opabs.common.enums.ModeOfOperation;
import lombok.Data;

@Data
public class AesDecryptCommand extends AesCommandBase {

    private byte[] cipher;

    private byte[] tag;

    private ModeOfOperation modeOfOperation;

}
