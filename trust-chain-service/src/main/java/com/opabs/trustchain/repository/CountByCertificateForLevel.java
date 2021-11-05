package com.opabs.trustchain.repository;

public interface CountByCertificateForLevel {

    public Integer getCount();

    public byte[] getParentCertificateId();

}
