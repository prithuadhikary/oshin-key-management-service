package com.opabs.trustchain.service;

import com.opabs.common.enums.KeyUsages;
import com.opabs.common.model.*;
import com.opabs.common.security.GroupPermissions;
import com.opabs.common.security.JWTAuthToken;
import com.opabs.trustchain.controller.command.CreateTrustChainCommand;
import com.opabs.trustchain.controller.command.UpdateTrustChainCommand;
import com.opabs.trustchain.controller.responses.TrustChainCountResponse;
import com.opabs.trustchain.controller.model.TrustChainModel;
import com.opabs.trustchain.domain.Certificate;
import com.opabs.trustchain.domain.TrustChain;
import com.opabs.trustchain.exception.InternalServerErrorException;
import com.opabs.trustchain.exception.InvalidTenantIdException;
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

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static com.opabs.trustchain.utils.CertificateUtils.createCSRRequest;
import static com.opabs.trustchain.utils.CertificateUtils.fromPemCertificate;
import static com.opabs.trustchain.utils.CompressionUtils.compress;
import static com.opabs.trustchain.utils.TransformationUtils.fromCertificate;
import static com.opabs.trustchain.utils.TransformationUtils.fromTrustChain;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TrustChainService {

    private final CryptoService cryptoService;

    private final TenantManagementService tenantManagementService;

    private final TrustChainRepository trustChainRepository;

    private final CertificateRepository certificateRepository;

    public TrustChainModel create(Principal userPrincipal, CreateTrustChainCommand command) {
        //1. Validate tenantExtId with tenant management service.
        //2. Create a root certificate using crypto service with key usages keyCertSign and crlSign.
        //3. Create TrustChain entity.
        //4. Save certificate and wrappedPrivateKey.
        //5. Return fully populated TrustChainModel back to the client.
        JWTAuthToken token = (JWTAuthToken) userPrincipal;
        UUID tenantExtId = getTenantId(command.getTenantExtId(), token);

        validateTenantExtId(command);

        GenerateCSRRequest request = createCSRRequest(command);
        GenerateCSRResponse csr = cryptoService.generateCSR(request);
        log.debug("CSR Response: {}", csr.getPkcs10CSR());
        log.debug("Wrapped Key: {}", csr.getWrappedKey());

        CertificateSigningRequest signingRequest = createSigningRequest(command, csr);

        CertificateSigningResponse signingResponse = cryptoService.signCSR(signingRequest);

        TrustChain trustChain = new TrustChain();
        trustChain.setTenantExtId(tenantExtId);
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
        certificate.setSubjectDistinguishedName(command.getSubjectDistinguishedName());

        CertificateInfo certInfo = CertificateUtils.getCertificateInfo(signingResponse.getCertificate());
        certificate.setPublicKeyFingerprint(certInfo.getPublicKeyFingerprint());
        certificate.setCertificateFingerprint(certInfo.getCertificateFingerprint());

        certificateRepository.save(certificate);
        trustChain.setRootCertificate(certificate);
        trustChainRepository.save(trustChain);

        return fromTrustChain(trustChain);
    }

    private UUID getTenantId(UUID commandTokenExtId, JWTAuthToken token) {
        UUID tenantExtId = null;
        // If it is a tenant admin,
        if (token.getGroup() == GroupPermissions.TENANT_ADMIN) {
            if (!token.getAccessToken().getTenantIdentifier().equals(commandTokenExtId)) {
                throw new InvalidTenantIdException();
            } else {
                tenantExtId = token.getAccessToken().getTenantIdentifier();
            }
        } else if (token.getGroup() == GroupPermissions.OPABS_ADMIN) {
            tenantExtId = commandTokenExtId;
        }
        return tenantExtId;
    }

    private void validateTenantExtId(CreateTrustChainCommand command) {
        try {
            TenantInfo tenantInfo = tenantManagementService.getTenantInfo(command.getTenantExtId());
            if (tenantInfo != null && !tenantInfo.getId().equals(command.getTenantExtId())) {
                throw new NotFoundException("tenant", command.getTenantExtId());
            }
        } catch (Exception ex) {
            log.error("Error occurred while fetching tenant information.", ex);
            throw new NotFoundException("tenant", command.getTenantExtId());
        }
    }

    public TrustChainModel show(Principal userPrincipal, UUID id) {
        UUID tenantId = getTenantExtId(userPrincipal);
        TrustChain trustChain;

        if (tenantId != null) {
            trustChain = trustChainRepository.findByIdAndTenantExtIdAndDeleted(id, tenantId, false)
                    .orElseThrow(() -> new NotFoundException("trust chain", id));
        } else {
            trustChain = trustChainRepository.findByIdAndDeleted(id, false)
                    .orElseThrow(() -> new NotFoundException("trust chain", id));
        }

        TrustChainModel responseModel = new TrustChainModel();
        responseModel.setTenantExtId(trustChain.getTenantExtId());
        responseModel.setId(trustChain.getId());
        responseModel.setName(trustChain.getName());
        responseModel.setDescription(trustChain.getDescription());
        responseModel.setRootCertificate(fromCertificate(trustChain.getRootCertificate()));
        responseModel.setDeleted(trustChain.isDeleted());
        responseModel.setDateCreated(trustChain.getDateCreated());
        responseModel.setDateUpdated(trustChain.getDateUpdated());
        return responseModel;
    }

    private UUID getTenantExtId(Principal userPrincipal) {
        UUID tenantId = null;
        if (userPrincipal instanceof JWTAuthToken) {
            JWTAuthToken token = (JWTAuthToken) userPrincipal;
            if (token.getGroup() == GroupPermissions.TENANT_ADMIN) {
                tenantId = token.getAccessToken().getTenantIdentifier();
            }
        }
        return tenantId;
    }

    public Optional<TrustChain> findById(UUID id) {
        return trustChainRepository.findByIdAndDeleted(id, false);
    }

    private CertificateSigningRequest createSigningRequest(CreateTrustChainCommand command, GenerateCSRResponse csr) {
        CertificateSigningRequest signingRequest = new CertificateSigningRequest();
        signingRequest.setSelfSigned(true);

        //Default for a self signed root certificate.
        signingRequest.setKeyUsages(Arrays.asList(KeyUsages.KEY_CERT_SIGN, KeyUsages.CRL_SIGN));

        signingRequest.setPkcs10CSR(csr.getPkcs10CSR());
        signingRequest.setSignatureAlgorithm(command.getSignatureAlgorithm());
        //TODO: Change this to use a valid key alias after implementation of key alias based keys.
        signingRequest.setUnwrappingKeyAlias("TENANT_SPECIFIC_KEY_ALIAS");
        signingRequest.setValidFrom(command.getValidFrom());
        signingRequest.setValidityInYears(command.getValidityInYears());
        signingRequest.setWrappedIssuerPrivateKey(csr.getWrappedKey());

        // The path length constraint determines how many sub CAs are allowed beneath this root CA certificate.
        // Every time a non root intermediate CA certificate is generated. the basic constraint will be decremented
        // by 1.
        signingRequest.setPathLengthConstraint(command.getPathLengthConstraint());

        return signingRequest;
    }

    public ListResponse<TrustChainModel> findAll(Principal userPrincipal, PageRequest pageRequest) {
        UUID tenantExtId = getTenantExtId(userPrincipal);

        Page<TrustChain> chains;
        if (tenantExtId != null) {
            chains = trustChainRepository.findAllByTenantExtIdAndDeleted(tenantExtId, false, pageRequest);
        } else {
            chains = trustChainRepository.findAllByDeleted(false, pageRequest);
        }
        ListResponse<TrustChainModel> response = new ListResponse<>();
        List<TrustChainModel> transformedModel = chains.getContent().stream().map(TransformationUtils::fromTrustChain).collect(Collectors.toList());
        response.setContent(transformedModel);
        response.setPage(pageRequest.getPageNumber());
        response.setPageSize(pageRequest.getPageSize());
        response.setTotalPages(chains.getTotalPages());
        response.setTotalElements(chains.getTotalElements());
        return response;
    }

    public Optional<TrustChain> update(Principal userPrincipal, UUID id, UpdateTrustChainCommand trustChain) {
        UUID tenantExtId = getTenantExtId(userPrincipal);
        Optional<TrustChain> existing;
        if (tenantExtId != null) {
            existing = trustChainRepository.findByIdAndTenantExtIdAndDeleted(id, tenantExtId, false);
        } else {
            existing = trustChainRepository.findByIdAndDeleted(id, false);
        }
        if (existing.isPresent()) {
            TrustChain existingTrustChain = existing.get();
            existingTrustChain.setDescription(trustChain.getDescription());
            existingTrustChain.setName(trustChain.getName());
            return Optional.of(trustChainRepository.save(existingTrustChain));
        } else {
            return Optional.empty();
        }
    }

    public Optional<TrustChain> delete(Principal userPrincipal, UUID id) {
        UUID tenantExtId = getTenantExtId(userPrincipal);
        Optional<TrustChain> existing;
        if (tenantExtId != null) {
            existing = trustChainRepository.findByIdAndTenantExtIdAndDeleted(id, tenantExtId, false);
        } else {
            existing = trustChainRepository.findByIdAndDeleted(id, false);
        }
        if (existing.isPresent()) {
            TrustChain trustChain = existing.get();
            trustChain.setDeleted(true);
            trustChainRepository.save(trustChain);
            return existing;
        } else {
            return Optional.empty();
        }
    }

    public TrustChainCountResponse count(Principal userPrincipal) {
        if (userPrincipal instanceof JWTAuthToken) {
            JWTAuthToken token = (JWTAuthToken) userPrincipal;
            TrustChainCountResponse count = new TrustChainCountResponse();
            switch (token.getGroup()) {
                case OPABS_ADMIN:
                    count.setTotal(trustChainRepository.count());
                    break;
                case TENANT_ADMIN:
                    UUID tenantExtId = token.getAccessToken().getTenantIdentifier();
                    count.setTotal(trustChainRepository.countByTenantExtId(tenantExtId));
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            return count;
        } else {
            throw new InternalServerErrorException();
        }
    }
}
