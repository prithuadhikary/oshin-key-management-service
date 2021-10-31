package com.opabs.trustchain.controller;

import com.opabs.trustchain.controller.command.CreateCertificateCommand;
import com.opabs.trustchain.controller.responses.CreateCertificateResponse;
import com.opabs.trustchain.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("certificate")
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping
    public CreateCertificateResponse createCertificate(@RequestBody CreateCertificateCommand command) {
        return certificateService.createCertificate(command);
    }

    @GetMapping("download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(certificateService.getCertificateContent(id));
    }

}
