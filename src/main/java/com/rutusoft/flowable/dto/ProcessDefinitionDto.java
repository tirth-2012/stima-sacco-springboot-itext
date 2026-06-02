package com.rutusoft.flowable.dto;

import lombok.Data;

@Data
public class ProcessDefinitionDto {
	private String id;
	private String category;
	private String name;
	private String key;
	private String description;
	private int version;
	private String deploymentId;
	private boolean isSuspended;
	private String derivedFrom;
	private String derivedVersion;
	private String diagramResourceName;
	private String resourceName;
	private String tenantId;
}
