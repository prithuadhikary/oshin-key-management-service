package com.opabs.trustchain.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile("cloud")
@FeignClient(value = "crypto-service")
public interface CryptoServiceCloud {
}
