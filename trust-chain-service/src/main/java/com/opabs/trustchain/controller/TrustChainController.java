package com.opabs.trustchain.controller;

import com.opabs.common.model.ListResponse;
import com.opabs.common.security.Permissions;
import com.opabs.trustchain.controller.command.CreateTrustChainCommand;
import com.opabs.trustchain.controller.command.UpdateTrustChainCommand;
import com.opabs.trustchain.controller.model.TrustChainModel;
import com.opabs.trustchain.domain.TrustChain;
import com.opabs.trustchain.service.TrustChainService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("trust-chain")
@RequiredArgsConstructor
public class TrustChainController {

    private final TrustChainService trustChainService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(Permissions.TRUST_CHAIN_CREATE)
    public TrustChainModel create(Principal userPrincipal, @RequestBody @Validated CreateTrustChainCommand command) {
        return trustChainService.create(userPrincipal, command);
    }

    @GetMapping("{id}")
    @Secured(Permissions.TRUST_CHAIN_VIEW)
    public ResponseEntity<TrustChainModel> show(Principal userPrincipal, @PathVariable("id") UUID id) {
        return ResponseEntity.ok(trustChainService.show(userPrincipal, id));
    }

    @GetMapping
    @Secured(Permissions.TRUST_CHAIN_VIEW)
    public ListResponse<TrustChainModel> list(Principal userPrincipal, @RequestParam(value = "page", defaultValue = "0") Integer page, @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return trustChainService.findAll(userPrincipal, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreated")));
    }

    @PutMapping("{id}")
    @Secured(Permissions.TRUST_CHAIN_EDIT)
    public ResponseEntity<TrustChain> update(Principal userPrincipal, @PathVariable("id") UUID id, @RequestBody UpdateTrustChainCommand command) {
        return trustChainService.update(userPrincipal, id, command).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Secured(Permissions.TRUST_CHAIN_DELETE)
    public ResponseEntity<TrustChain> delete(Principal userPrincipal, @PathVariable UUID id) {
        Optional<TrustChain> deleted = trustChainService.delete(userPrincipal, id);
        return deleted.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

}
