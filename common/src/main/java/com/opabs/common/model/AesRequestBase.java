package com.opabs.common.model;

import com.opabs.common.enums.ModeOfOperation;
import lombok.Data;

import javax.validation.constraints.NotNull;

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

    /**
     * Key alias identifying the key to encrypt the message with.
     */
    @NotNull
    private String keyAlias;

}
