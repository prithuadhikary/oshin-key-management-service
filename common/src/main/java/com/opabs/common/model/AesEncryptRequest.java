package com.opabs.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * AES encrypt request containing the plaintext/message
 * to encrypt along with other parameters such as mode
 * of operation, initialization vector, tag length
 * and additional authentication data.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AesEncryptRequest extends AesRequestBase {

    /**
     * Base64 encoded message to encrypt.
     */
    @NotNull
    private String message;


    /**
     * Optional field for AES GCM. Determines the length of tag(in bytes) generated during GCM based encryption.
     */
    private Integer tagLength = 16;

}
