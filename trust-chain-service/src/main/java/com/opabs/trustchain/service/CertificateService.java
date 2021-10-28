package com.opabs.trustchain.service;

import com.opabs.common.enums.KeyUsages;
import com.opabs.common.model.CertificateSigningRequest;
import com.opabs.common.model.CertificateSigningResponse;
import com.opabs.common.model.GenerateCSRRequest;
import com.opabs.common.model.GenerateCSRResponse;
import com.opabs.trustchain.controller.command.CreateCertificateCommand;
import com.opabs.trustchain.controller.responses.CreateCertificateResponse;
import com.opabs.trustchain.domain.Certificate;
import com.opabs.trustchain.exception.KeyTypeAndUsageMismatch;
import com.opabs.trustchain.exception.NotFoundException;
import com.opabs.trustchain.feign.CryptoService;
import com.opabs.trustchain.model.CertificateInfo;
import com.opabs.trustchain.repository.CertificateRepository;
import com.opabs.trustchain.utils.CertificateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static com.opabs.trustchain.utils.CertificateUtils.*;
import static com.opabs.trustchain.utils.CompressionUtils.uncompress;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CryptoService cryptoService;

    private final CertificateRepository certificateRepository;

    public CreateCertificateResponse createCertificate(CreateCertificateCommand command) {
        //1. Fetch the parent certificate object containing the certificate content and
        //   the wrapped private key.
        //2. Generate CSR for the target keyType and subject by calling crypto service.
        //3. Call crypto service to generate the certificate.
        //4. Save the certificate content and wrapped private key.

        validateKeyUsage(command);

        GenerateCSRRequest generateCSRRequest = createCSRRequest(command);
        GenerateCSRResponse csrResponse = cryptoService.generateCSR(generateCSRRequest);

        Certificate parentCertificate = certificateRepository.findById(command.getParentCertificateId())
                .orElseThrow(() -> new NotFoundException("Certificate", command.getParentCertificateId()));

        CertificateSigningRequest csrReq = new CertificateSigningRequest();
        csrReq.setKeyUsages(command.getKeyUsages());
        csrReq.setWrappedIssuerPrivateKey(Base64.getEncoder().encodeToString(uncompress(parentCertificate.getWrappedPrivateKey())));
        csrReq.setValidityInYears(command.getValidityInYears());
        csrReq.setValidFrom(command.getValidFrom());
        //TODO: Implement tenant specific unwrapping key alias in crypto service.
        csrReq.setUnwrappingKeyAlias("unwrappingKeyAlias");
        csrReq.setSignatureAlgorithm(command.getSignatureAlgorithm());
        csrReq.setPkcs10CSR(csrResponse.getPkcs10CSR());
        String pemCertificate = toPemCertificate(uncompress(parentCertificate.getContent()));
        csrReq.setIssuerCertificate(pemCertificate);

        CertificateSigningResponse certificateResponse = cryptoService.signCSR(csrReq);

        Certificate newCertificate = new Certificate();
        newCertificate.setAnchor(false);
        newCertificate.setParentCertificate(parentCertificate);
        newCertificate.setContent(fromPemCertificate(certificateResponse.getCertificate()));

        CertificateInfo certificateInfo = CertificateUtils.getCertificateInfo(certificateResponse.getCertificate());
        newCertificate.setCertificateFingerprint(certificateInfo.getCertificateFingerprint());
        newCertificate.setPublicKeyFingerprint(certificateInfo.getPublicKeyFingerprint());

        certificateRepository.save(newCertificate);

        CreateCertificateResponse response = new CreateCertificateResponse();
        response.setCertificate(certificateResponse.getCertificate());
        response.setTrustChainId(parentCertificate.getTrustChain().getId());
        response.setParentCertificateId(parentCertificate.getId());
        response.setCertificateFingerprint(certificateInfo.getCertificateFingerprint());
        response.setPublicKeyFingerprint(certificateInfo.getPublicKeyFingerprint());

        return response;
    }

    private void validateKeyUsage(CreateCertificateCommand command) {
        List<KeyUsages> unsupported = null;
        if (command.getKeyUsages() != null) {
            unsupported = command.getKeyUsages().stream()
                    .filter(keyUsages -> !keyUsages.getSupportedKeyTypes().contains(command.getKeyType())).collect(Collectors.toList());
        }
        if (unsupported != null && !unsupported.isEmpty()) {
            throw new KeyTypeAndUsageMismatch(command.getKeyType(), unsupported);
        }
    }

}
