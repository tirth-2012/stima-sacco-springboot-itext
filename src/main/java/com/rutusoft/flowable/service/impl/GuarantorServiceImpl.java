package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.GuarantorRequestDto;
import com.rutusoft.flowable.dto.GuarantorResponseDto;
import com.rutusoft.flowable.entity.Customer;
import com.rutusoft.flowable.entity.Guarantor;
import com.rutusoft.flowable.enums.Status;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.repository.CustomerRepository;
import com.rutusoft.flowable.repository.GuarantorRepository;
import com.rutusoft.flowable.service.GuarantorService;

import com.rutusoft.flowable.utility.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GuarantorServiceImpl implements GuarantorService {
    private final GuarantorRepository guarantorRepository;
    private final CustomerRepository customerRepository;
    private final SecurityUtil securityUtil;

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------
    @Override
    public GuarantorResponseDto createGuarantor(
            GuarantorRequestDto dto
    ) {

        // ---------------------------------------------------------
        // GUARANTOR CUSTOMER
        // ---------------------------------------------------------
        Customer guarantorCustomer = customerRepository
                .findByCifNumber(dto.getMemberNumber())
                .orElseThrow(() ->
                        new ValidationException("Customer not found with CIF number"));

        String currentUser = securityUtil.getCurrentUserId();

        Customer applicant = customerRepository
                        .findByCifNumber(currentUser)
                        .orElseThrow(() ->
                                new ValidationException("Applicant customer not found"));

        Guarantor guarantor = new Guarantor();

        guarantor.setCustomer(guarantorCustomer);

        // ---------------------------------------------------------
        // GUARANTOR DETAILS
        // ---------------------------------------------------------
        guarantor.setProcessInstanceId(dto.getProcessIntanceId());
        guarantor.setMemberNumber(guarantorCustomer.getCifNumber());
        guarantor.setGuaranteeId(dto.getGuaranteeId());
        guarantor.setFullName(guarantorCustomer.getFullName());
        guarantor.setMobileNumber(guarantorCustomer.getMobileNumber());
        guarantor.setGuarantorAmount(dto.getGuarantorAmount());

        // ---------------------------------------------------------
        // BORROWER DETAILS
        // ---------------------------------------------------------
        guarantor.setBorrowerName(applicant.getFullName());
        guarantor.setBorrowerMemberNumber(applicant.getCifNumber());
        guarantor.setBorrowerMobileNumber(applicant.getMobileNumber());
        guarantor.setBorrowerNationalId(applicant.getNationalId());

        // ---------------------------------------------------------
        // STATUS
        // ---------------------------------------------------------
        guarantor.setStatus(
                dto.getStatus() != null
                        ? dto.getStatus()
                        : "PENDING"
        );

        Guarantor saved = guarantorRepository.save(guarantor);

        return mapToResponse(saved);
    }

    // ----------------------------------------------------------------
    // GET ALL
    // ----------------------------------------------------------------
    @Override
    public Page<GuarantorResponseDto> getAllGuarantors(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return guarantorRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    // ----------------------------------------------------------------
    // GET BY ID
    // ----------------------------------------------------------------
    @Override
    @Transactional
    public GuarantorResponseDto getGuarantorById(Long id) {

        Guarantor guarantor = guarantorRepository.findById(id)
                .orElseThrow(() ->
                        new ValidationException("Guarantor not found")
                );

        return mapToResponse(guarantor);
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------
    @Override
    public GuarantorResponseDto updateGuarantor(
            Long id,
            GuarantorRequestDto dto
    ) {

        Guarantor guarantor = guarantorRepository.findById(id)
                .orElseThrow(() ->
                        new ValidationException("Guarantor not found")
                );

        Customer customer = customerRepository
                .findByCifNumber(dto.getMemberNumber())
                .orElseThrow(() ->
                        new ValidationException("Customer not found with CIF")
                );

        // IMPORTANT
        guarantor.setCustomer(customer);

        // Auto Populate from Customer
        guarantor.setMemberNumber(customer.getCifNumber());
        guarantor.setFullName(customer.getFullName());
        guarantor.setMobileNumber(customer.getMobileNumber());

        guarantor.setGuarantorAmount(dto.getGuarantorAmount());
        guarantor.setStatus(dto.getStatus());

        Guarantor updated = guarantorRepository.save(guarantor);

        return mapToResponse(updated);
    }

    @Override
    public String updateStatus(Long id, String status) {
        log.info("Updating status to : {} for guarantor : {}", status, id);
        Guarantor guarantor = guarantorRepository.findById(id)
                .orElseThrow(() ->
                        new ValidationException("Guarantor not found")
                );

        guarantor.setStatus(status);

        guarantorRepository.save(guarantor);
        return "Guarantor status updated successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuarantorResponseDto> getGuarantorsByProcessInstanceId(String processInstanceId) {
        List<Guarantor> guarantors = guarantorRepository.findAllByProcessInstanceId(processInstanceId);

        return guarantors.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuarantorResponseDto> getMyActiveConsents() {

        String userId = securityUtil.getCurrentUserId();

        log.info("Fetching active guarantor consents for userId={}", userId);

        List<Guarantor> guarantors =
                guarantorRepository.findByGuaranteeIdAndStatus(
                        userId,
                        Status.PENDING.getCode()
                );

        if (guarantors.isEmpty()) {
            log.info("No active guarantor consents found for userId={}", userId);
            return Collections.emptyList();
        }

        return guarantors.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<GuarantorResponseDto> getMyHistoricalConsents() {
        String userId = securityUtil.getCurrentUserId();

        log.info("Fetching historical guarantor consents for userId={}", userId);

        List<Guarantor> guarantors =
                guarantorRepository.findByGuaranteeIdAndStatusNot(
                        userId,
                        Status.PENDING.getCode()
                );

        if (guarantors.isEmpty()) {
            log.info("No historical guarantor consents found for userId={}", userId);
            return Collections.emptyList();
        }

        return guarantors.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------
    @Override
    public void deleteGuarantor(Long id) {

        Guarantor guarantor = guarantorRepository.findById(id)
                .orElseThrow(() ->
                        new ValidationException("Guarantor not found")
                );

        guarantorRepository.delete(guarantor);
    }

    @Override
    public Long getGuarantorsByStatusCount(String status) {
        return guarantorRepository.countByStatus(status);
    }

    @Override
    public Long getGuarantorsByUserAndStatusCount(String user, String status) {
        return 0L;
    }

    @Override
    public Long getMyActiveConsentsCount() {

        String userId =
                securityUtil.getCurrentUserId();

        return guarantorRepository
                .countByGuaranteeIdAndStatus(
                        userId,
                        Status.PENDING.getCode()
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuarantorResponseDto> getMyGuarantorRequests() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String currentUser =
                authentication.getName();

        log.info(
                "Fetching guarantor requests for user={}",
                currentUser
        );

        List<Guarantor> guarantors =
                guarantorRepository
                        .findAllByMemberNumber(currentUser);

        if (guarantors.isEmpty()) {

            return Collections.emptyList();
        }

        return guarantors.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long getMyExistingGuaranteesCount() {

        String currentUser =
                securityUtil.getCurrentUserId();

        return guarantorRepository
                .countByMemberNumberAndStatus(
                        currentUser,
                        Status.APPROVED.getCode()
                );
    }

    // ----------------------------------------------------------------
    // MAPPER
    // ----------------------------------------------------------------
    private GuarantorResponseDto mapToResponse(Guarantor guarantor) {

        GuarantorResponseDto dto = new GuarantorResponseDto();

        dto.setId(guarantor.getId());
        dto.setFullName(guarantor.getFullName());
        dto.setMemberNumber(guarantor.getMemberNumber());
        dto.setGuaranteeId(guarantor.getGuaranteeId());
        dto.setMobileNumber(guarantor.getMobileNumber());
        dto.setGuarantorAmount(guarantor.getGuarantorAmount());
        dto.setStatus(guarantor.getStatus());

        // ---------------------------------------------------------
        // BORROWER DETAILS
        // ---------------------------------------------------------
        dto.setBorrowerName(guarantor.getBorrowerName());
        dto.setBorrowerMemberNumber(guarantor.getBorrowerMemberNumber());
        dto.setBorrowerMobileNumber(guarantor.getBorrowerMobileNumber());
        dto.setBorrowerNationalId(guarantor.getBorrowerNationalId());

        dto.setCreatedAt(guarantor.getCreatedAt());
        dto.setUpdatedAt(guarantor.getUpdatedAt());

        // ---------------------------------------------------------
        // Customer Details
        // ---------------------------------------------------------
        Customer customer = guarantor.getCustomer();

        if (customer != null) {

            dto.setCustomerId(customer.getId());
            dto.setCustomerName(customer.getFullName());
            dto.setCustomerEmail(customer.getEmail());
            dto.setCustomerMobile(customer.getMobileNumber());
            dto.setNationalId(customer.getNationalId());
            dto.setCustomerType(customer.getCustomerType());
        }

        return dto;
    }

}