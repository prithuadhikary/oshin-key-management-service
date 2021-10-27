package com.opabs.trustchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class TrustChainServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrustChainServiceApplication.class, args);
    }

}
