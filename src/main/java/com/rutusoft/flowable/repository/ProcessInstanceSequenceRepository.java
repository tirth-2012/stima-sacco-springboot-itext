package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.ProcessInstanceSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessInstanceSequenceRepository extends JpaRepository<ProcessInstanceSequence, Long> {
}
