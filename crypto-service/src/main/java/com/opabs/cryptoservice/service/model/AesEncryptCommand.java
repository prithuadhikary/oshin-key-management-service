package com.opabs.cryptoservice.service.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AesEncryptCommand extends AesCommandBase {

    private byte[] message;

    private Integer tagLength;

    public Integer getTagLength() {
        return tagLength;
    }

    public void setTagLength(Integer tagLength) {
        this.tagLength = tagLength;
    }
}
