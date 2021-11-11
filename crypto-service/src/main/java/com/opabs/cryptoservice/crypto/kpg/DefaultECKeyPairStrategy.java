package com.opabs.cryptoservice.crypto.kpg;

import com.cavium.cfm2.CFM2Exception;
import com.cavium.cfm2.Util;
import com.cavium.key.CaviumKey;
import com.cavium.key.parameter.CaviumECGenParameterSpec;
import com.opabs.common.model.KeyType;
import com.opabs.cryptoservice.exception.CurveNotSpecifiedException;
import com.opabs.cryptoservice.exception.KeyPairGenerationFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.*;
import java.util.Map;

import static com.opabs.cryptoservice.constants.Constants.AWS_HSM_JCE_PROVIDER_NAME;
import static com.opabs.cryptoservice.constants.Constants.PARAM_CURVE;

@Slf4j
@Component
@Profile("cloud")
public class DefaultECKeyPairStrategy implements KeyPairStrategy {

    public static final String EC_ALGORITHM_NAME = "EC";

    @Override
    public KeyPair generate(Map<String, Object> params, String privateKeyAlias) {
        if (!params.containsKey(PARAM_CURVE)) {
            throw new CurveNotSpecifiedException();
        }
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(EC_ALGORITHM_NAME, AWS_HSM_JCE_PROVIDER_NAME);
            String curveName = (String) params.get(PARAM_CURVE);
            keyPairGen.initialize(
                    new CaviumECGenParameterSpec(
                            curveName,
                            privateKeyAlias + ":public",
                            privateKeyAlias,
                            false,
                            true));
            KeyPair keyPair = keyPairGen.generateKeyPair();
            Util.persistKey((CaviumKey) keyPair.getPrivate());
            return keyPair;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | CFM2Exception ex) {
            log.error("Error occurred during EC keypair generation.", ex);
            throw new KeyPairGenerationFailureException();
        }
    }

    @Override
    public KeyType supportedKeyType() {
        return KeyType.ELLIPTIC_CURVE;
    }
}
