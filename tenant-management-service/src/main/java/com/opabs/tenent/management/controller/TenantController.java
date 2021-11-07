package com.opabs.tenent.management.controller;

import com.opabs.common.model.ListResponse;
import com.opabs.tenent.management.controller.command.CreateTenantCommand;
import com.opabs.tenent.management.controller.command.UpdateTenantCommand;
import com.opabs.tenent.management.controller.response.CreateTenantResponse;
import com.opabs.tenent.management.domain.Tenant;
import com.opabs.tenent.management.security.Permissions;
import com.opabs.tenent.management.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(Permissions.TENANT_CREATE)
    public CreateTenantResponse create(@RequestBody @Validated CreateTenantCommand command) {
        return tenantService.createTenant(command);
    }

    @GetMapping
    @Secured(Permissions.TENANT_VIEW)
    public ListResponse<Tenant> list(@RequestParam(value = "page", defaultValue = "0") Integer page, @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return tenantService.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateUpdated")));
    }

    @GetMapping("{id}")
    public ResponseEntity<Tenant> show(@PathVariable("id") UUID id) {
        Optional<Tenant> tenant = tenantService.findById(id);
        return tenant.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    @Secured(Permissions.TENANT_EDIT)
    public ResponseEntity<Tenant> update(@PathVariable("id") UUID id, @RequestBody @Validated UpdateTenantCommand command) {
        return tenantService.update(id, command).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Secured(Permissions.TENANT_DELETE)
    public ResponseEntity<Tenant> delete(@PathVariable UUID id) {
        Optional<Tenant> deleted = tenantService.delete(id);
        return deleted.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

}
