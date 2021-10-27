package com.opabs.common.model;

import lombok.Data;

/**
 * The AES decryption response containing the plain text.
 */
@Data
public class AesDecryptResponse {

    /**
     * The plain text message obtained after the AES decryption process.
     */
    private String message;

}
