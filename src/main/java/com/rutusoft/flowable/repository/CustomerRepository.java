package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c " +
            "WHERE c.cifNumber = :cifNumber " +
            "AND (:nationalId IS NULL OR c.nationalId = :nationalId) " +
            "AND (:mobileNumber IS NULL OR c.mobileNumber = :mobileNumber)")
    Optional<Customer> searchCustomer(
            @Param("cifNumber") String cifNumber,
            @Param("nationalId") String nationalId,
            @Param("mobileNumber") String mobileNumber);

    Optional<Customer> findByNationalId(String nationalId);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByMobileNumber(String mobileNumber);

    Optional<Customer> findByCifNumber(String cifNumber);

    List<Customer> findByFullNameContainingIgnoreCaseAndCifNumberContainingIgnoreCase(String fullname, String cifnumber);
}