package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.CustomerObligationRequestDto;
import com.rutusoft.flowable.dto.CustomerObligationResponseDto;

import java.util.List;

public interface CustomerObligationService {

    CustomerObligationResponseDto create(CustomerObligationRequestDto dto);

    List<CustomerObligationResponseDto> getAll();

    CustomerObligationResponseDto getById(Long id);

    List<CustomerObligationResponseDto> getByCif(String cifNumber);

    CustomerObligationResponseDto update(Long id, CustomerObligationRequestDto dto);

    void delete(Long id);
}