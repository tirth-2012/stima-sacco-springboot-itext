package com.rutusoft.flowable.service;

public interface ClosureSummaryReportService {
    byte[] generateClosureSummaryReport(String processInstanceId) throws Exception;
    void generateAndUploadClosureSummaryReport(String processInstanceId);
}
