package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.RiskAssessmentDto;
import com.rutusoft.flowable.service.CreditAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/credit-assessment")
public class CreditAssessmentController {

    @Autowired
    private CreditAssessmentService creditAssessmentService;
    @GetMapping("/{customerId}")
    public ResponseEntity<RiskAssessmentDto> generateCreditAssessment(@PathVariable("customerId") String customerId) {
        return new ResponseEntity<>(creditAssessmentService.generateCreditAssessment(customerId), HttpStatus.OK);
    }
}
