package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.ProcessInstanceAuditResponseDto;
import com.rutusoft.flowable.entity.ProcessInstanceAudit;

import java.util.List;

public interface ProcessInstanceAuditService {
    List<ProcessInstanceAuditResponseDto> fetchProcessInstanceAudits(String processInstanceId);
}
