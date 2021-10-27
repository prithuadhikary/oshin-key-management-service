package com.opabs.trustchain.exception;

import com.opabs.common.enums.KeyUsages;
import com.opabs.common.model.KeyType;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

public class KeyTypeAndUsageMismatch extends BadRequestException {

    public KeyTypeAndUsageMismatch(KeyType targetKeyType, List<KeyUsages> unsupportedKeyUsage) {
        super(
                MessageFormat.format("The key type:{0}, does not support usages: {1}", targetKeyType,
                        unsupportedKeyUsage.stream().map(KeyUsages::name).collect(Collectors.joining(", ")))
                , ErrorCode.KEY_TYPE_USAGE_MISMATCH);
    }
}
