package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.Collateral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollateralRepository extends JpaRepository<Collateral, Long> {

    List<Collateral> findByCifNumber(String cifNumber);
    List<Collateral> findByProcessInstanceId(String processInstanceId);
}