package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.SectorDto;
import com.rutusoft.flowable.entity.Sector;

import java.util.List;

public interface SectorService {
    SectorDto createSector(SectorDto sectorDto);
    List<SectorDto> listAllSectors();
    SectorDto updateSector(SectorDto sectorDto);
    void deleteSector(Long id);
}
