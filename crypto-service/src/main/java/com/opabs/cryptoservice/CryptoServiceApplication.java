package com.opabs.cryptoservice;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.security.Security;
import java.util.TimeZone;

@EnableDiscoveryClient
@SpringBootApplication
public class CryptoServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Security.addProvider(new BouncyCastleProvider());
        SpringApplication.run(CryptoServiceApplication.class, args);
    }

}
