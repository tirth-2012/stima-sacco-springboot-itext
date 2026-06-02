package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.GuarantorRequestDto;
import com.rutusoft.flowable.dto.GuarantorResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GuarantorService {

    GuarantorResponseDto createGuarantor(GuarantorRequestDto dto);

    Page<GuarantorResponseDto> getAllGuarantors(int page, int size);

    GuarantorResponseDto getGuarantorById(Long id);

    GuarantorResponseDto updateGuarantor(Long id, GuarantorRequestDto dto);

    String updateStatus(Long id, String status);

    List<GuarantorResponseDto> getGuarantorsByProcessInstanceId(String processInstanceId);

    List<GuarantorResponseDto> getMyActiveConsents();

    List<GuarantorResponseDto> getMyHistoricalConsents();

    void deleteGuarantor(Long id);

    Long getGuarantorsByStatusCount(String status);

    Long getGuarantorsByUserAndStatusCount(String user, String status);

    Long getMyActiveConsentsCount();

    List<GuarantorResponseDto> getMyGuarantorRequests();

    Long getMyExistingGuaranteesCount();
}