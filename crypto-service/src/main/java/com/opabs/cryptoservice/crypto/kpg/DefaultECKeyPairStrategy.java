package com.opabs.cryptoservice.crypto.kpg;

import com.cavium.key.parameter.CaviumECGenParameterSpec;
import com.opabs.common.model.KeyType;
import com.opabs.cryptoservice.exception.CurveNotSpecifiedException;
import com.opabs.cryptoservice.exception.KeyLabelMissingException;
import com.opabs.cryptoservice.exception.KeyPairGenerationFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Map;

import static com.opabs.cryptoservice.constants.Constants.*;

@Slf4j
@Component
@Profile("cloud")
public class DefaultECKeyPairStrategy implements KeyPairStrategy {

    public static final String EC_ALGORITHM_NAME = "EC";

    @Override
    public KeyPair generate(Map<String, Object> params) {
        if (!params.containsKey(PARAM_CURVE)) {
            throw new CurveNotSpecifiedException();
        }
        if (!params.containsKey(KEY_LABEL)) {
            throw new KeyLabelMissingException();
        }
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(EC_ALGORITHM_NAME, AWS_HSM_JCE_PROVIDER_NAME);
            String curveName = (String) params.get(PARAM_CURVE);
            String label = (String) params.get(KEY_LABEL);
            keyPairGen.initialize(
                    new CaviumECGenParameterSpec(
                            curveName,
                            label + ":public",
                            label + ":private",
                            true,
                            false));
            return keyPairGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException ex) {
            log.error("Error occurred during EC keypair generation.", ex);
            throw new KeyPairGenerationFailureException();
        }
    }

    @Override
    public PrivateKey loadPrivateKey(byte[] privateKeyBytes) throws Exception {
        KeyFactory factory = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return factory.generatePrivate(keySpec);
    }

    @Override
    public KeyType supportedKeyType() {
        return KeyType.ELLIPTIC_CURVE;
    }
}
