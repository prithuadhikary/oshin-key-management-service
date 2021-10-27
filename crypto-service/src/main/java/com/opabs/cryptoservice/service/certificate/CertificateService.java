package com.opabs.cryptoservice.service.certificate;

import com.opabs.common.model.CertificateSigningRequest;
import com.opabs.common.model.CertificateSigningResponse;
import com.opabs.common.model.GenerateCSRRequest;
import com.opabs.common.model.GenerateCSRResponse;
import reactor.core.publisher.Mono;

public interface CertificateService {

    /**
     * Generates a Public/Private key pair and generates a self signed CSR containing the public key.
     *
     * @param request the {@link GenerateCSRRequest} request.
     * @return response {@link GenerateCSRResponse} object containing the wrapped private key and certificate
     * signing request.
     */
    Mono<GenerateCSRResponse> generateCertificateSigningRequest(GenerateCSRRequest request);


    /**
     * Sign certificate signing request and generate an X509 certificate.
     *
     * @param request {@link CertificateSigningRequest} containing the PKCS10 certificate signing request.
     * @return A certificate signing response containing the signed certificate.
     */
    Mono<CertificateSigningResponse> sign(CertificateSigningRequest request);

}
