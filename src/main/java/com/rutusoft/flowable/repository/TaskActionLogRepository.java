package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.TaskActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskActionLogRepository extends JpaRepository<TaskActionLog, Long> {
    List<TaskActionLog> findByProcessInstanceIdOrderByActionTimeDesc(String processInstanceId);
}
