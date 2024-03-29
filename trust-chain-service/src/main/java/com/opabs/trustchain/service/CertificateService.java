package com.opabs.trustchain.service;

import com.opabs.common.enums.KeyUsages;
import com.opabs.common.model.*;
import com.opabs.common.security.GroupPermissions;
import com.opabs.common.security.JWTAuthToken;
import com.opabs.trustchain.controller.command.CreateCertificateCommand;
import com.opabs.trustchain.controller.model.CertificateModel;
import com.opabs.trustchain.controller.responses.CreateCertificateResponse;
import com.opabs.trustchain.domain.Certificate;
import com.opabs.trustchain.domain.TrustChain;
import com.opabs.trustchain.exception.InternalServerErrorException;
import com.opabs.trustchain.exception.KeyTypeAndUsageMismatch;
import com.opabs.trustchain.exception.NotFoundException;
import com.opabs.trustchain.exception.ParentKeyUsageInvalidException;
import com.opabs.trustchain.feign.CryptoService;
import com.opabs.trustchain.model.CertificateInfo;
import com.opabs.trustchain.repository.CertificateRepository;
import com.opabs.trustchain.repository.TrustChainRepository;
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
import java.security.Principal;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.opabs.trustchain.domain.specifications.CertificateSpecifications.searchSpecification;
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

    private final TrustChainRepository trustChainRepository;

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

        TrustChain trustChain = parentCertificate.getTrustChain();
        GenerateCSRRequest generateCSRRequest = createCSRRequest(command, trustChain.getTenantExtId());
        GenerateCSRResponse csrResponse = cryptoService.generateCSR(generateCSRRequest);

        CertificateSigningRequest csrReq = new CertificateSigningRequest();
        csrReq.setKeyUsages(command.getKeyUsages());
        csrReq.setIssuerPrivateKeyAlias(parentCertificate.getPrivateKeyAlias());
        csrReq.setValidityInYears(command.getValidityInYears());
        csrReq.setValidFrom(command.getValidFrom());
        //TODO: Implement tenant specific unwrapping key alias in crypto service.
        csrReq.setSignatureAlgorithm(command.getSignatureAlgorithm());
        csrReq.setPkcs10CSR(csrResponse.getPkcs10CSR());
        String pemCertificate = toPemCertificate(uncompress(parentCertificate.getContent()));
        csrReq.setIssuerCertificate(pemCertificate);

        Long lastSerialNumber = trustChain.getLastSerialNumber();
        csrReq.setSerial(lastSerialNumber + 1);
        trustChain.setLastSerialNumber(lastSerialNumber + 1);
        trustChainRepository.save(trustChain);

        CertificateSigningResponse certificateResponse = cryptoService.signCSR(csrReq);

        Certificate newCertificate = new Certificate();
        newCertificate.setAnchor(false);
        newCertificate.setKeyType(command.getKeyType());
        newCertificate.setParentCertificate(parentCertificate);
        newCertificate.setContent(compress(fromPemCertificate(certificateResponse.getCertificate())));
        newCertificate.setTrustChain(trustChain);
        newCertificate.setPrivateKeyAlias(csrResponse.getPrivateKeyAlias());
        newCertificate.setSubjectDistinguishedName(command.getSubjectDistinguishedName());
        CertificateInfo certificateInfo = CertificateUtils.getCertificateInfo(certificateResponse.getCertificate());
        newCertificate.setCertificateFingerprint(certificateInfo.getCertificateFingerprint());
        newCertificate.setPublicKeyFingerprint(certificateInfo.getPublicKeyFingerprint());
        newCertificate.setDateIssued(certificateInfo.getDateIssued());
        newCertificate.setExpiryDate(certificateInfo.getExpiryDate());
        certificateRepository.save(newCertificate);

        CreateCertificateResponse response = new CreateCertificateResponse();
        response.setId(newCertificate.getId());
        response.setCertificate(certificateResponse.getCertificate());
        response.setTrustChainId(trustChain.getId());
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

    public ListResponse<CertificateModel> list(Principal userPrincipal, String search, UUID parentCertificateId, Pageable pageRequest) {
        Page<Certificate> certificates;
        UUID tenantId = null;
        if (userPrincipal instanceof JWTAuthToken && ((JWTAuthToken) userPrincipal).getGroup() == GroupPermissions.TENANT_ADMIN) {
            tenantId = ((JWTAuthToken) userPrincipal).getAccessToken().getTenantIdentifier();
        }

        certificates = certificateRepository.findAll(searchSpecification(search, parentCertificateId, tenantId), pageRequest);

        ListResponse<CertificateModel> listResponse = new ListResponse<>();
        listResponse.setContent(certificates.getContent().stream().map(TransformationUtils::fromCertificate).collect(Collectors.toList()));
        listResponse.setTotalElements(certificates.getTotalElements());
        listResponse.setTotalPages(certificates.getTotalPages());
        listResponse.setPageSize(pageRequest.getPageSize());
        listResponse.setPage(pageRequest.getPageNumber());

        return listResponse;
    }
}
