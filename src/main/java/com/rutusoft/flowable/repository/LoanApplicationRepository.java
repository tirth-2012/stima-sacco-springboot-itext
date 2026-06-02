package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    Optional<LoanApplication> findByReferenceId(String referenceId);
    Optional<LoanApplication> findByBusinessKey(String businessKey);
    Optional<LoanApplication> findByProcessInstanceId(String processInstanceId);
    Long countByStatus(String status);
    Long countByRequesterAndStatus(String requester, String status);
}