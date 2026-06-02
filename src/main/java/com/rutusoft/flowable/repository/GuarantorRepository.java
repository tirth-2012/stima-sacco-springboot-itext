package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.Guarantor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface GuarantorRepository extends JpaRepository<Guarantor, Long> {

    Optional<Guarantor> findByMemberNumber(String memberNumber);

    @Query("""
        SELECT g
        FROM Guarantor g
        LEFT JOIN FETCH g.customer
        WHERE g.processInstanceId = :processInstanceId
    """)
    List<Guarantor> findByProcessInstanceIdWithCustomer(
            @Param("processInstanceId") String processInstanceId
    );
    List<Guarantor> findAllByProcessInstanceId(String processInstanceId);
    List<Guarantor> findByProcessInstanceIdAndStatus(String processInstanceId, String status);
    List<Guarantor> findByStatus(String status);
    Long countByStatus(String status);
    @Query("""
        SELECT COALESCE(SUM(g.guarantorAmount), 0)
        FROM Guarantor g
        WHERE g.status = :status
    """)
    BigDecimal getTotalGuarantorAmountByStatus(String status);
    List<Guarantor> findByGuaranteeIdAndStatus(String guaranteeId, String status);
    List<Guarantor> findByGuaranteeIdAndStatusNot(String guaranteeId, String status);
    Long countByGuaranteeIdAndStatus(
            String guaranteeId,
            String status
    );
    List<Guarantor> findAllByMemberNumber(
            String memberNumber
    );
    Long countByMemberNumberAndStatus(
            String memberNumber,
            String status
    );
}