package com.opabs.tenant.management.service;

import com.opabs.common.model.ListResponse;
import com.opabs.tenant.management.util.TransformationUtils;
import com.opabs.tenant.management.controller.command.CreateTenantCommand;
import com.opabs.tenant.management.controller.command.UpdateTenantCommand;
import com.opabs.tenant.management.controller.response.CreateTenantResponse;
import com.opabs.tenant.management.domain.ContactInfo;
import com.opabs.tenant.management.domain.Tenant;
import com.opabs.tenant.management.repository.AddressRepository;
import com.opabs.tenant.management.repository.ContactInfoRepository;
import com.opabs.tenant.management.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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

    public ListResponse<Tenant> findAll(PageRequest pageRequest) {
        Page<Tenant> tenants = tenantRepository.findAllByDeleted(false, pageRequest);
        //TODO: Get certificate info data from the trust chain service and populate tenantlistreposne.
        ListResponse<Tenant> response = new ListResponse<>();
        response.setContent(tenants.getContent());
        response.setPage(pageRequest.getPageNumber());
        response.setPageSize(pageRequest.getPageSize());
        response.setTotalPages(tenants.getTotalPages());
        response.setTotalElements(tenants.getTotalElements());

        return response;
    }

    public Optional<Tenant> findById(UUID id) {
        return tenantRepository.findByIdAndDeleted(id, false);
    }

    public Optional<Tenant> update(UUID id, UpdateTenantCommand command) {
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
}
