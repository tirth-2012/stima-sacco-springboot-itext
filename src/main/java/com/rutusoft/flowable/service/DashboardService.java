package com.rutusoft.flowable.service;

import java.util.Map;

public interface DashboardService {
    Map<String, Object> getDashboardDataByUser(String assignee);
    Map<String, Object> getDashboardData();
    Long activeProcessInstancesCount(String assignee);
    Long activeProcessInstancesCount();
    Long completedProcessInstanceCount(String assignee);
    Long completedProcessInstanceCount();
    Long activeTasksCount(String assignee);
    Long activeTasksCount();
    Long completeTasksCount(String assignee);
    Long completeTasksCount();
    Long groupTasksCount(String assignee);
    Long groupTasksCount();
    String averageTaskCompletionTime(String assignee);
    String averageTaskCompletionTime();
    Long waitingForApprovalTasksCount(String assignee);
    Long waitingForApprovalTasksCount();
    Map<String, Long> getStagesPipeline(String processDefinitionKey);
    Long approvedLoanApplicationsCount();
    Long approvedLoanApplicationsByUserCount(String userId);
    Long rejectedLoanApplicationsCount();
    Long rejectedLoanApplicationsByUserCount(String userId);
    Long activatedLoanApplicationsCount();
    Long activatedLoanApplicationsByUserCount(String userId);
    Long awaitingGuarantorConsentsCount();
    Long awaitingGuarantorConsentsByUserCount(String userId);
    Long existingGuaranteesPlead();
    Long existingGuaranteesPleadByUserCount(String userId);
}
