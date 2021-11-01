package com.opabs.trustchain.exception;

public class ParentKeyUsageInvalidException extends BadRequestException {

    public ParentKeyUsageInvalidException() {
        super("Parent certificate's key usages do not contain 'Key Cert Sign'.", ErrorCode.PARENT_KEY_USAGE_INVALID);
    }

}
