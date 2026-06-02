package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.CustomerObligationRequestDto;
import com.rutusoft.flowable.dto.CustomerObligationResponseDto;
import com.rutusoft.flowable.entity.Customer;
import com.rutusoft.flowable.entity.CustomerObligation;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.repository.CustomerObligationRepository;
import com.rutusoft.flowable.repository.CustomerRepository;
import com.rutusoft.flowable.service.CustomerObligationService;
import org.springframework.dao.DataIntegrityViolationException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerObligationServiceImpl implements CustomerObligationService {

    private final CustomerObligationRepository obligationRepository;
    private final CustomerRepository customerRepository;

    public CustomerObligationServiceImpl(CustomerObligationRepository obligationRepository,
                                         CustomerRepository customerRepository) {
        this.obligationRepository = obligationRepository;
        this.customerRepository = customerRepository;
    }

    // 🔹 CREATE
    @Override
    public CustomerObligationResponseDto create(CustomerObligationRequestDto dto) {

        log.info("Creating obligation for CIF: {}", dto.getCifNumber());

        Customer customer = customerRepository.findByCifNumber(dto.getCifNumber())
                .orElseThrow(() -> new ValidationException("Invalid CIF number"));

        CustomerObligation obligation = mapToEntity(dto, customer);

        return mapToResponse(obligationRepository.save(obligation));
    }

    // 🔹 GET ALL
    @Override
    public List<CustomerObligationResponseDto> getAll() {

        return obligationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔹 GET BY ID
    @Override
    public CustomerObligationResponseDto getById(Long id) {

        CustomerObligation obligation = obligationRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Obligation not found"));

        return mapToResponse(obligation);
    }

    // 🔹 GET BY CIF
    @Override
    public List<CustomerObligationResponseDto> getByCif(String cifNumber) {

        log.info("Fetching obligations for CIF: {}", cifNumber);

        return obligationRepository.findByCifNumber(cifNumber)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔹 UPDATE
    @Override
    public CustomerObligationResponseDto update(Long id, CustomerObligationRequestDto dto) {

        CustomerObligation obligation = obligationRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Obligation not found"));

        obligation.setLender(dto.getLender());
        obligation.setFacilityType(dto.getFacilityType());
        obligation.setOutstanding(dto.getOutstanding());
        obligation.setMonthlyCommitment(dto.getMonthlyCommitment());
        obligation.setSource(dto.getSource());
        obligation.setStatus(dto.getStatus());

        return mapToResponse(obligationRepository.save(obligation));
    }

    // 🔹 DELETE
    @Override
    public void delete(Long id) {
        log.info("Deleting obligation with ID: {}", id);
        CustomerObligation obligation = obligationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obligation not found with id: " + id));
        try {
            obligationRepository.delete(obligation);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException(
                    "Cannot delete obligation. It is linked with other records."
            );
        }
    }

    // 🔹 MAPPERS
    private CustomerObligation mapToEntity(CustomerObligationRequestDto dto, Customer customer) {

        CustomerObligation o = new CustomerObligation();

        o.setCustomer(customer);
        o.setCifNumber(dto.getCifNumber());
        o.setLender(dto.getLender());
        o.setFacilityType(dto.getFacilityType());
        o.setOutstanding(dto.getOutstanding());
        o.setMonthlyCommitment(dto.getMonthlyCommitment());
        o.setSource(dto.getSource());
        o.setStatus(dto.getStatus());

        return o;
    }

    private CustomerObligationResponseDto mapToResponse(CustomerObligation o) {

        CustomerObligationResponseDto dto = new CustomerObligationResponseDto();

        dto.setId(o.getId());
        dto.setCifNumber(o.getCifNumber());
        dto.setLender(o.getLender());
        dto.setFacilityType(o.getFacilityType());
        dto.setOutstanding(o.getOutstanding());
        dto.setMonthlyCommitment(o.getMonthlyCommitment());
        dto.setSource(o.getSource());
        dto.setStatus(o.getStatus());

        return dto;
    }
}