package com.rutusoft.flowable.entity;

import java.util.Date;
import lombok.Data;
import lombok.ToString;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "process_instance_audit")
@Data
@ToString
public class ProcessInstanceAudit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "processInstanceId")
	private String processInstanceId;

	@Column(name = "taskId")
	private String taskId;

	@Column(name = "title")
	private String title;

	@Column(name = "actionTime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date actionTime;

	@Column(name = "action")
	private String action;

	@Column(name = "actionReason")
	private String actionReason;

	@Column(name = "actionedBy")
	private String actionedBy;
}