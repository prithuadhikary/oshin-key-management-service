package com.opabs.cryptoservice.util;

import com.opabs.common.enums.ModeOfOperation;
import com.opabs.common.model.AesDecryptRequest;
import com.opabs.common.model.AesEncryptRequest;
import com.opabs.cryptoservice.service.model.AesDecryptCommand;
import com.opabs.cryptoservice.service.model.AesEncryptCommand;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class TransformationUtils {

    public static AesEncryptCommand toCommand(AesEncryptRequest request) {
        AesEncryptCommand command = new AesEncryptCommand();
        Base64.Decoder decoder = Base64.getDecoder();
        if (StringUtils.isNotEmpty(request.getMessage())) {
            command.setMessage(decoder.decode(request.getMessage()));
        } else {
            command.setMessage("".getBytes(StandardCharsets.UTF_8));
        }
        if (StringUtils.isNotEmpty(request.getIv())) {
            command.setIv(decoder.decode(request.getIv()));
        } else {
            SecureRandom random = new SecureRandom();
            byte[] iv = new byte[16];
            random.nextBytes(iv);
            command.setIv(iv);
        }
        if (StringUtils.isNotEmpty(request.getAdditionalAuthData())) {
            command.setAdditionalAuthData(decoder.decode(request.getAdditionalAuthData()));
        }
        if (request.getTagLength() != null) {
            command.setTagLength(request.getTagLength());
        } else {
            command.setTagLength(16);
        }
        command.setModeOfOperation(request.getModeOfOperation());
        if (command.getModeOfOperation() == null) {
            command.setModeOfOperation(ModeOfOperation.CBC);
        }
        return command;
    }

    public static AesDecryptCommand toCommand(AesDecryptRequest request) {
        AesDecryptCommand command = new AesDecryptCommand();
        Base64.Decoder decoder = Base64.getDecoder();
        if (StringUtils.isNotEmpty(request.getCipher())) {
            command.setCipher(decoder.decode(request.getCipher()));
        } else {
            command.setCipher("".getBytes(StandardCharsets.UTF_8));
        }
        command.setIv(decoder.decode(request.getIv()));
        if (StringUtils.isNotEmpty(request.getAdditionalAuthData())) {
            command.setAdditionalAuthData(decoder.decode(request.getAdditionalAuthData()));
        }
        if (request.getTag() != null) {
            command.setTag(decoder.decode(request.getTag()));
        }
        command.setModeOfOperation(request.getModeOfOperation());
        if (command.getModeOfOperation() == null) {
            command.setModeOfOperation(ModeOfOperation.CBC);
        }
        return command;
    }

}
