package com.opabs.cryptoservice;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.security.Security;

@EnableDiscoveryClient
@SpringBootApplication
public class CryptoServiceApplication {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        SpringApplication.run(CryptoServiceApplication.class, args);
    }

}
