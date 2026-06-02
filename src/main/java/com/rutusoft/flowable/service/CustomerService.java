package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.CustomerRequestDto;
import com.rutusoft.flowable.dto.CustomerResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {

    CustomerResponseDto createCustomer(CustomerRequestDto dto);

    Page<CustomerResponseDto> getAllCustomers(int page, int size);

    CustomerResponseDto getCustomerById(Long id);

    CustomerResponseDto updateCustomer(Long id, CustomerRequestDto dto);

    void deleteCustomer(Long id);

    List<CustomerResponseDto> searchCustomers(
            String cifNumber,
            String nationalId,
            String mobileNumber
    );

    List<CustomerResponseDto> searchByNameAndCif(String fullname, String cifnumber);

    CustomerResponseDto recalculateLoanLimit(String cifNumber);
}