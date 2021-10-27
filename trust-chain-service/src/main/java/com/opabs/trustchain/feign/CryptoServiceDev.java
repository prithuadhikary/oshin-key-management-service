package com.opabs.trustchain.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile("!cloud")
@FeignClient(value = "crypto-service", url = "http://localhost:8083")
public interface CryptoServiceDev extends CryptoService {
}
