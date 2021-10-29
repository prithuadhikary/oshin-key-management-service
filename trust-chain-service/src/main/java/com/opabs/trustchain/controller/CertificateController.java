package com.opabs.trustchain.controller;

import com.opabs.trustchain.controller.command.CreateCertificateCommand;
import com.opabs.trustchain.controller.responses.CreateCertificateResponse;
import com.opabs.trustchain.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("certificate")
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping
    public CreateCertificateResponse createCertificate(@RequestBody CreateCertificateCommand command) {
        return certificateService.createCertificate(command);
    }

}
