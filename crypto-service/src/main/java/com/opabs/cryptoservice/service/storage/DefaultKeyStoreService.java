package com.opabs.cryptoservice.service.storage;

import com.cavium.cfm2.CFM2Exception;
import com.cavium.cfm2.Util;
import com.cavium.key.CaviumKey;
import com.opabs.cryptoservice.exception.BadRequestException;
import com.opabs.cryptoservice.exception.ErrorCode;
import com.opabs.cryptoservice.exception.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.util.Enumeration;

@Slf4j
@Component
@Profile("cloud")
public class DefaultKeyStoreService implements KeyStoreService {
    @Override
    public PrivateKey findPrivateKey(String keyAlias) {
        CaviumKey key;
        try {
            Enumeration<CaviumKey> keys = Util.findAllKeys(keyAlias);
            if (keys.hasMoreElements()) {
                key = keys.nextElement();
            } else {
                throw new BadRequestException("Could not find key with alias: " + keyAlias,
                        ErrorCode.KEY_NOT_FOUND);
            }
            return (PrivateKey) key;
        } catch (CFM2Exception ex) {
            log.error("Error occurred while fetching private key from HSM.", ex);
            throw new InternalServerErrorException();
        }

    }

    @Override
    public void persistKey(String keyAlias, PrivateKey privateKey) {
        try {
            Util.persistKey((CaviumKey) privateKey);
        } catch (CFM2Exception ex) {
            log.error("Error occurred while persisting the key to HSM.", ex);
            throw new InternalServerErrorException();
        }
    }
}
