package com.opabs.trustchain.service;

import com.opabs.common.enums.KeyUsages;
import com.opabs.common.model.*;
import com.opabs.trustchain.controller.command.CreateCertificateCommand;
import com.opabs.trustchain.controller.model.CertificateModel;
import com.opabs.trustchain.controller.responses.CreateCertificateResponse;
import com.opabs.trustchain.domain.Certificate;
import com.opabs.trustchain.exception.InternalServerErrorException;
import com.opabs.trustchain.exception.KeyTypeAndUsageMismatch;
import com.opabs.trustchain.exception.NotFoundException;
import com.opabs.trustchain.exception.ParentKeyUsageInvalidException;
import com.opabs.trustchain.feign.CryptoService;
import com.opabs.trustchain.model.CertificateInfo;
import com.opabs.trustchain.repository.CertificateRepository;
import com.opabs.trustchain.utils.CertificateUtils;
import com.opabs.trustchain.utils.CompressionUtils;
import com.opabs.trustchain.utils.TransformationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cms.CMSException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.stream.Collectors;

import static com.opabs.trustchain.utils.CertificateUtils.*;
import static com.opabs.trustchain.utils.CompressionUtils.compress;
import static com.opabs.trustchain.utils.CompressionUtils.uncompress;

@Slf4j
@Service
@Transactional
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

        Certificate parentCertificate = certificateRepository.findById(command.getParentCertificateId())
                .orElseThrow(() -> new NotFoundException("parent certificate", command.getParentCertificateId()));

        // The parent certificate must have the key usage 'KEY_CERT_SIGN' otherwise its private key
        // can't be used to sign the CSR.

        List<KeyUsages> parentKeyUsages = getKeyUsages(uncompress(parentCertificate.getContent()));
        if (!parentKeyUsages.contains(KeyUsages.KEY_CERT_SIGN)) {
            throw new ParentKeyUsageInvalidException();
        }

        GenerateCSRRequest generateCSRRequest = createCSRRequest(command);
        GenerateCSRResponse csrResponse = cryptoService.generateCSR(generateCSRRequest);

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
        newCertificate.setKeyType(command.getKeyType());
        newCertificate.setParentCertificate(parentCertificate);
        newCertificate.setContent(compress(fromPemCertificate(certificateResponse.getCertificate())));
        newCertificate.setTrustChain(parentCertificate.getTrustChain());
        newCertificate.setWrappedPrivateKey(compress(Base64.getDecoder().decode(csrResponse.getWrappedKey())));

        CertificateInfo certificateInfo = CertificateUtils.getCertificateInfo(certificateResponse.getCertificate());
        newCertificate.setCertificateFingerprint(certificateInfo.getCertificateFingerprint());
        newCertificate.setPublicKeyFingerprint(certificateInfo.getPublicKeyFingerprint());

        certificateRepository.save(newCertificate);

        CreateCertificateResponse response = new CreateCertificateResponse();
        response.setId(newCertificate.getId());
        response.setCertificate(certificateResponse.getCertificate());
        response.setTrustChainId(parentCertificate.getTrustChain().getId());
        response.setParentCertificateId(parentCertificate.getId());
        response.setCertificateFingerprint(certificateInfo.getCertificateFingerprint());
        response.setPublicKeyFingerprint(certificateInfo.getPublicKeyFingerprint());

        return response;
    }

    public byte[] getCertificateContent(UUID id) {
        Optional<Certificate> certificate = certificateRepository.findById(id);
        return certificate.map(Certificate::getContent)
                .map(CompressionUtils::uncompress)
                .orElseThrow(() -> new NotFoundException("certificate", id));
    }

    public byte[] getCertificateChainContent(UUID id) {
        Optional<Certificate> certificateOpt = certificateRepository.findById(id);
        Certificate certificate = certificateOpt
                .orElseThrow(() -> new NotFoundException("certificate", id));
        try {
            List<byte[]> chainContents = new ArrayList<>();
            addParentBytes(certificate, chainContents);
            return getP7b(chainContents);
        } catch (CertificateException | CMSException | IOException exception) {
            log.error("Error occurred while generating p7b bundle.", exception);
            throw new InternalServerErrorException();
        }
    }

    private void addParentBytes(Certificate certificate, List<byte[]> chainContents) {
        chainContents.add(uncompress(certificate.getContent()));
        Certificate parentCert = certificate.getParentCertificate();
        if (parentCert != null) {
            addParentBytes(parentCert, chainContents);
        }
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

    public ListResponse<CertificateModel> list(Pageable pageRequest) {
        Page<Certificate> certificates = certificateRepository.findAll(pageRequest);
        ListResponse<CertificateModel> listResponse = new ListResponse<>();
        listResponse.setContent(certificates.getContent().stream().map(TransformationUtils::fromCertificate).collect(Collectors.toList()));
        listResponse.setTotalElements(certificates.getTotalElements());
        listResponse.setTotalPages(certificates.getTotalPages());
        listResponse.setPageSize(pageRequest.getPageSize());
        listResponse.setPage(pageRequest.getPageNumber());

        return listResponse;
    }
}
