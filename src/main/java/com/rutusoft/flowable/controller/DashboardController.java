package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getDashboardData(@PathVariable("assignee") String assignee) {
        return new ResponseEntity<>(dashboardService.getDashboardData(), HttpStatus.OK);
    }

    @GetMapping("/users/{assignee}")
    public ResponseEntity<Map<String, Object>> getDashboardDataByUser(@PathVariable("assignee") String assignee) {
        return new ResponseEntity<>(dashboardService.getDashboardDataByUser(assignee), HttpStatus.OK);
    }

    @GetMapping("/pipeline/stages/processes/{processDefinitionKey}")
    public ResponseEntity<Map<String, Long>> getStagesPipeline(@PathVariable("processDefinitionKey") String processDefinitionKey){
        return new ResponseEntity<>(dashboardService.getStagesPipeline(processDefinitionKey), HttpStatus.OK);
    }
}
