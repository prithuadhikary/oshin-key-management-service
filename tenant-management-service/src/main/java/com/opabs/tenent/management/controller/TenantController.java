package com.opabs.tenent.management.controller;

import com.opabs.common.model.ListResponse;
import com.opabs.tenent.management.controller.command.CreateTenantCommand;
import com.opabs.tenent.management.controller.command.UpdateTenantCommand;
import com.opabs.tenent.management.controller.response.CreateTenantResponse;
import com.opabs.tenent.management.domain.Tenant;
import com.opabs.tenent.management.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public CreateTenantResponse create(@RequestBody @Validated CreateTenantCommand command) {
        return tenantService.createTenant(command);
    }

    @GetMapping
    public ListResponse<Tenant> list(@RequestParam(value = "page", defaultValue = "0") Integer page, @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return tenantService.findAll(PageRequest.of(page, size));
    }

    @GetMapping("{id}")
    public ResponseEntity<Tenant> show(@PathVariable("id") UUID id) {
        Optional<Tenant> tenant = tenantService.findById(id);
        return tenant.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<Tenant> update(@PathVariable("id") UUID id, @RequestBody @Validated UpdateTenantCommand command) {
        return tenantService.update(id, command).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Tenant> delete(@PathVariable UUID id) {
        Optional<Tenant> deleted = tenantService.delete(id);
        return deleted.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

}
