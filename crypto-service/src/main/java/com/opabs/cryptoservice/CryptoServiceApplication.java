package com.opabs.cryptoservice;

import com.cavium.provider.CaviumProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.io.IOException;
import java.security.Security;
import java.util.TimeZone;

@EnableDiscoveryClient
@SpringBootApplication
public class CryptoServiceApplication {

    public static void main(String[] args) throws IOException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new CaviumProvider());
        SpringApplication.run(CryptoServiceApplication.class, args);
    }

}
