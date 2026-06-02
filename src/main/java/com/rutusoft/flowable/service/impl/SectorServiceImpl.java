package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.ProductResponseDto;
import com.rutusoft.flowable.dto.SectorDto;
import com.rutusoft.flowable.entity.Product;
import com.rutusoft.flowable.entity.Sector;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.repository.SectorRepository;
import com.rutusoft.flowable.service.SectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SectorServiceImpl implements SectorService {
    private final SectorRepository sectorRepository;

    @Override
    public SectorDto createSector(SectorDto sectorDto) {
        boolean isPresent = sectorRepository.findByCode(sectorDto.getCode()).isPresent();
        if(isPresent) {
            log.error("Sector with code: {} is already exists", sectorDto.getCode());
            throw new ValidationException("Sector is already exists");
        }

        Sector sector = new Sector();
        sector.setCode(sectorDto.getCode());
        sector.setName(sectorDto.getName());
        sector.setDescription(sectorDto.getDescription());
        sectorRepository.save(sector);
        log.info("Sector created successfully");
        return mapToResponse(sector);
    }

    @Override
    public List<SectorDto> listAllSectors() {
        return sectorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SectorDto updateSector(SectorDto sectorDto) {
        return null;
    }

    @Override
    public void deleteSector(Long id) {
        sectorRepository.deleteById(id);
    }

    // MAPPER
    private SectorDto mapToResponse(Sector sector) {
        SectorDto sectorDto = new SectorDto();
        sectorDto.setId(sector.getId());
        sectorDto.setCode(sector.getCode());
        sectorDto.setName(sector.getName());
        sectorDto.setDescription(sector.getDescription());

        return sectorDto;
    }
}
