package com.opabs.common.model;

import com.opabs.common.enums.ModeOfOperation;
import lombok.Data;

@Data
public class AesRequestBase {

    /**
     * The mode of operation to use during encryption.
     */
    private ModeOfOperation modeOfOperation;

    /**
     * Used in the GCM and CBC modes of cipher operation.
     */
    private String iv;

    /**
     * Additional authentication data for GCM mode. Optional parameter.
     */
    private String additionalAuthData;

}
