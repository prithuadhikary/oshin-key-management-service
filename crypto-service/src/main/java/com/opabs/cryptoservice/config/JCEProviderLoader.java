package com.opabs.cryptoservice.config;

import com.cavium.provider.CaviumProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.security.Provider;
import java.security.Security;

@Configuration
public class JCEProviderLoader {

    @Bean
    @Profile("local")
    public Provider bouncyCastleProvider() {
        Provider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        return provider;
    }

    /**
     * Loads the cavium provider required to communicate with the aws cloudHSM.
     * @return A security provider bean.
     * @throws IOException May be thrown.
     */
    @Bean
    @Profile("cloud")
    public Provider caviumProvider() throws IOException {
        Provider provider = new CaviumProvider();
        Security.addProvider(provider);
        return provider;
    }

}
