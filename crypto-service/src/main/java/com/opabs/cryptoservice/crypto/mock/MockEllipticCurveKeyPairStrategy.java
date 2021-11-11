package com.opabs.cryptoservice.crypto.mock;

import com.opabs.common.model.KeyType;
import com.opabs.cryptoservice.crypto.kpg.KeyPairStrategy;
import com.opabs.cryptoservice.exception.CurveNotSpecifiedException;
import com.opabs.cryptoservice.exception.KeyPairGenerationFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.opabs.cryptoservice.constants.Constants.PARAM_CURVE;

@Slf4j
@Component
@Profile("local")
public class MockEllipticCurveKeyPairStrategy implements KeyPairStrategy {

    public static final String EC_ALGORITHM_NAME = "EC";

    @Override
    public KeyPair generate(Map<String, Object> params, String privateKeyAlias) {
        if (!params.containsKey(PARAM_CURVE)) {
            throw new CurveNotSpecifiedException();
        }
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(EC_ALGORITHM_NAME);
            ECGenParameterSpec spec = new ECGenParameterSpec((String) params.get(PARAM_CURVE));
            kpg.initialize(spec);
            return kpg.generateKeyPair();
        } catch (Exception ex) {
            log.error("Error occurred while generating EC key pair.", ex);
            throw new KeyPairGenerationFailureException();
        }
    }

    @Override
    public KeyType supportedKeyType() {
        return KeyType.ELLIPTIC_CURVE;
    }
}
