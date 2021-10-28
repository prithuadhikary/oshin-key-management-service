package com.opabs.tenent.management.service;

import com.opabs.tenent.management.controller.command.CreateTenantCommand;
import com.opabs.tenent.management.controller.command.UpdateTenantCommand;
import com.opabs.tenent.management.controller.response.CreateTenantResponse;
import com.opabs.tenent.management.domain.ContactInfo;
import com.opabs.tenent.management.domain.Tenant;
import com.opabs.tenent.management.repository.AddressRepository;
import com.opabs.tenent.management.repository.ContactInfoRepository;
import com.opabs.tenent.management.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.opabs.tenent.management.util.TransformationUtils.fromContactInfoDTO;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    private final ContactInfoRepository contactInfoRepository;

    private final AddressRepository addressRepository;

    public CreateTenantResponse createTenant(CreateTenantCommand command) {
        Tenant tenant = new Tenant();
        tenant.setName(command.getName());

        ContactInfo contactInfo = fromContactInfoDTO(command.getContactInfo());
        contactInfoRepository.save(contactInfo);

        tenant.setContactInfo(contactInfo);

        tenant = tenantRepository.save(tenant);

        CreateTenantResponse response = new CreateTenantResponse();
        response.setId(tenant.getId());

        return response;
    }

    public Iterable<Tenant> findAll(PageRequest pageRequest) {
        return tenantRepository.findAll(pageRequest);
    }

    public Optional<Tenant> findById(UUID id) {
        return tenantRepository.findById(id);
    }

    public Optional<Tenant> update(UUID id, UpdateTenantCommand command) {
        Optional<Tenant> existing = tenantRepository.findById(id);
        return existing.map(tenant -> {
            tenant.setName(command.getName());
            ContactInfo contactInfo = fromContactInfoDTO(command.getContactInfo());
            addressRepository.deleteAll(tenant.getContactInfo().getAddresses());
            tenant.setContactInfo(contactInfo);
            contactInfoRepository.save(tenant.getContactInfo());
            tenantRepository.save(tenant);
            return Optional.of(tenant);
        }).orElse(Optional.empty());
    }

    public Optional<Tenant> delete(UUID id) {
        Optional<Tenant> existing = tenantRepository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        } else {
            tenantRepository.delete(existing.get());
            return existing;
        }
    }
}
