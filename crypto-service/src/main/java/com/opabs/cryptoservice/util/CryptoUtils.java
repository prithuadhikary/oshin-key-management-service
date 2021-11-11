package com.opabs.cryptoservice.util;

import com.cavium.cfm2.CFM2Exception;
import com.cavium.cfm2.Util;
import com.cavium.key.CaviumKey;
import com.opabs.common.enums.ModeOfOperation;
import com.opabs.common.model.AesEncryptRequest;
import com.opabs.common.model.AesRequestBase;
import com.opabs.cryptoservice.exception.BadRequestException;
import com.opabs.cryptoservice.exception.ErrorCode;
import com.opabs.cryptoservice.service.model.AesCommandBase;
import com.opabs.cryptoservice.service.model.AesDecryptCommand;
import com.opabs.cryptoservice.service.model.AesEncryptCommand;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Enumeration;

public class CryptoUtils {

    public static ModeOfOperation getModeOfOperation(AesCommandBase request) {
        ModeOfOperation mode = request.getModeOfOperation();
        if (request.getModeOfOperation() == null) {
            mode = ModeOfOperation.CBC;
        }
        return mode;
    }

    public static AlgorithmParameterSpec getAlgorithmParameterSpec(Integer tagLength, ModeOfOperation modeOfOperation, byte[] iv) {
        AlgorithmParameterSpec parameterSpec;
        if (ModeOfOperation.GCM.equals(modeOfOperation)) {
            if (tagLength == null) {
                tagLength = 16;
            }
            parameterSpec = new GCMParameterSpec(tagLength * 8, iv);
        } else {
            parameterSpec = new IvParameterSpec(iv);
        }
        return parameterSpec;
    }

    public static byte[] getCipherText(boolean isGcmMode, AesDecryptCommand command) {
        if (isGcmMode) {
            byte[] cipherTextFinal = new byte[command.getCipher().length + command.getTag().length];
            System.arraycopy(command.getCipher(), 0, cipherTextFinal, 0, command.getCipher().length);
            System.arraycopy(command.getTag(), 0, cipherTextFinal, command.getCipher().length, command.getTag().length);
            return cipherTextFinal;
        } else {
            return command.getCipher();
        }
    }


    public static Key getKeyForAlias(String keyAlias) throws CFM2Exception {
        CaviumKey key;
        Enumeration<CaviumKey> keys = Util.findAllKeys(keyAlias);
        if (keys.hasMoreElements()) {
            key = keys.nextElement();
        } else {
            throw new BadRequestException("Could not find key with alias: " + keyAlias,
                    ErrorCode.KEY_NOT_FOUND);
        }
        return key;
    }

    public static boolean updateAAD(AesEncryptCommand command, Cipher cipher) {
        boolean isGcmMode = ModeOfOperation.GCM.equals(command.getModeOfOperation());

        if (isGcmMode && ArrayUtils.isNotEmpty(command.getAdditionalAuthData())) {
            cipher.updateAAD(command.getAdditionalAuthData());
        }
        return isGcmMode;
    }
}
