package com.rutusoft.flowable.dto;

import java.util.Map;

public class DecisionTableInputJson {
	private String decisionKey;
	private Map<String, Object> inputVariables;

	public String getDecisionKey() {
		return decisionKey;
	}

	public void setDecisionKey(String decisionKey) {
		this.decisionKey = decisionKey;
	}

	public Map<String, Object> getInputVariables() {
		return inputVariables;
	}

	public void setInputVariables(Map<String, Object> inputVariables) {
		this.inputVariables = inputVariables;
	}

}