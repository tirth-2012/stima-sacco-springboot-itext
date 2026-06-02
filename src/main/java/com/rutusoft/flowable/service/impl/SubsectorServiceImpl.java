package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.SectorDto;
import com.rutusoft.flowable.dto.SubsectorDto;
import com.rutusoft.flowable.entity.Sector;
import com.rutusoft.flowable.entity.SubSector;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.repository.SectorRepository;
import com.rutusoft.flowable.repository.SubSectorRepository;
import com.rutusoft.flowable.service.SubsectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubsectorServiceImpl implements SubsectorService {
    private final SubSectorRepository subSectorRepository;
    private final SectorRepository sectorRepository;

    @Override
    @Transactional
    public SubsectorDto createSubsector(SubsectorDto subsectorDto) {

        log.info("Creating sub sector : {}", subsectorDto.getCode());

        subSectorRepository.findByCode(subsectorDto.getCode())
                .ifPresent(subSector -> {
                    log.error("Sub sector with code {} already exists",
                            subsectorDto.getCode());

                    throw new ValidationException(
                            "Sub sector already exists : " + subsectorDto.getCode()
                    );
                });

        Sector sector = sectorRepository.findById(subsectorDto.getSectorId())
                .orElseThrow(() -> {
                    log.error("Sector with id {} not found",
                            subsectorDto.getSectorId());

                    return new ValidationException(
                            "Sector not found : " + subsectorDto.getSectorId()
                    );
                });

        SubSector subSector = new SubSector();
        subSector.setCode(subsectorDto.getCode());
        subSector.setName(subsectorDto.getName());
        subSector.setDescription(subsectorDto.getDescription());
        subSector.setSector(sector);

        subSector = subSectorRepository.save(subSector);

        log.info("Sub sector {} created successfully",
                subsectorDto.getName());

        return mapToResponse(subSector);
    }

    @Override
    public List<SubsectorDto> listAllSubsectors() {
        List<SubSector> subSectors = subSectorRepository.findAll();
        return subSectors.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SubsectorDto> listAllSubsectorsBySectorCode(String sectorCode) {
        List<SubSector> subSectors = subSectorRepository.findBySectorCodeOrderByCodeAsc(sectorCode);

        return subSectors.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SubsectorDto> listAllSubsectorsBySectorId(Long sectorId) {
        List<SubSector> subSectors =
                subSectorRepository.findBySectorIdOrderByCodeAsc(sectorId);
        log.info("subSectors : {}", subSectors.size());
        return subSectors.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SectorDto updateSubsector(SectorDto sectorDto) {
        return null;
    }

    @Override
    public void deleteSubsector(Long id) {
        subSectorRepository.deleteById(id);
    }

    // MAPPER
    private SubsectorDto mapToResponse(SubSector subSector) {
        SubsectorDto subsectorDto = new SubsectorDto();
        subsectorDto.setId(subSector.getId());
        subsectorDto.setCode(subSector.getCode());
        subsectorDto.setName(subSector.getName());
        subsectorDto.setDescription(subSector.getDescription());
        subsectorDto.setSectorId(subSector.getSector().getId());
        return subsectorDto;
    }
}
