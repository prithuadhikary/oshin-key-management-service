package com.opabs.cryptoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.io.IOException;
import java.util.TimeZone;

@EnableDiscoveryClient
@SpringBootApplication
public class CryptoServiceApplication {

    public static void main(String[] args) throws IOException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(CryptoServiceApplication.class, args);
    }

}
