package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.service.OfferLetterService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping("offer-letter")
public class OfferLetterController {

    private final OfferLetterService offerLetterService;

    public OfferLetterController(OfferLetterService offerLetterService) {
        this.offerLetterService = offerLetterService;
    }

    @Operation(summary = "Download Offer Letter PDF")
    @GetMapping(
            value = "/{processInstanceId}",
            produces = { MediaType.APPLICATION_PDF_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE }
    )
    public ResponseEntity<byte[]> downloadFile(@PathVariable String processInstanceId) {

        if (processInstanceId == null || processInstanceId.trim().isEmpty()) {
            throw new IllegalArgumentException("processInstanceId must not be null or empty");
        }

        try {
            byte[] pdfBytes = offerLetterService.generateOfferLetter(processInstanceId);

            if (pdfBytes == null || pdfBytes.length == 0) {
                log.warn("Generated PDF is empty for processInstanceId={}", processInstanceId);
                return ResponseEntity.noContent().build();
            }

            String fileName = "offer-letter-" + processInstanceId + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(pdfBytes.length);
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename(fileName)
                            .build()
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception ex) {
            log.error("Error generating offer letter for processInstanceId={}", processInstanceId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
