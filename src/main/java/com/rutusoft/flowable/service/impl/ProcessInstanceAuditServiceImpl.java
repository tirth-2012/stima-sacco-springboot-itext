package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.ProcessInstanceAuditResponseDto;
import com.rutusoft.flowable.entity.ProcessInstanceAudit;
import com.rutusoft.flowable.repository.ProcessInstanceAuditRepository;
import com.rutusoft.flowable.service.ProcessInstanceAuditService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.IdentityService;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProcessInstanceAuditServiceImpl implements ProcessInstanceAuditService {
    @Autowired
    private ProcessInstanceAuditRepository processInstanceAuditRepository;

    @Autowired
    private IdentityService identityService;

    @Override
    public List<ProcessInstanceAuditResponseDto> fetchProcessInstanceAudits(String processInstanceId) {
        log.info("Fetching audits for processInstanceId: {}", processInstanceId);
        List<ProcessInstanceAudit> audits = null;
        try {
            audits = processInstanceAuditRepository
                            .findByProcessInstanceIdOrderByActionTimeDesc(processInstanceId);

            log.info("Total audits found: {}", audits.size());
            return audits.stream()
                    .map(this::mapToDto)
                    .toList();
        } catch (Exception ex) {
            log.error("Error occured while fetching audits for processInstanceId", ex);
            ex.printStackTrace();
        }

        return List.of();
    }

    private ProcessInstanceAuditResponseDto mapToDto(ProcessInstanceAudit audit) {
        ProcessInstanceAuditResponseDto dto = new ProcessInstanceAuditResponseDto();
        dto.setId(audit.getId());
        dto.setProcessInstanceId(audit.getProcessInstanceId());
        dto.setTitle(audit.getTitle());
        dto.setAction(audit.getAction());
        dto.setActionReason(audit.getActionReason());
        dto.setActionedBy(fetchUserFullname(audit.getActionedBy()));
        dto.setCreatedDateTime(audit.getActionTime());
        return dto;
    }

    private String fetchUserFullname(String userId) {
        if(userId != null) {
            List<User> users = identityService.createUserQuery().userId(userId).list();
            if (!users.isEmpty()) {
                return users.get(0).getFirstName() + " " + users.get(0).getLastName();
            }
        }
        return "";
    }
}
