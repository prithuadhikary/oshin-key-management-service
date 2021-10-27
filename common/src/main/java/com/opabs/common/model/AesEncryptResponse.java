package com.opabs.common.model;

import lombok.Data;

/**
 * Response containing the resultant ciphertext after encryption,
 * the initialization vector used during the encryption
 * and an optional tag generated during encryption using GCM.
 */
@Data
public class AesEncryptResponse {

    /**
     * The resultant ciphertext after encryption using AES.
     */
    private String cipher;

    /**
     * The initialization vector used during the encryption process.
     */
    private String iv;

    /**
     * Optional tag generated during encryption using GCM mode of operation.
     */
    private String tag;

}
