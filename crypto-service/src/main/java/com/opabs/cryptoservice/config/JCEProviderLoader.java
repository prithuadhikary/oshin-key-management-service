package com.opabs.cryptoservice.config;

import com.cavium.provider.CaviumProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.security.Security;
import java.util.List;

@Component
public class JCEProviderLoader implements CommandLineRunner {

    @Autowired
    private Environment environment;

    @Override
    public void run(String... args) throws Exception {
        List<String> activeProfiles = List.of(environment.getActiveProfiles());
        if (activeProfiles.contains("local")) {
            Security.addProvider(new BouncyCastleProvider());
        } else if (activeProfiles.contains("cloud")) {
            Security.addProvider(new CaviumProvider());
        }
    }
}
