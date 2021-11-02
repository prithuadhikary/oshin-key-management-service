package com.opabs.trustchain.utils;

import com.opabs.trustchain.controller.model.CertificateModel;
import com.opabs.trustchain.controller.model.TrustChainModel;
import com.opabs.trustchain.domain.Certificate;
import com.opabs.trustchain.domain.TrustChain;
import com.opabs.trustchain.exception.InternalServerErrorException;
import com.opabs.trustchain.model.CertificateInfo;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneOffset;

import static com.opabs.trustchain.utils.CompressionUtils.uncompress;

@Slf4j
public class TransformationUtils {

    public static TrustChainModel fromTrustChain(TrustChain trustChain) {
        TrustChainModel trustChainModel = new TrustChainModel();
        trustChainModel.setId(trustChain.getId());
        trustChainModel.setDateCreated(trustChain.getDateCreated());
        trustChainModel.setDateUpdated(trustChain.getDateUpdated());
        trustChainModel.setDeleted(trustChain.isDeleted());
        trustChainModel.setName(trustChain.getName());
        trustChainModel.setDescription(trustChain.getDescription());
        trustChainModel.setRootCertificate(fromCertificate(trustChain.getRootCertificate()));
        trustChainModel.setTenantExtId(trustChain.getTenantExtId());

        return trustChainModel;
    }

    public static CertificateModel fromCertificate(Certificate certificate) {
        CertificateModel certificateModel = new CertificateModel();
        certificateModel.setId(certificate.getId());
        certificateModel.setAnchor(certificate.isAnchor());
        certificateModel.setCertificateFingerprint(certificate.getCertificateFingerprint());
        certificateModel.setPublicKeyFingerprint(certificate.getPublicKeyFingerprint());
        certificateModel.setDateCreated(certificate.getDateCreated());
        certificateModel.setDateUpdated(certificate.getDateUpdated());
        certificateModel.setKeyType(certificate.getKeyType());
        if (certificate.getParentCertificate() != null) {
            certificateModel.setParentCertificateId(certificate.getParentCertificate().getId());
        }
        certificateModel.setTrustChainId(certificate.getTrustChain().getId());

        try {
            CertificateInfo certificateInfo = CertificateUtils.getCertificateInfo(uncompress(certificate.getContent()));
            certificateModel.setIssuerDistinguishedName(certificateInfo.getIssuerDistinguishedName());
            certificateModel.setSubjectDistinguishedName(certificateInfo.getSubjectDistinguishedName());
            certificateModel.setValidFrom(certificateInfo.getValidFrom().toInstant().atOffset(ZoneOffset.UTC));
            certificateModel.setValidUpto(certificateInfo.getValidUpto().toInstant().atOffset(ZoneOffset.UTC));
            certificateModel.setExpired(certificateInfo.isExpired());
            certificateModel.setNotYetValid(certificateInfo.isNotYetValid());
            certificateModel.setKeyUsages(certificateInfo.getKeyUsages());
            certificateModel.setKeyLength(certificateInfo.getKeyLength());
            certificateModel.setNamedCurve(certificateInfo.getNamedCurve());
        } catch (Exception ex) {
            log.error("Error occurred while parsing certificate.", ex);
            throw new InternalServerErrorException();
        }

        return certificateModel;
    }

    private TransformationUtils() {
    }
}
