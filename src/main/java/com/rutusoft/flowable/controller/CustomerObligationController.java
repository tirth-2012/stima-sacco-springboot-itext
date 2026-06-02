package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.CustomerObligationRequestDto;
import com.rutusoft.flowable.dto.CustomerObligationResponseDto;
import com.rutusoft.flowable.service.CustomerObligationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customer-obligations")
public class CustomerObligationController {

    private final CustomerObligationService service;

    public CustomerObligationController(CustomerObligationService service) {
        this.service = service;
    }

    // 🔹 CREATE
    @PostMapping
    public ResponseEntity<CustomerObligationResponseDto> create(
            @Valid @RequestBody CustomerObligationRequestDto dto) {

        return ResponseEntity.ok(service.create(dto));
    }

    // 🔹 GET ALL
    @GetMapping
    public ResponseEntity<List<CustomerObligationResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 🔹 GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerObligationResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // 🔹 GET BY CIF 🔥
    @GetMapping("/cif/{cifNumber}")
    public ResponseEntity<List<CustomerObligationResponseDto>> getByCif(
            @PathVariable String cifNumber) {

        return ResponseEntity.ok(service.getByCif(cifNumber));
    }

    // 🔹 UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<CustomerObligationResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerObligationRequestDto dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    // 🔹 DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.ok("Obligation deleted successfully");
    }
}