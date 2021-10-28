package com.opabs.trustchain.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.opabs.common.model.ListResponse;
import com.opabs.trustchain.controller.command.CreateTrustChainCommand;
import com.opabs.trustchain.controller.command.UpdateTrustChainCommand;
import com.opabs.trustchain.domain.TrustChain;
import com.opabs.trustchain.service.TrustChainService;
import com.opabs.trustchain.views.TrustChainViews;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("trust-chain")
@RequiredArgsConstructor
public class TrustChainController {

    private final TrustChainService trustChainService;

    @JsonView(TrustChainViews.CertificateWithoutTrustChain.class)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrustChain create(@RequestBody @Validated CreateTrustChainCommand command) {
        return trustChainService.create(command);
    }

    @GetMapping("{id}")
    public ResponseEntity<TrustChain> show(@PathVariable("id") UUID id) {
        Optional<TrustChain> trustChain = trustChainService.findById(id);
        return trustChain.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ListResponse<TrustChain> list(@RequestParam(value = "page", defaultValue = "0") Integer page, @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return trustChainService.findAll(PageRequest.of(page, size));
    }

    @PutMapping("{id}")
    public ResponseEntity<TrustChain> update(@PathVariable("id") UUID id, @RequestBody UpdateTrustChainCommand command) {
        return trustChainService.update(id, command).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TrustChain> delete(@PathVariable UUID id) {
        Optional<TrustChain> deleted = trustChainService.delete(id);
        return deleted.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

}
