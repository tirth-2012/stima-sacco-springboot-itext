package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.EligibilityDTO;
import com.rutusoft.flowable.service.EligibilityService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/eligibility")
@RequiredArgsConstructor
public class EligibilityController {
    private final EligibilityService eligibilityService;

    @GetMapping("/{customerId}")
    public ResponseEntity<EligibilityDTO> getCustomerEligibility(
            @Parameter(description = "Customer Id", required = true)
            @PathVariable("customerId") String customerId) {
        return new ResponseEntity<>(eligibilityService.fetchEligibility(customerId), HttpStatus.OK);
    }
}
