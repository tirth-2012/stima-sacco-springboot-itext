package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.ProcessDiagramDto;
import com.rutusoft.flowable.service.ProcessDiagramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/process-diagram")
public class ProcessDiagramController {
    @Autowired
    private ProcessDiagramService processDiagramService;

    @GetMapping("/{processInstanceId}")
    public ResponseEntity<ProcessDiagramDto> getProcessDiagram(@PathVariable("processInstanceId") String processInstanceId) {
        return new ResponseEntity<>(processDiagramService.processDiagram(processInstanceId), HttpStatus.OK);
    }
}
