package com.opabs.common.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AesCreateKeyRequest {

    @NotNull
    private Integer keySize;

    @NotEmpty
    private String label;

}
