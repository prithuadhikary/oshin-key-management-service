package com.opabs.tenant.management.service;

import com.opabs.common.model.ListResponse;
import com.opabs.common.security.AccessToken;
import com.opabs.common.security.GroupPermissions;
import com.opabs.common.security.JWTAuthToken;
import com.opabs.tenant.management.controller.command.CreateTenantCommand;
import com.opabs.tenant.management.controller.command.UpdateTenantCommand;
import com.opabs.tenant.management.controller.response.CreateTenantResponse;
import com.opabs.tenant.management.controller.response.TenantCountResponse;
import com.opabs.tenant.management.domain.ContactInfo;
import com.opabs.tenant.management.domain.Tenant;
import com.opabs.tenant.management.repository.AddressRepository;
import com.opabs.tenant.management.repository.ContactInfoRepository;
import com.opabs.tenant.management.repository.TenantRepository;
import com.opabs.tenant.management.util.TransformationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    private final ContactInfoRepository contactInfoRepository;

    private final AddressRepository addressRepository;

    public CreateTenantResponse createTenant(CreateTenantCommand command) {
        Tenant tenant = new Tenant();
        tenant.setName(command.getName());

        tenant = tenantRepository.save(tenant);

        ContactInfo contactInfo = TransformationUtils.fromContactInfoDTO(command.getContactInfo());
        contactInfo.setTenant(tenant);
        contactInfoRepository.save(contactInfo);

        contactInfo.getAddresses().forEach(address -> address.setContactInfo(contactInfo));

        addressRepository.saveAll(contactInfo.getAddresses());

        tenant.setContactInfo(contactInfo);

        tenantRepository.save(tenant);

        CreateTenantResponse response = new CreateTenantResponse();
        response.setId(tenant.getId());

        return response;
    }

    public ListResponse<Tenant> findAll(Principal userPrincipal, PageRequest pageRequest) {
        Page<Tenant> tenants = null;
        if (userPrincipal instanceof JWTAuthToken) {
            JWTAuthToken authToken = (JWTAuthToken) userPrincipal;
            AccessToken accessToken = authToken.getAccessToken();
            if (authToken.getGroup() == GroupPermissions.TENANT_ADMIN) {
                UUID tenantIdentifier = accessToken.getTenantIdentifier();
                tenants = tenantRepository.findAllByDeletedAndId(false, tenantIdentifier, pageRequest);
            } else if (authToken.getGroup() == GroupPermissions.OPABS_ADMIN) {
                tenants = tenantRepository.findAllByDeleted(false, pageRequest);
            }
        }
        //TODO: Get certificate info data from the trust chain service and populate tenantlistreposne.
        ListResponse<Tenant> response = new ListResponse<>();
        response.setContent(tenants.getContent());
        response.setPage(pageRequest.getPageNumber());
        response.setPageSize(pageRequest.getPageSize());
        response.setTotalPages(tenants.getTotalPages());
        response.setTotalElements(tenants.getTotalElements());

        return response;
    }

    public Optional<Tenant> findById(Principal principal, UUID id) {
        if (principal instanceof JWTAuthToken) {
            JWTAuthToken token = (JWTAuthToken) principal;
            UUID tenantIdentifier = token.getAccessToken().getTenantIdentifier();
            if (token.getGroup() == GroupPermissions.TENANT_ADMIN && (tenantIdentifier == null || tenantIdentifier != id)) {
                return Optional.empty();
            }
            return tenantRepository.findByIdAndDeleted(id, false);
        } else {
            return Optional.empty();
        }
    }

    public Optional<Tenant> update(Principal userPrincipal, UUID id, UpdateTenantCommand command) {
        if (userPrincipal instanceof JWTAuthToken) {
            JWTAuthToken token = (JWTAuthToken) userPrincipal;
            if (token.getGroup() == GroupPermissions.TENANT_ADMIN && token.getAccessToken().getTenantIdentifier() != id) {
                return Optional.empty();
            }
        }
        Optional<Tenant> existing = tenantRepository.findById(id);
        return existing.map(tenant -> {
            tenant.setName(command.getName());
            addressRepository.deleteAll(tenant.getContactInfo().getAddresses());
            contactInfoRepository.delete(tenant.getContactInfo());
            ContactInfo contactInfo = TransformationUtils.fromContactInfoDTO(command.getContactInfo());
            contactInfo.setTenant(tenant);
            contactInfoRepository.save(contactInfo);
            tenant.setContactInfo(contactInfo);
            return Optional.of(tenant);
        }).orElse(Optional.empty());
    }

    public Optional<Tenant> delete(UUID id) {
        Optional<Tenant> existing = tenantRepository.findByIdAndDeleted(id, false);
        if (existing.isEmpty()) {
            return Optional.empty();
        } else {
            Tenant entity = existing.get();
            entity.setDeleted(true);
            tenantRepository.save(entity);
            return existing;
        }
    }

    public TenantCountResponse count() {
        TenantCountResponse response = new TenantCountResponse();
        response.setTotal(tenantRepository.count());
        return response;
    }
}
