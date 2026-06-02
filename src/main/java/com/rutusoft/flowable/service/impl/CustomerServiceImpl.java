package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.CustomerObligationResponseDto;
import com.rutusoft.flowable.dto.CustomerRequestDto;
import com.rutusoft.flowable.dto.CustomerResponseDto;
import com.rutusoft.flowable.entity.Customer;
import com.rutusoft.flowable.entity.CustomerObligation;
import com.rutusoft.flowable.repository.CustomerRepository;
import com.rutusoft.flowable.service.CustomerService;
import com.rutusoft.flowable.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.rutusoft.flowable.service.HistoryProcessInstanceService;
@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final HistoryProcessInstanceService historyProcessInstanceService;

    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            HistoryProcessInstanceService historyProcessInstanceService
    ) {
        this.customerRepository = customerRepository;
        this.historyProcessInstanceService = historyProcessInstanceService;
    }
    // 🔹 CREATE
    @Override
    public CustomerResponseDto createCustomer(CustomerRequestDto dto) {

        log.info("Incoming request: {}", dto);

        log.info("Creating customer with nationalId: {}", dto.getNationalId());

        try {

            // Duplicate checks
            /*if (customerRepository.findByNationalId(dto.getNationalId()).isPresent()) {
                log.error("Customer already exists with nationalId: {}", dto.getNationalId());
                //throw new ValidationException("Customer already exists with this National ID");
            }

            if (dto.getEmail() != null &&
                    customerRepository.findByEmail(dto.getEmail()).isPresent()) {
                log.error("Email already exists: {}", dto.getEmail());
                //throw new ValidationException("Email already exists");
            }

            if (dto.getMobileNumber() != null &&
                    customerRepository.findByMobileNumber(dto.getMobileNumber()).isPresent()) {
                log.error("Mobile already exists: {}", dto.getMobileNumber());
                //throw new ValidationException("Mobile number already exists");
            }


             */
            if (dto.getCifNumber() != null &&
                    customerRepository.findByCifNumber(dto.getCifNumber()).isPresent()) {
                log.error("CIF already exists: {}", dto.getCifNumber());
                throw new ValidationException("CIF already exists");
            }

            // Business Conditions
            if (Boolean.TRUE.equals(dto.getExistingCustomer()) &&
                    (dto.getCifNumber() == null || dto.getCifNumber().trim().isEmpty())) {
                log.error("CIF missing or empty for existing customer");
                throw new ValidationException("CIF is required for existing customer");
            }

            if ((dto.getEmail() == null || dto.getEmail().trim().isEmpty()) &&
                    (dto.getMobileNumber() == null || dto.getMobileNumber().trim().isEmpty())) {
                log.error("Both email and mobile missing");
                throw new ValidationException("Either Email or Mobile Number is required");
            }

            if (Boolean.TRUE.equals(dto.getKycVerified()) &&
                    !"ACTIVE".equalsIgnoreCase(dto.getStatus())) {
                log.error("Invalid status for KYC verified customer");
                throw new ValidationException("KYC verified customer must be ACTIVE");
            }

            // Mapping
            Customer customer = mapToEntity(dto);

            // Initialize available limit same as total limit
            if (customer.getLoanAmountLimit() != null) {
                customer.setAvailableLoanLimit(customer.getLoanAmountLimit());
            }

            // 🔹 Handle obligations
            if (dto.getObligations() != null && !dto.getObligations().isEmpty()) {

                List<CustomerObligation> obligations = dto.getObligations()
                        .stream()
                        .map(o -> {
                            CustomerObligation entity = new CustomerObligation();

                            entity.setCustomer(customer); // VERY IMPORTANT
                            entity.setCifNumber(o.getCifNumber());
                            entity.setLender(o.getLender());
                            entity.setFacilityType(o.getFacilityType());
                            entity.setOutstanding(o.getOutstanding());
                            entity.setMonthlyCommitment(o.getMonthlyCommitment());
                            entity.setSource(o.getSource());
                            entity.setStatus(o.getStatus());

                            return entity;
                        })
                        .collect(Collectors.toList());

                for (CustomerObligation o : obligations) {
                    o.setCustomer(customer);   // ensure relation
                }

                customer.setObligations(obligations);
            }

            Customer saved = customerRepository.save(customer);

            log.info("Customer created successfully with ID: {}", saved.getId());

            return mapToResponse(saved);

        } catch (ValidationException e) {
            log.error("Validation error while creating customer: {}", e.getMessage());
            throw e; // rethrow same
        } catch (Exception e) {
            log.error("Unexpected error while creating customer", e);
            throw new RuntimeException("Internal server error", e);
        }
    }

    // 🔹 GET ALL
    @Override
    public Page<CustomerResponseDto> getAllCustomers(int page, int size) {

        log.info("Fetching customers with pagination: page={}, size={}", page, size);

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50); // limit max size to 50

        Pageable pageable = PageRequest.of(safePage, safeSize);

        Page<Customer> customerPage = customerRepository.findAll(pageable);

        return customerPage.map(this::mapToResponse);
    }

    // 🔹 GET BY ID
    @Override
    public CustomerResponseDto getCustomerById(Long id) {

        log.info("Fetching customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Customer not found"));

        return mapToResponse(customer);
    }

    // 🔹 UPDATE
    @Override
    public CustomerResponseDto updateCustomer(Long id, CustomerRequestDto dto) {

        log.info("Updating customer with ID: {}", id);

        try {

            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new ValidationException("Customer not found"));

            customer.setFullName(dto.getFullName());
            customer.setGender(dto.getGender());
            customer.setDateOfBirth(dto.getDateOfBirth());
            customer.setEmail(dto.getEmail());
            customer.setMobileNumber(dto.getMobileNumber());
            customer.setStatus(dto.getStatus());

            Customer updated = customerRepository.save(customer);

            log.info("Customer updated successfully");

            return mapToResponse(updated);

        } catch (ValidationException e) {
            log.error("Validation error while updating customer: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating customer", e);
            throw new RuntimeException("Internal server error", e);
        }
    }

    @Override
    public List<CustomerResponseDto> searchCustomers(
            String cifNumber,
            String nationalId,
            String mobileNumber) {

        String cif = cifNumber != null ? cifNumber.trim() : null;
        String nid = nationalId != null ? nationalId.trim() : null;
        String mobile = mobileNumber != null ? mobileNumber.trim() : null;

        log.info("Search request - cif: {}, nationalId: {}, mobile: {}", cif, nid, mobile);

        try {

            //  CIF Mandatory
            if (cif == null || cif.isEmpty()) {
                throw new ValidationException("CIF number is mandatory");
            }

            // 🔹 Fetch base customer
            Customer customer = customerRepository.findByCifNumber(cif)
                    .orElseThrow(() -> new ValidationException("Invalid CIF number"));

            // 🔹 Validate National ID
            if (nid != null && !nid.isEmpty()) {

                Customer nationalCustomer = customerRepository.findByNationalId(nid)
                        .orElseThrow(() -> new ValidationException("National ID not found"));

                if (!Objects.equals(nationalCustomer.getId(), customer.getId())) {
                    throw new ValidationException("National ID does not belong to given CIF");
                }
            }

            // 🔹 Validate Mobile Number
            if (mobile != null && !mobile.isEmpty()) {

                Customer mobileCustomer = customerRepository.findByMobileNumber(mobile)
                        .orElseThrow(() -> new ValidationException("Mobile number not found"));

                if (!Objects.equals(mobileCustomer.getId(), customer.getId())) {
                    throw new ValidationException("Mobile number does not belong to given CIF");
                }
            }

            log.info("Customer validated successfully for CIF: {}", cif);

            return List.of(mapToResponse(customer));

        } catch (ValidationException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while searching customers", e);
            throw new RuntimeException("Internal server error", e);
        }
    }

    @Override
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        try {
            customerRepository.delete(customer);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException(
                    "Cannot delete customer. It is linked with other records."
            );
        }
    }

    // 🔹 MAPPER
    private Customer mapToEntity(CustomerRequestDto dto) {

        Customer c = new Customer();

        c.setFullName(dto.getFullName());
        c.setGender(dto.getGender());
        c.setDateOfBirth(dto.getDateOfBirth());
        c.setNationalId(dto.getNationalId());
        c.setKraPin(dto.getKraPin());
        c.setEmail(dto.getEmail());
        c.setMobileNumber(dto.getMobileNumber());
        c.setPhysicalAddress(dto.getPhysicalAddress());
        c.setPostalAddress(dto.getPostalAddress());
        c.setNationality(dto.getNationality());
        c.setMaritalStatus(dto.getMaritalStatus());
        c.setCifNumber(dto.getCifNumber());
        c.setCustomerType(dto.getCustomerType());
        c.setAccountSince(dto.getAccountSince());
        c.setExistingCustomer(dto.getExistingCustomer());
        c.setKycVerified(dto.getKycVerified());
        c.setStatus(dto.getStatus());
        c.setIntakeChannel(dto.getIntakeChannel());
        c.setRelationshipManager(dto.getRelationshipManager());
        c.setExistingFacilities(dto.getExistingFacilities());
        c.setTotalExposure(dto.getTotalExposure());
        c.setRepaymentRecord(dto.getRepaymentRecord());
        c.setLastFacility(dto.getLastFacility());
        c.setBankName(dto.getBankName());
        c.setAccountNumber(dto.getAccountNumber());
        c.setBranchName(dto.getBranchName());
        c.setAccountType(dto.getAccountType());
        c.setSwiftCode(dto.getSwiftCode());
        c.setLoanAmountLimit(dto.getLoanAmountLimit());
        c.setAvailableLoanLimit(dto.getAvailableLoanLimit());

        return c;
    }

    private CustomerResponseDto mapToResponse(Customer c) {

        CustomerResponseDto dto = new CustomerResponseDto();

        dto.setId(c.getId());
        dto.setFullName(c.getFullName());
        dto.setGender(c.getGender());
        dto.setDateOfBirth(c.getDateOfBirth());
        dto.setNationalId(c.getNationalId());
        dto.setKraPin(c.getKraPin());
        dto.setEmail(c.getEmail());
        dto.setMobileNumber(c.getMobileNumber());
        dto.setPhysicalAddress(c.getPhysicalAddress());
        dto.setPostalAddress(c.getPostalAddress());
        dto.setNationality(c.getNationality());
        dto.setMaritalStatus(c.getMaritalStatus());
        dto.setCifNumber(c.getCifNumber());
        dto.setCustomerType(c.getCustomerType());
        dto.setAccountSince(c.getAccountSince());
        dto.setExistingCustomer(c.getExistingCustomer());
        dto.setKycVerified(c.getKycVerified());
        dto.setStatus(c.getStatus());
        dto.setIntakeChannel(c.getIntakeChannel());
        dto.setRelationshipManager(c.getRelationshipManager());
        dto.setExistingFacilities(c.getExistingFacilities());
        dto.setTotalExposure(c.getTotalExposure());
        dto.setRepaymentRecord(c.getRepaymentRecord());
        dto.setLastFacility(c.getLastFacility());
        dto.setBankName(c.getBankName());
        dto.setAccountNumber(c.getAccountNumber());
        dto.setBranchName(c.getBranchName());
        dto.setAccountType(c.getAccountType());
        dto.setSwiftCode(c.getSwiftCode());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        dto.setLoanAmountLimit(c.getLoanAmountLimit());
        dto.setAvailableLoanLimit(c.getAvailableLoanLimit());

        if (c.getObligations() != null) {

            List<CustomerObligationResponseDto> obligations = c.getObligations()
                    .stream()
                    .map(o -> {
                        CustomerObligationResponseDto obligation_dto = new CustomerObligationResponseDto();

                        obligation_dto.setId(o.getId());
                        obligation_dto.setCifNumber(o.getCifNumber());
                        obligation_dto.setLender(o.getLender());
                        obligation_dto.setFacilityType(o.getFacilityType());
                        obligation_dto.setOutstanding(o.getOutstanding());
                        obligation_dto.setMonthlyCommitment(o.getMonthlyCommitment());
                        obligation_dto.setSource(o.getSource());
                        obligation_dto.setStatus(o.getStatus());

                        return obligation_dto;
                    })
                    .collect(Collectors.toList());

            dto.setObligations(obligations);
        }

        return dto;
    }

    @Override
    public List<CustomerResponseDto> searchByNameAndCif(String fullname, String cifnumber) {

        log.info("Searching customers by FullName: {} and CIF: {}", fullname, cifnumber);

        if ((fullname == null || fullname.trim().isEmpty()) &&
                (cifnumber == null || cifnumber.trim().isEmpty())) {
            throw new ValidationException("At least one search value is required");
        }

        String name = (fullname == null) ? "" : fullname.trim();
        String cif = (cifnumber == null) ? "" : cifnumber.trim();

        List<Customer> customers =
                customerRepository
                        .findByFullNameContainingIgnoreCaseAndCifNumberContainingIgnoreCase(name, cif);

        return customers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponseDto recalculateLoanLimit(
            String cifNumber
    ) {

        log.info(
                "Recalculating loan limit for CIF: {}",
                cifNumber
        );

        Customer customer = customerRepository
                .findByCifNumber(cifNumber)
                .orElseThrow(() ->
                        new ValidationException(
                                "Customer not found"
                        )
                );

        Double totalLimit =
                customer.getLoanAmountLimit() != null
                        ? customer.getLoanAmountLimit()
                        : 0.0;

        Map<String, Object> loanLimitData =
                historyProcessInstanceService
                        .calculateLoanLimit(cifNumber);

        Double utilizedAmount =
                loanLimitData.get("utilizedLoanAmount") != null
                        ? Double.parseDouble(
                        loanLimitData
                                .get("utilizedLoanAmount")
                                .toString()
                )
                        : 0.0;

        Double availableLimit =
                totalLimit - utilizedAmount;

        if (availableLimit < 0) {
            availableLimit = 0.0;
        }

        customer.setAvailableLoanLimit(
                availableLimit
        );

        Customer updated =
                customerRepository.save(customer);

        return mapToResponse(updated);
    }
}