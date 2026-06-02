package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.CustomerObligation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerObligationRepository extends JpaRepository<CustomerObligation, Long> {

    List<CustomerObligation> findByCifNumber(String cifNumber);
}