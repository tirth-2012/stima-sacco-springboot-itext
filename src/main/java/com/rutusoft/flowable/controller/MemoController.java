package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("memo")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService legalMemoService;

    @GetMapping("/legal/{processInstanceId}")
    public ResponseEntity<byte[]> downloadLegal(@PathVariable String processInstanceId) throws Exception {

        byte[] pdf = legalMemoService.generateLegalMemo(processInstanceId);

        return buildResponse(pdf, "legal-memo-" + processInstanceId + ".pdf");
    }

    @GetMapping("/rca/{processInstanceId}")
    public ResponseEntity<byte[]> downloadRca(@PathVariable String processInstanceId) throws Exception {

        byte[] pdf = legalMemoService.generateRcaMemo(processInstanceId);

        return buildResponse(pdf, "rca-memo-" + processInstanceId + ".pdf");
    }

    private ResponseEntity<byte[]> buildResponse(byte[] pdf, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}