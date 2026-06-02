package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.ProcessInstanceDto;
import com.rutusoft.flowable.dto.ProcessInstancesResponseDto;
import com.rutusoft.flowable.service.HistoryProcessInstanceService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(
        name = "History Process Instance APIs",
        description = "APIs for querying Historical Flowable Process instances"
)
@RestController
@RequestMapping("/historical-process-instances")
public class HistoryProcessInstanceController {
    @Autowired
    private HistoryProcessInstanceService historyProcessInstanceService;

    @GetMapping("/all/initiator/{initiator}")
    public ResponseEntity<ProcessInstancesResponseDto> allProcessInstances(
            @Parameter(description = "Process initiator", example = "admin")
            @PathVariable("initiator") String initiator,

            @Parameter(description = "Starting index for pagination", example = "0")
            @RequestParam("from") int from,

            @Parameter(description = "Ending index for pagination", example = "10")
            @RequestParam("to") int to) {

        return new ResponseEntity<>(
                historyProcessInstanceService.allProcessInstances(initiator, from, to),
                HttpStatus.OK
        );
    }

    @GetMapping("/completed/initiator/{initiator}")
    public ResponseEntity<ProcessInstancesResponseDto> completedProcessInstances(
            @Parameter(description = "Process initiator", example = "admin")
            @PathVariable("initiator") String initiator,

            @Parameter(description = "Starting index for pagination", example = "0")
            @RequestParam("from") int from,

            @Parameter(description = "Ending index for pagination", example = "10")
            @RequestParam("to") int to) {

        return new ResponseEntity<>(
                historyProcessInstanceService.completedProcessInstancesByInitiator(initiator, from, to),
                HttpStatus.OK
        );
    }

    @GetMapping("/{processInstanceId}")
    public ResponseEntity<ProcessInstanceDto> getProcessInstance(
            @Parameter(description = "", example = "77973cb2-29b7-11f1-8895-c247406d9200")
            @PathVariable("processInstanceId") String processInstanceId){
        return new ResponseEntity<>(
                historyProcessInstanceService.processInstanceByProcessInstanceId(processInstanceId),
                HttpStatus.OK
        );
    }

    // ------------------------------------------------------------------------
    // Calculate Loan Limit
    // ------------------------------------------------------------------------
    @GetMapping("/loan-limit/{initiator}")
    public ResponseEntity<Map<String, Object>> calculateLoanLimit(

            @Parameter(
                    description = "Customer CIF Number",
                    example = "STM-00038111"
            )
            @PathVariable("initiator") String initiator
    ) {

        return new ResponseEntity<>(

                historyProcessInstanceService
                        .calculateLoanLimit(initiator),

                HttpStatus.OK
        );
    }

}
