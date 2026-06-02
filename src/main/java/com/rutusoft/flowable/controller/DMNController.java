package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.DecisionTableInputJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import org.flowable.dmn.api.DmnDecisionService;

@RestController
@RequestMapping("dmn")
public class DMNController {
	@Autowired
	private DmnDecisionService dmnDecisionService;

	@PostMapping("/execute")
	public ResponseEntity<List<Map<String, Object>>> startProcess(@RequestBody DecisionTableInputJson decisionTableInputJson) {
		try {
			List<Map<String, Object>> outputVariables = dmnDecisionService.createExecuteDecisionBuilder()
					.decisionKey(decisionTableInputJson.getDecisionKey())   // DMN decision key
					.variables(decisionTableInputJson.getInputVariables())
					.execute();
			return new ResponseEntity<>(outputVariables, HttpStatus.OK);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
