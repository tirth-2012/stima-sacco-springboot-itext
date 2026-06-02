package com.rutusoft.flowable.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProcessInstanceAuditResponseDto {
	private Long id;
	private String processInstanceId;
	private String taskId;
	private String title;
	private Date createdDateTime;
	private Date endedDateTime;
	private String action;
	private String actionReason;
	private String actionedBy;
}