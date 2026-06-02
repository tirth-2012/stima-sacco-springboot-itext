package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.CollateralRequest;
import com.rutusoft.flowable.entity.Collateral;
import com.rutusoft.flowable.entity.Customer;
import com.rutusoft.flowable.repository.CollateralRepository;
import com.rutusoft.flowable.repository.CustomerRepository;
import com.rutusoft.flowable.service.CollateralService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollateralServiceImpl implements CollateralService {

    private final CollateralRepository collateralRepository;
    private final CustomerRepository customerRepository;

    @Override
    public Collateral createCollateral(String cifNumber, CollateralRequest request) {

        Customer customer = customerRepository
                .findByCifNumber(cifNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Collateral collateral = new Collateral();
        collateral.setProcessInstanceId(request.getProcessInstanceId());
        collateral.setCifNumber(cifNumber);
        collateral.setSecurityType(request.getSecurityType());
        collateral.setDescription(request.getDescription());
        collateral.setOwnership(request.getOwnership());
        collateral.setDisbursementType(request.getDisbursementType());
        collateral.setDetail(request.getDetail());

        //collateral.setCustomer(customer);

        return collateralRepository.save(collateral);
    }

    @Override
    public Collateral updateCollateral(String cifNumber, Long collateralId, CollateralRequest request) {

        // 🔹 Get customer by CIF
        Customer customer = customerRepository
                .findByCifNumber(cifNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // 🔹 Get collateral
        Collateral existing = collateralRepository.findById(collateralId)
                .orElseThrow(() -> new RuntimeException("Collateral not found"));

        // 🔥 IMPORTANT VALIDATION
//        if (!existing.getCustomer().getId().equals(customer.getId())) {
//            throw new RuntimeException("Collateral does not belong to this customer");
//        }

        // 🔹 Update common fields
        existing.setProcessInstanceId(request.getProcessInstanceId());
        existing.setSecurityType(request.getSecurityType());
        existing.setDescription(request.getDescription());
        existing.setOwnership(request.getOwnership());
        existing.setDisbursementType(request.getDisbursementType());
        existing.setDetail(request.getDetail());
        existing.setUpdatedAt(java.time.LocalDateTime.now());

        return collateralRepository.save(existing);
    }

    @Override
    public void deleteCollateral(Long id) {
        collateralRepository.deleteById(id);
    }

    @Override
    public Collateral getCollateralById(Long id) {
        return collateralRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collateral not found"));
    }

    @Override
    public List<Collateral> getAllByCif(String cifNumber) {
        return collateralRepository.findByCifNumber(cifNumber);
    }

    @Override
    public List<Collateral> getByProcessInstanceId(String processInstanceId) {

        return collateralRepository.findByProcessInstanceId(processInstanceId);
    }
}