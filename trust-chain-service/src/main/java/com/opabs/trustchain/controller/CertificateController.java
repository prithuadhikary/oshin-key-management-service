package com.opabs.trustchain.controller;

import com.opabs.common.model.ListResponse;
import com.opabs.common.security.Permissions;
import com.opabs.trustchain.controller.command.CreateCertificateCommand;
import com.opabs.trustchain.controller.model.CertificateModel;
import com.opabs.trustchain.controller.responses.CreateCertificateResponse;
import com.opabs.trustchain.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("certificate")
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping
    @Secured(Permissions.CERTIFICATE_CREATE)
    public CreateCertificateResponse createCertificate(@RequestBody @Validated CreateCertificateCommand command) {
        return certificateService.createCertificate(command);
    }

    @Secured(Permissions.CERTIFICATE_VIEW)
    @GetMapping("download/der/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(certificateService.getCertificateContent(id));
    }

    @Secured(Permissions.CERTIFICATE_VIEW)
    @GetMapping("download/p7b/{id}")
    public ResponseEntity<byte[]> downloadCertChain(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(certificateService.getCertificateChainContent(id));
    }

    @GetMapping
    @Secured(Permissions.CERTIFICATE_VIEW)
    public ListResponse<CertificateModel> list(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "parentCertificateId", required = false) UUID parentCertificateId
    ) {
        return certificateService.list(search, parentCertificateId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreated")));
    }

}
