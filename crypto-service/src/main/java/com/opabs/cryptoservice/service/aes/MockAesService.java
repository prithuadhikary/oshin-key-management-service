package com.opabs.cryptoservice.service.aes;

import com.opabs.common.enums.ModeOfOperation;
import com.opabs.common.model.AesDecryptRequest;
import com.opabs.common.model.AesDecryptResponse;
import com.opabs.common.model.AesEncryptRequest;
import com.opabs.common.model.AesEncryptResponse;
import com.opabs.cryptoservice.config.MockConfig;
import com.opabs.cryptoservice.exception.BadTagException;
import com.opabs.common.model.AesCreateKeyResponse;
import com.opabs.cryptoservice.service.model.AesDecryptCommand;
import com.opabs.cryptoservice.service.model.AesEncryptCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Optional;

import static com.opabs.cryptoservice.util.CryptoUtils.*;
import static com.opabs.cryptoservice.util.TransformationUtils.toCommand;

@Slf4j
@Service
@Profile("local")
@RequiredArgsConstructor
public class MockAesService implements AesService {

    private final MockConfig mockConfig;

    private SecretKey aesKey;

    @PostConstruct
    public void setup() {
        this.aesKey = new SecretKeySpec(Base64.getDecoder().decode(mockConfig.getAesKey()), "AES");
    }

    public Mono<AesEncryptResponse> encrypt(AesEncryptRequest request) {
        return Mono.fromCallable(() -> {

            AesEncryptCommand command = toCommand(request);

            ModeOfOperation modeOfOperation = getModeOfOperation(command);

            AlgorithmParameterSpec parameterSpec = getAlgorithmParameterSpec(command.getTagLength(), modeOfOperation, command.getIv());

            Cipher cipher = Cipher.getInstance(modeOfOperation.modeOfOperation());

            cipher.init(Cipher.ENCRYPT_MODE, this.aesKey, parameterSpec);

            boolean isGcmMode = ModeOfOperation.GCM.equals(command.getModeOfOperation());

            if (isGcmMode && ArrayUtils.isNotEmpty(command.getAdditionalAuthData())) {
                cipher.updateAAD(command.getAdditionalAuthData());
            }

            byte[] cipherText = cipher.doFinal(command.getMessage());

            AesEncryptResponse response = new AesEncryptResponse();
            Base64.Encoder encoder = Base64.getEncoder();
            if (isGcmMode) {
                byte[] cipherTextFinal = new byte[cipherText.length - command.getTagLength()];
                byte[] tag = new byte[command.getTagLength()];

                System.arraycopy(cipherText, 0, cipherTextFinal, 0, cipherTextFinal.length);
                System.arraycopy(cipherText, cipherTextFinal.length, tag, 0, command.getTagLength());
                response.setTag(encoder.encodeToString(tag));
                response.setIv(encoder.encodeToString(command.getIv()));
                response.setCipher(encoder.encodeToString(cipherTextFinal));
            } else {
                response.setCipher(encoder.encodeToString(cipherText));
            }
            return response;
        }).onErrorResume(AEADBadTagException.class, throwable -> Mono.error(new BadTagException()));
    }

    public Mono<AesDecryptResponse> decrypt(AesDecryptRequest request) {
        return Mono.fromCallable(() -> {
            AesDecryptCommand command = toCommand(request);

            ModeOfOperation modeOfOperation = getModeOfOperation(command);

            Integer tagLength = Optional.ofNullable(command.getTag()).map(tag -> tag.length).orElse(16);

            AlgorithmParameterSpec parameterSpec = getAlgorithmParameterSpec(
                    tagLength, modeOfOperation, command.getIv()
            );

            Cipher cipher = Cipher.getInstance(modeOfOperation.modeOfOperation());

            cipher.init(Cipher.DECRYPT_MODE, this.aesKey, parameterSpec);

            boolean isGcmMode = ModeOfOperation.GCM.equals(command.getModeOfOperation());

            if (isGcmMode && ArrayUtils.isNotEmpty(command.getAdditionalAuthData())) {
                cipher.updateAAD(command.getAdditionalAuthData());
            }

            byte[] cipherText = getCipherText(isGcmMode, command);

            byte[] plainTextBytes = cipher.doFinal(cipherText);

            AesDecryptResponse response = new AesDecryptResponse();
            response.setMessage(Base64.getEncoder().encodeToString(plainTextBytes));
            return response;
        }).onErrorResume(AEADBadTagException.class, throwable -> Mono.error(new BadTagException()));
    }

    @Override
    public Mono<AesCreateKeyResponse> createKey(int keyLength, String label) {
        return null;
    }

}
