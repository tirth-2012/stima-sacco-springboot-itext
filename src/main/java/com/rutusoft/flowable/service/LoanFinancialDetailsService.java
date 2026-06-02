package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.LoanFinancialDetailsRequestDto;
import com.rutusoft.flowable.dto.LoanFinancialDetailsResponseDto;
import org.springframework.data.domain.Page;

public interface LoanFinancialDetailsService {

    LoanFinancialDetailsResponseDto createFinancialDetails(
            LoanFinancialDetailsRequestDto dto
    );

    Page<LoanFinancialDetailsResponseDto> getAllFinancialDetails(
            int page,
            int size
    );

    LoanFinancialDetailsResponseDto getFinancialDetailsById(
            Long id
    );

    LoanFinancialDetailsResponseDto updateFinancialDetails(
            Long id,
            LoanFinancialDetailsRequestDto dto
    );

    void deleteFinancialDetails(Long id);
}