package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.SubSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubSectorRepository extends JpaRepository<SubSector, Long> {
    List<SubSector> findBySectorIdOrderByCodeAsc(Long sectorId);
    Optional<SubSector> findByCode(String code);
    List<SubSector> findBySectorCodeOrderByCodeAsc(String sectorCode);
}