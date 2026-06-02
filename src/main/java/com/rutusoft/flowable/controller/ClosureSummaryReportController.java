package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.service.ClosureSummaryReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/closure-summary-report")
@RequiredArgsConstructor
@Slf4j
public class ClosureSummaryReportController {
    private final ClosureSummaryReportService closureSummaryReportService;

    @GetMapping(
            value = "/process-instances/{processInstanceId}"
    )
    public ResponseEntity<String> createAndUploadClosureSummaryReport(
            @PathVariable String processInstanceId) {
        try {
            closureSummaryReportService.generateAndUploadClosureSummaryReport(processInstanceId);
        } catch (Exception e) {
            log.error("Error generating facility agreement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return new ResponseEntity<>("Closure summary report generated", HttpStatus.CREATED);
    }
}
