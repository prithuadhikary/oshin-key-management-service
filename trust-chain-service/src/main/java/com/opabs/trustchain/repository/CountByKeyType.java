package com.opabs.trustchain.repository;

import com.opabs.common.model.KeyType;

public interface CountByKeyType {

    KeyType getKeyType();

    Integer getCount();

}
