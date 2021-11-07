package com.opabs.trustchain.exception;

public class InvalidTenantIdException extends BadRequestException {

    public InvalidTenantIdException() {
        super("Tenant Id does not match the user's tenant's id.", ErrorCode.TENANT_ID_MISMATCH);
    }
}
