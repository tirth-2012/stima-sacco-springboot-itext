package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.entity.LoanApplication;
import com.rutusoft.flowable.enums.Status;
import com.rutusoft.flowable.repository.LoanApplicationRepository;
import com.rutusoft.flowable.service.DashboardService;
import com.rutusoft.flowable.service.GuarantorService;
import com.rutusoft.flowable.utility.SecurityUtil;
import com.rutusoft.flowable.utility.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final SecurityUtil securityUtil;
    private final LoanApplicationRepository loanApplicationRepository;
    private final GuarantorService guarantorService;

    @Override
    public Map<String, Object> getDashboardDataByUser(String assignee) {
        Map<String, Object> dashBoardData = new HashMap<>();
        dashBoardData.put("myActiveProcessInstancesCount", activeProcessInstancesCount(assignee));
        dashBoardData.put("myCompletedProcessInstancesCount", completedProcessInstanceCount(assignee));
        dashBoardData.put("myGroupTasksCount", groupTasksCount(assignee));
        dashBoardData.put("myActiveTasksCount", activeTasksCount(assignee));
        dashBoardData.put("myCompletedTasksCount", completeTasksCount(assignee));
        dashBoardData.put("myAverageTasksCompletionTime", averageTaskCompletionTime(assignee));
        dashBoardData.put("myPendingForApprovalTasksCount", waitingForApprovalTasksCount(assignee));


        dashBoardData.put("activeProcessInstancesCount", activeProcessInstancesCount());
        dashBoardData.put("completedProcessInstancesCount", completedProcessInstanceCount());
        dashBoardData.put("groupTasksCount", groupTasksCount());
        dashBoardData.put("activeTasksCount", activeTasksCount());
        dashBoardData.put("completedTasksCount", completeTasksCount());
        dashBoardData.put("averageTasksCompletionTime", averageTaskCompletionTime());
        dashBoardData.put("pendingForApprovalTasksCount", waitingForApprovalTasksCount());
        dashBoardData.put("loanApplicationStatusCount", getLoanApplicationStatusCount());

        dashBoardData.put("approvedLoanApplicationsCount", approvedLoanApplicationsCount());
        dashBoardData.put("approvedLoanApplicationsByUserCount", approvedLoanApplicationsByUserCount(assignee));
        dashBoardData.put("rejectedLoanApplicationsCount", rejectedLoanApplicationsCount());
        dashBoardData.put("activatedLoanApplicationsCount", activatedLoanApplicationsCount());
        dashBoardData.put("activatedLoanApplicationsByUserCount", activatedLoanApplicationsByUserCount(assignee));
        dashBoardData.put("awaitingGuarantorConsentsCount", awaitingGuarantorConsentsCount());
        dashBoardData.put("awaitingGuarantorConsentsByUserCount", awaitingGuarantorConsentsByUserCount(assignee));

        dashBoardData.put("existingGuaranteesPlead", existingGuaranteesPlead());
        dashBoardData.put("existingGuaranteesPleadByUserCount", existingGuaranteesPleadByUserCount(assignee));

        return dashBoardData;
    }

    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashBoardData = new HashMap<>();
        dashBoardData.put("activeProcessInstancesCount", activeProcessInstancesCount());
        dashBoardData.put("completedProcessInstancesCount", completedProcessInstanceCount());
        dashBoardData.put("groupTasksCount", groupTasksCount());
        dashBoardData.put("activeTasksCount", activeTasksCount());
        dashBoardData.put("completedTasksCount", completeTasksCount());
        dashBoardData.put("averageTasksCompletionTime", averageTaskCompletionTime());
        dashBoardData.put("waitingForApprovalTasksCount", waitingForApprovalTasksCount());
        dashBoardData.put("loanApplicationStatusCount", getLoanApplicationStatusCount());
        return dashBoardData;
    }

    @Override
    public Long activeProcessInstancesCount(String assignee) {
        return runtimeService.createProcessInstanceQuery().startedBy(assignee).active().count();
    }

    @Override
    public Long activeProcessInstancesCount() {
        return runtimeService.createProcessInstanceQuery().active().count();
    }

    @Override
    public Long completedProcessInstanceCount(String assignee) {
        return historyService.createHistoricProcessInstanceQuery().startedBy(assignee).finished().count();
    }

    @Override
    public Long completedProcessInstanceCount() {
        return historyService.createHistoricProcessInstanceQuery().finished().count();
    }

    @Override
    public Long activeTasksCount(String assignee) {
        return taskService.createTaskQuery().taskAssignee(assignee).active().count();
    }

    @Override
    public Long activeTasksCount() {
        return taskService.createTaskQuery().active().count();
    }

    @Override
    public Long completeTasksCount(String assignee) {
        return historyService.createHistoricTaskInstanceQuery().taskAssignee(assignee).finished().count();
    }

    @Override
    public Long completeTasksCount() {
        return historyService.createHistoricTaskInstanceQuery().finished().count();
    }

    @Override
    public Long groupTasksCount(String assignee) {
        List<String> currentUserGroups = securityUtil.getCurrentUserGroups();
        return taskService.createTaskQuery().taskUnassigned().taskCandidateGroupIn(currentUserGroups).count();
    }

    @Override
    public Long groupTasksCount() {
        return taskService.createTaskQuery().taskUnassigned().count();
    }

    @Override
    public String averageTaskCompletionTime(String assignee) {
        Long durationTime = 0l;
        for (HistoricTaskInstance historicTaskInstance : historyService.createHistoricTaskInstanceQuery().taskAssignee(assignee).finished().list()) {
            durationTime += historicTaskInstance.getDurationInMillis();
        }

        return TimeUtil.convertToDDHHMMSS(durationTime);
    }

    @Override
    public String averageTaskCompletionTime() {
        Long durationTime = 0l;
        for (HistoricTaskInstance historicTaskInstance : historyService.createHistoricTaskInstanceQuery().finished().list()) {
            durationTime += historicTaskInstance.getDurationInMillis();
        }

        return TimeUtil.convertToDDHHMMSS(durationTime);
    }

    @Override
    public Long waitingForApprovalTasksCount(String assignee) {
        List<String> currentUserGroups = securityUtil.getCurrentUserGroups();
        List<String> taskDefinitionKeys = new ArrayList<>();
        taskDefinitionKeys.add("usertask_credit_officer");
        taskDefinitionKeys.add("usertask_senior_credit_manager");
        taskDefinitionKeys.add("usertask_credit_committe");
        taskDefinitionKeys.add("usertask_branch_credit_committe");
        return taskService.createTaskQuery().active().taskCandidateGroupIn(currentUserGroups).taskDefinitionKeys(taskDefinitionKeys).count();
    }

    @Override
    public Long waitingForApprovalTasksCount() {
        List<String> currentUserGroups = securityUtil.getCurrentUserGroups();
        List<String> taskDefinitionKeys = new ArrayList<>();
        taskDefinitionKeys.add("usertask_credit_officer");
        taskDefinitionKeys.add("usertask_senior_credit_manager");
        taskDefinitionKeys.add("usertask_credit_committe");
        taskDefinitionKeys.add("usertask_branch_credit_committe");
        return taskService.createTaskQuery().active().taskDefinitionKeys(taskDefinitionKeys).count();

    }

    @Override
    public Map<String, Long> getStagesPipeline(String processDefinitionKey) {
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .active()
                .list();

        return tasks.stream()
                .collect(Collectors.groupingBy(
                        Task::getName,   // key
                        Collectors.counting()         // value
                ));
    }

    @Override
    public Long approvedLoanApplicationsCount() {
        return loanApplicationRepository.countByStatus(Status.APPROVED.getCode());
    }

    @Override
    public Long approvedLoanApplicationsByUserCount(String userId) {
        return loanApplicationRepository.countByRequesterAndStatus(securityUtil.getCurrentUserId(), Status.APPROVED.getCode());
    }

    @Override
    public Long rejectedLoanApplicationsCount() {
        return loanApplicationRepository.countByStatus(Status.DECLINED.getCode());
    }

    @Override
    public Long rejectedLoanApplicationsByUserCount(String userId) {
        return loanApplicationRepository.countByRequesterAndStatus(securityUtil.getCurrentUserId(), Status.DECLINED.getCode());
    }

    @Override
    public Long activatedLoanApplicationsCount() {
        return loanApplicationRepository.countByStatus(Status.APPROVED.getCode());
    }

    @Override
    public Long activatedLoanApplicationsByUserCount(String userId) {
        return loanApplicationRepository.countByRequesterAndStatus(securityUtil.getCurrentUserId(), Status.IN_PROGRESS.getCode());
    }

    @Override
    public Long awaitingGuarantorConsentsCount() {
        return guarantorService.getGuarantorsByStatusCount(Status.PENDING.getCode());
    }

    @Override
    public Long awaitingGuarantorConsentsByUserCount(String userId) {
        return 0L;
    }

    @Override
    public Long existingGuaranteesPlead() {
        Long guarantorsByStatus = guarantorService.getGuarantorsByStatusCount(Status.APPROVED.getCode());
        return 0L;
    }

    @Override
    public Long existingGuaranteesPleadByUserCount(String userId) {
        return 0L;
    }

    private Map<String, Long> getLoanApplicationStatusCount() {

        Map<String, Long> statusCount = new HashMap<>();

        List<LoanApplication> applications = loanApplicationRepository.findAll();

        statusCount.put("IN_PROGRESS", applications.stream()
                        .filter(a ->
                                a.getStatus() != null &&
                                        a.getStatus().equals("IN_PROGRESS")
                        )
                        .count()
        );

        statusCount.put("COMPLETED", applications.stream()
                        .filter(a ->
                                a.getStatus() != null &&
                                        a.getStatus().equals("COMPLETED")
                        )
                        .count()
        );

        statusCount.put("DECLINED", applications.stream()
                        .filter(a ->
                                a.getStatus() != null &&
                                        a.getStatus().equals("DECLINED")
                        )
                        .count()
        );

        return statusCount;
    }
}
