package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.service.JasperReportService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
public class JasperReportController {
    private final JasperReportService jasperReportService;

    @GetMapping(value = "/legal-memo/{processInstanceId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateLegalMemo(@PathVariable String processInstanceId) {

        byte[] pdf = jasperReportService.generateLegalMemo(processInstanceId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=legal_memo.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
