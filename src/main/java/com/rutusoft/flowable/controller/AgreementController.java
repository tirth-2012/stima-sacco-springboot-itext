package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.service.AgreementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("agreement")
@RequiredArgsConstructor
@Slf4j
public class AgreementController {

    private final AgreementService agreementService;

    // =========================================================
    // ✅ CONVENTIONAL AGREEMENT
    // =========================================================
    @GetMapping(
            value = "/conventional/{processInstanceId}",
            produces = { MediaType.APPLICATION_PDF_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE }
    )
    public ResponseEntity<byte[]> downloadFacilityAgreement(
            @PathVariable String processInstanceId) {

        validate(processInstanceId);

        try {
            byte[] pdf = agreementService.generateFacilityAgreement(processInstanceId);

            return buildResponse(pdf, "facility-agreement-" + processInstanceId + ".pdf");

        } catch (Exception e) {
            log.error("Error generating facility agreement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =========================================================
    // ✅ WAKALA AGREEMENT
    // =========================================================
    @GetMapping(
            value = "/wakala/{processInstanceId}",
            produces = { MediaType.APPLICATION_PDF_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE }
    )
    public ResponseEntity<byte[]> downloadWakalaAgreement(
            @PathVariable String processInstanceId) {

        validate(processInstanceId);

        try {
            byte[] pdf = agreementService.generateWakalaAgreement(processInstanceId);

            return buildResponse(pdf, "wakala-agreement-" + processInstanceId + ".pdf");

        } catch (Exception e) {
            log.error("Error generating wakala agreement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =========================================================
    // COMMON RESPONSE BUILDER
    // =========================================================
    private ResponseEntity<byte[]> buildResponse(byte[] pdf, String filename) {

        if (pdf == null || pdf.length == 0) {
            return ResponseEntity.noContent().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdf.length);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(filename).build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    private void validate(String processInstanceId) {
        if (processInstanceId == null || processInstanceId.trim().isEmpty()) {
            throw new IllegalArgumentException("processInstanceId must not be null or empty");
        }
    }
}