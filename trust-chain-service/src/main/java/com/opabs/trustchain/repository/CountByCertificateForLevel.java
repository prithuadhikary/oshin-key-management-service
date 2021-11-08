package com.opabs.trustchain.repository;

import java.util.UUID;

public interface CountByCertificateForLevel {

    public Integer getCount();

    public UUID getParentCertificateId();

}
