package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.LoanFinancialDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanFinancialDetailsRepository
        extends JpaRepository<LoanFinancialDetails, Long> {

    Optional<LoanFinancialDetails>
    findByLoanApplication_Id(Long loanApplicationId);
}