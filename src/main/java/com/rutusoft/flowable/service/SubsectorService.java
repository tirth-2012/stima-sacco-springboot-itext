package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.SectorDto;
import com.rutusoft.flowable.dto.SubsectorDto;

import java.util.List;

public interface SubsectorService {
    SubsectorDto createSubsector(SubsectorDto subsectorDto);
    List<SubsectorDto> listAllSubsectors();
    List<SubsectorDto> listAllSubsectorsBySectorCode(String sectorCode);
    List<SubsectorDto> listAllSubsectorsBySectorId(Long sectorId);
    SectorDto updateSubsector(SectorDto sectorDto);
    void deleteSubsector(Long id);

}
