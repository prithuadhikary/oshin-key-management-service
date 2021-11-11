package com.opabs.cryptoservice.service.aes;

import com.opabs.common.enums.ModeOfOperation;
import com.opabs.common.model.AesDecryptRequest;
import com.opabs.common.model.AesDecryptResponse;
import com.opabs.common.model.AesEncryptRequest;
import com.opabs.common.model.AesEncryptResponse;
import com.opabs.cryptoservice.constants.Constants;
import com.opabs.cryptoservice.exception.BadTagException;
import com.opabs.cryptoservice.crypto.kg.KeyDetails;
import com.opabs.cryptoservice.crypto.kg.KeyGeneratorStrategy;
import com.opabs.common.model.AesCreateKeyResponse;
import com.opabs.cryptoservice.service.KeyManagementService;
import com.opabs.cryptoservice.service.model.AesDecryptCommand;
import com.opabs.cryptoservice.service.model.AesEncryptCommand;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Optional;

import static com.opabs.cryptoservice.util.CryptoUtils.*;
import static com.opabs.cryptoservice.util.TransformationUtils.toCommand;

@Component
@Profile("cloud")
@RequiredArgsConstructor
public class DefaultAesService implements AesService {

    private final KeyGeneratorStrategy keyGeneratorStrategy;

    private final KeyManagementService keyManagementService;

    @Override
    public Mono<AesEncryptResponse> encrypt(AesEncryptRequest request) {
        return Mono.fromCallable(() -> {
            AesEncryptCommand command = toCommand(request);
            ModeOfOperation modeOfOperation = getModeOfOperation(command);
            Key key = keyManagementService.getKeyForKeyAlias(request.getKeyAlias());

            Cipher cipher = Cipher.getInstance(modeOfOperation.modeOfOperation(), Constants.AWS_HSM_JCE_PROVIDER_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            boolean isGcmMode = updateAAD(command, cipher);

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
        });
    }

    @Override
    public Mono<AesDecryptResponse> decrypt(AesDecryptRequest request) {
        return Mono.fromCallable(() -> {
            AesDecryptCommand command = toCommand(request);

            ModeOfOperation modeOfOperation = getModeOfOperation(command);

            Integer tagLength = Optional.ofNullable(command.getTag()).map(tag -> tag.length).orElse(16);

            AlgorithmParameterSpec parameterSpec = getAlgorithmParameterSpec(
                    tagLength, modeOfOperation, command.getIv()
            );
            Key key = keyManagementService.getKeyForKeyAlias(request.getKeyAlias());
            Cipher cipher = Cipher.getInstance(modeOfOperation.modeOfOperation());

            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

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
        return Mono.fromCallable(() -> {
            KeyDetails keyDetails = keyGeneratorStrategy.generate(keyLength, label);
            AesCreateKeyResponse response = new AesCreateKeyResponse();
            response.setHandle(keyDetails.getKeyHandle());
            response.setLabel(label);
            return response;
        });
    }
}
