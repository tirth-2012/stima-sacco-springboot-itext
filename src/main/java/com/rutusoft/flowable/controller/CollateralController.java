package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.CollateralRequest;
import com.rutusoft.flowable.entity.Collateral;
import com.rutusoft.flowable.service.CollateralService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collaterals")
@RequiredArgsConstructor
public class CollateralController {

    private final CollateralService collateralService;

    // Create
    @PostMapping("/{cifNumber}")
    public Collateral create(
            @PathVariable String cifNumber,
            @RequestBody CollateralRequest request
    ) {
        return collateralService.createCollateral(cifNumber, request);
    }

    // Update
    @PutMapping("/{cifNumber}/{collateralId}")
    public Collateral update(
            @PathVariable String cifNumber,
            @PathVariable Long collateralId,
            @RequestBody CollateralRequest request
    ) {
        return collateralService.updateCollateral(cifNumber, collateralId, request);
    }

    // Delete
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        collateralService.deleteCollateral(id);
        return "Deleted successfully";
    }

    // Get One
    @GetMapping("/{id}")
    public Collateral getById(@PathVariable Long id) {
        return collateralService.getCollateralById(id);
    }

    // Get All by CIF
    @GetMapping("/customer/{cifNumber}")
    public List<Collateral> getByCif(@PathVariable String cifNumber) {
        return collateralService.getAllByCif(cifNumber);
    }

    // Get All by Process Instance ID
    @GetMapping("/process-instance/{processInstanceId}")
    public List<Collateral> getByProcessInstanceId(
            @PathVariable String processInstanceId
    ) {
        return collateralService.getByProcessInstanceId(processInstanceId);
    }
}