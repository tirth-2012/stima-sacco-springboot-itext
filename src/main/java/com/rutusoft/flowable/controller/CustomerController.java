package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.CustomerRequestDto;
import com.rutusoft.flowable.dto.CustomerResponseDto;
import com.rutusoft.flowable.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Tag(
        name = "Customer APIs",
        description = "APIs for managing customers"
)
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // 🔹 CREATE CUSTOMER
    @Operation(summary = "Create Customer")
    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(
            @Valid @RequestBody CustomerRequestDto requestDto) {

        CustomerResponseDto response = customerService.createCustomer(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 🔹 GET ALL CUSTOMERS
    @Operation(summary = "Get All Customers")
    @GetMapping
    public ResponseEntity<Page<CustomerResponseDto>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(customerService.getAllCustomers(page, size));
    }

    // 🔹 GET CUSTOMER BY ID
    @Operation(summary = "Get Customer By ID")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    // 🔹 SEARCH CUSTOMERS
    @Operation(summary = "Search Customer Details by CIF, National ID, or Mobile Number")
    @Validated
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponseDto>> searchCustomers(

            @RequestParam @NotBlank(message = "CIF number is mandatory")
            String cifNumber,

            @RequestParam(required = false)
            String nationalId,

            @RequestParam(required = false)
            String mobileNumber
    ) {

        return ResponseEntity.ok(
                customerService.searchCustomers(cifNumber, nationalId, mobileNumber)
        );
    }

    // 🔹 UPDATE CUSTOMER
    @Operation(summary = "Update Customer")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto requestDto) {

        return ResponseEntity.ok(customerService.updateCustomer(id, requestDto));
    }

    // 🔹 DELETE CUSTOMER
    @Operation(summary = "Delete Customer")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {

        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully");
    }

    // ------------------------------------------------------------------------
    // Search Customer by CIF or Full Name
    // ------------------------------------------------------------------------
    @Operation(summary = "Search customer by CIF or full name")
    @GetMapping("/search/{fullname}/{cifnumber}")
    public ResponseEntity<List<CustomerResponseDto>> searchCustomers(
            @RequestParam(required = false) String fullname,
            @RequestParam(required = false) String cifnumber
    ) {
        return ResponseEntity.ok(
                customerService.searchByNameAndCif(fullname, cifnumber)
        );
    }

    // ------------------------------------------------------------------------
    // Recalculate Customer Loan Limit
    // ------------------------------------------------------------------------
    @Operation(summary = "Recalculate customer available loan amount")
    @GetMapping("/{cifNumber}/loan-limit/recalculate")
    public ResponseEntity<CustomerResponseDto> recalculateLoanLimit(
            @PathVariable String cifNumber
    ) {
        return ResponseEntity.ok(
                customerService.recalculateLoanLimit(cifNumber)
        );
    }
}