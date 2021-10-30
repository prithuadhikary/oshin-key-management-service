package com.opabs.trustchain.service;

import com.opabs.common.enums.KeyUsages;
import com.opabs.common.model.*;
import com.opabs.trustchain.controller.command.CreateTrustChainCommand;
import com.opabs.trustchain.controller.command.UpdateTrustChainCommand;
import com.opabs.trustchain.controller.model.TrustChainModel;
import com.opabs.trustchain.domain.Certificate;
import com.opabs.trustchain.domain.TrustChain;
import com.opabs.trustchain.exception.NotFoundException;
import com.opabs.trustchain.feign.CryptoService;
import com.opabs.trustchain.feign.TenantManagementService;
import com.opabs.trustchain.model.CertificateInfo;
import com.opabs.trustchain.model.TenantInfo;
import com.opabs.trustchain.repository.CertificateRepository;
import com.opabs.trustchain.repository.TrustChainRepository;
import com.opabs.trustchain.utils.CertificateUtils;
import com.opabs.trustchain.utils.TransformationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.opabs.trustchain.utils.CertificateUtils.createCSRRequest;
import static com.opabs.trustchain.utils.CertificateUtils.fromPemCertificate;
import static com.opabs.trustchain.utils.CompressionUtils.compress;
import static com.opabs.trustchain.utils.TransformationUtils.fromTrustChain;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrustChainService {

    private final CryptoService cryptoService;

    private final TenantManagementService tenantManagementService;

    private final TrustChainRepository trustChainRepository;

    private final CertificateRepository certificateRepository;

    public TrustChainModel create(CreateTrustChainCommand command) {

        //1. Validate tenantExtId with tenant management service.
        //2. Create a root certificate using crypto service with key usage
        //3. Create TrustChain entity.
        //4. Save certificate and wrappedPrivateKey.
        //5. Return fully populated TrustChain entity back to the client.
        validateTenantExtId(command);

        GenerateCSRRequest request = createCSRRequest(command);
        GenerateCSRResponse csr = cryptoService.generateCSR(request);
        log.debug("CSR Response: {}", csr.getPkcs10CSR());
        log.debug("Wrapped Key: {}", csr.getWrappedKey());

        CertificateSigningRequest signingRequest = createSigningRequest(command, csr);

        CertificateSigningResponse signingResponse = cryptoService.signCSR(signingRequest);

        TrustChain trustChain = new TrustChain();
        trustChain.setTenantExtId(command.getTenantExtId());
        trustChain.setName(command.getName());
        trustChain.setDescription(command.getDescription());

        trustChain = trustChainRepository.save(trustChain);

        Certificate certificate = new Certificate();
        certificate.setAnchor(true);
        byte[] content = fromPemCertificate(signingResponse.getCertificate());
        certificate.setContent(compress(content));
        byte[] wrappedKey = Base64.getDecoder().decode(csr.getWrappedKey());
        certificate.setWrappedPrivateKey(compress(wrappedKey));
        certificate.setTrustChain(trustChain);
        certificate.setKeyType(command.getKeyType());

        CertificateInfo certInfo = CertificateUtils.getCertificateInfo(signingResponse.getCertificate());
        certificate.setPublicKeyFingerprint(certInfo.getPublicKeyFingerprint());
        certificate.setCertificateFingerprint(certInfo.getCertificateFingerprint());

        certificateRepository.save(certificate);
        trustChain.setRootCertificate(certificate);
        trustChainRepository.save(trustChain);

        return fromTrustChain(trustChain);
    }

    private void validateTenantExtId(CreateTrustChainCommand command) {
        try {
            TenantInfo tenantInfo = tenantManagementService.getTenantInfo(command.getTenantExtId());
            if (!tenantInfo.getId().equals(command.getTenantExtId())) {
                throw new NotFoundException("tenant", command.getTenantExtId());
            }
        } catch (Exception ex) {
            log.error("Error occurred while fetching tenant information.", ex);
            throw new NotFoundException("tenant", command.getTenantExtId());
        }
    }

    public Optional<TrustChain> findById(UUID id) {
        return trustChainRepository.findByIdAndDeleted(id, false);
    }

    private CertificateSigningRequest createSigningRequest(CreateTrustChainCommand command, GenerateCSRResponse csr) {
        CertificateSigningRequest signingRequest = new CertificateSigningRequest();
        signingRequest.setSelfSigned(true);

        //Default for a self signed root certificate.
        signingRequest.setKeyUsages(Collections.singletonList(KeyUsages.KEY_CERT_SIGN));

        signingRequest.setPkcs10CSR(csr.getPkcs10CSR());
        signingRequest.setSignatureAlgorithm(command.getSignatureAlgorithm());
        //TODO: Change this to use a valid key alias after implementation of key alias based keys.
        signingRequest.setUnwrappingKeyAlias("TENANT_SPECIFIC_KEY_ALIAS");
        signingRequest.setValidFrom(command.getValidFrom());
        signingRequest.setValidityInYears(command.getValidityInYears());
        signingRequest.setWrappedIssuerPrivateKey(csr.getWrappedKey());
        return signingRequest;
    }

    public ListResponse<TrustChainModel> findAll(PageRequest pageRequest) {
        Page<TrustChain> chains = trustChainRepository.findAllByDeleted(false, pageRequest);
        ListResponse<TrustChainModel> response = new ListResponse<>();
        List<TrustChainModel> transformedModel = chains.getContent().stream().map(TransformationUtils::fromTrustChain).collect(Collectors.toList());
        response.setContent(transformedModel);
        response.setPage(pageRequest.getPageNumber());
        response.setPageSize(pageRequest.getPageSize());
        response.setTotalPages(chains.getTotalPages());
        response.setTotalElements(chains.getTotalElements());
        return response;
    }

    public Optional<TrustChain> update(UUID id, UpdateTrustChainCommand trustChain) {
        Optional<TrustChain> existing = trustChainRepository.findByIdAndDeleted(id, false);
        if (existing.isPresent()) {
            TrustChain existingTrustChain = existing.get();
            existingTrustChain.setDescription(trustChain.getDescription());
            existingTrustChain.setName(trustChain.getName());
            return Optional.of(trustChainRepository.save(existingTrustChain));
        } else {
            return Optional.empty();
        }
    }

    public Optional<TrustChain> delete(UUID id) {
        Optional<TrustChain> existing = trustChainRepository.findByIdAndDeleted(id, false);
        if (existing.isPresent()) {
            TrustChain trustChain = existing.get();
            trustChain.setDeleted(true);
            trustChainRepository.save(trustChain);
            return existing;
        } else {
            return Optional.empty();
        }
    }

}
