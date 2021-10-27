package com.opabs.trustchain.feign;

import com.opabs.common.model.CertificateSigningRequest;
import com.opabs.common.model.CertificateSigningResponse;
import com.opabs.common.model.GenerateCSRRequest;
import com.opabs.common.model.GenerateCSRResponse;
import org.springframework.web.bind.annotation.PostMapping;

public interface CryptoService {

    @PostMapping("/certificate/generate-csr")
    GenerateCSRResponse generateCSR(GenerateCSRRequest request);

    @PostMapping("/certificate/sign")
    CertificateSigningResponse signCSR(CertificateSigningRequest request);

}
