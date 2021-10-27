package com.opabs.trustchain.service;

import com.opabs.common.model.*;
import com.opabs.trustchain.controller.command.CreateTrustChainCommand;
import com.opabs.trustchain.domain.Certificate;
import com.opabs.trustchain.domain.TrustChain;
import com.opabs.trustchain.feign.CryptoService;
import com.opabs.trustchain.repository.CertificateRepository;
import com.opabs.trustchain.repository.TrustChainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.opabs.trustchain.utils.CertificateUtils.parsePemCertificate;
import static com.opabs.trustchain.utils.CompressionUtils.compress;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrustChainService {

    private final CryptoService cryptoService;

    private final TrustChainRepository trustChainRepository;

    private final CertificateRepository certificateRepository;

    public TrustChain create(CreateTrustChainCommand command) {

        //1. Create a root certificate using crypto service
        //2. Create TrustChain entity.
        //3. Save certificate and wrappedPrivateKey.
        //4. Return fully populated TrustChainEntity back to the client.

        GenerateCSRRequest request = getCSRRequest(command);
        GenerateCSRResponse csr = cryptoService.generateCSR(request);
        log.info("CSR Response: {}", csr.getPkcs10CSR());
        log.info("Wrapped Key: {}", csr.getWrappedKey());

        CertificateSigningRequest signingRequest = getSigningRequest(command, csr);

        CertificateSigningResponse signingResponse = cryptoService.signCSR(signingRequest);

        TrustChain trustChain = new TrustChain();
        trustChain.setName(command.getName());
        trustChain.setDescription(command.getDescription());

        trustChain = trustChainRepository.save(trustChain);

        Certificate certificate = new Certificate();
        certificate.setAnchor(true);
        byte[] content = parsePemCertificate(signingResponse.getCertificate());
        certificate.setContent(compress(content));
        byte[] wrappedKey = Base64.getDecoder().decode(csr.getWrappedKey());
        certificate.setWrappedPrivateKey(compress(wrappedKey));
        certificate.setTrustChain(trustChain);

        certificateRepository.save(certificate);

        return trustChain;
    }

    public Optional<TrustChain> findById(UUID id) {
        return trustChainRepository.findById(id);
    }

    private CertificateSigningRequest getSigningRequest(CreateTrustChainCommand command, GenerateCSRResponse csr) {
        CertificateSigningRequest signingRequest = new CertificateSigningRequest();
        signingRequest.setSelfSigned(true);
        signingRequest.setKeyUsages(command.getKeyUsages());
        signingRequest.setPkcs10CSR(csr.getPkcs10CSR());
        signingRequest.setSignatureAlgorithm(command.getSignatureAlgorithm());
        //TODO: Change this to use a valid key alias after implementation of key alias based keys.
        signingRequest.setUnwrappingKeyAlias("TENANT_SPECIFIC_KEY_ALIAS");
        signingRequest.setValidFrom(command.getValidFrom());
        signingRequest.setValidityInYears(command.getValidityInYears());
        signingRequest.setWrappedIssuerPrivateKey(csr.getWrappedKey());
        return signingRequest;
    }

    private GenerateCSRRequest getCSRRequest(CreateTrustChainCommand command) {
        GenerateCSRRequest request = new GenerateCSRRequest();
        request.setKeyType(command.getKeyType());
        request.setWrappingKeyAlias("aes-key-alias");
        request.setSubjectDN(command.getSubjectDistinguishedName());
        if (command.getKeyType() == KeyType.RSA) {
            request.setKeyGenParams(
                    Map.of("keySize", command.getKeySize().getLength())
            );
        } else {
            request.setKeyGenParams(
                    Map.of("namedCurve", command.getNamedCurve().name())
            );
        }
        return request;
    }

    public Iterable<TrustChain> findAll(PageRequest pageRequest) {
        return trustChainRepository.findAll(pageRequest);
    }

    public Optional<TrustChain> update(UUID id, TrustChain trustChain) {
        Optional<TrustChain> existing = trustChainRepository.findById(id);
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
        Optional<TrustChain> existing = trustChainRepository.findById(id);
        if (existing.isPresent()) {
            trustChainRepository.delete(existing.get());
            return existing;
        } else {
            return Optional.empty();
        }
    }

}
