package com.opabs.tenent.management.service;

import com.opabs.common.model.ListResponse;
import com.opabs.tenent.management.controller.command.CreateTenantCommand;
import com.opabs.tenent.management.controller.command.UpdateTenantCommand;
import com.opabs.tenent.management.controller.response.CreateTenantResponse;
import com.opabs.tenent.management.controller.response.TenantListResponse;
import com.opabs.tenent.management.domain.ContactInfo;
import com.opabs.tenent.management.domain.Tenant;
import com.opabs.tenent.management.repository.AddressRepository;
import com.opabs.tenent.management.repository.ContactInfoRepository;
import com.opabs.tenent.management.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

        tenant = tenantRepository.save(tenant);

        ContactInfo contactInfo = fromContactInfoDTO(command.getContactInfo());
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
            ContactInfo contactInfo = fromContactInfoDTO(command.getContactInfo());
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
