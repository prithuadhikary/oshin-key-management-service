package com.opabs.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * AES decrypt request containing the cipher text to decrypt
 * along with parameters such as iv, tag, cipher mode of operation
 * and additional authentication data.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AesDecryptRequest extends AesRequestBase {

    /**
     * The cipher text to decrypt using AES.
     */
    @NotNull
    private String cipher;

    /**
     * Optional tag required during the decryption process while using GCM mode of operation.
     */
    private String tag;

}
