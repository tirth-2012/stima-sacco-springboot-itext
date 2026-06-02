package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.ProcessInstanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessInstanceAuditRepository extends JpaRepository<ProcessInstanceAudit, Long> {
    List<ProcessInstanceAudit> findByProcessInstanceIdOrderByActionTimeDesc(String processInstanceId);
}
