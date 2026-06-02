package com.rutusoft.flowable.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.rutusoft.flowable.service.ClosureSummaryReportService;
import com.rutusoft.flowable.service.DocumentService;
import com.rutusoft.flowable.service.ProcessInstanceVariablesService;
import com.rutusoft.flowable.utility.ByteArrayMultipartFile;
import com.rutusoft.flowable.utility.JsonUtils;
import com.rutusoft.flowable.utility.MailNotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClosureSummaryReportServiceImpl implements ClosureSummaryReportService {
    private final TemplateEngine templateEngine;
    private final HistoryService historyService;
    private final ProcessInstanceVariablesService processInstanceVariablesService;
    private final DocumentService documentService;
    private final MailNotificationUtil mailNotificationUtil;

    @Override
    public byte[] generateClosureSummaryReport(String processInstanceId) throws Exception {
        Map<String, Object> vars = getVars(processInstanceId);

        Context ctx = buildClosureSummaryReportContext(vars);

        String html = templateEngine.process("closure-summary/closure-summary-report", ctx);


        return renderToPdf(html);
    }

    public void generateAndUploadClosureSummaryReport(DelegateExecution execution) {
        log.info("Generating and uploading closure summary report to mayan");

        String processInstanceId = execution.getProcessInstanceId();
        try {
            byte[] legalMemo = generateClosureSummaryReport(processInstanceId);

            Map<String, Object> variables = processInstanceVariablesService.getProcessInstanceVariablesMap(processInstanceId);
            String referenceId = (String) variables.get("referenceId");

            // 🔥 Convert byte[] → MultipartFile
            MultipartFile multipartFile = new ByteArrayMultipartFile(
                    legalMemo,
                    "Closure summary report",
                    "closure-summary-report-"+referenceId+".pdf",
                    "application/pdf"
            );

            log.info("Uploading Closure summary report to mayan");
            Long documentTypeId = documentService.getDocumentTypeIdByName("Closure summary report" +
                    " ");
            sleep(300);
            log.info("Document type id for Closure summary report : {}", documentTypeId);

            String documentResponse = documentService.uploadDocument(documentTypeId, "Closure summary report", "System generated Closure summary report", multipartFile);
            sleep(300);
            log.info("documentResponse : {}", documentResponse);
            Map<String, Object> responseDocumentMap = JsonUtils.toMap(documentResponse);

            Long documentId = JsonUtils.getLong(responseDocumentMap, "id");

            //Adding document meta data
            Map<String, Long> metadataMap =  documentService.getAllMetadataTypes();
            Long processInstanceMetadataId = metadataMap.get("processInstanceId");
            Long uploadedByMetadataId = metadataMap.get("uploadedBy");
            Long statusMetadataId = metadataMap.get("status");

            sleep(400);

            documentService.addMetadata(documentId, processInstanceMetadataId, processInstanceId);
            sleep(400);

            documentService.addMetadata(documentId, uploadedByMetadataId, "System");
            sleep(400);

            documentService.addMetadata(documentId, statusMetadataId, "System generated");
            sleep(400);

            //Send email with attachment
            if(variables.get("email_id") != null) {
                String subject = "Closure summary report for loan application #"+referenceId;
                String emailBody = "Dear "+""+variables.get("full_name").toString()+", Please find attached closure summary report for your loan application";
                mailNotificationUtil.sendEmailWithAttachment(variables.get("email_id").toString(), subject, emailBody, legalMemo, "Closure summary report-"+referenceId+".pdf");
            }

        } catch (Exception e) {
            log.error("Generating closure summary report failed due to : {}", e.getMessage());
            //throw new RuntimeException(e);
        }
    }

    public void generateAndUploadClosureSummaryReport(String processInstanceId) {
        log.info("Generating and uploading closure summary report to mayan");

        try {
            byte[] legalMemo = generateClosureSummaryReport(processInstanceId);

            Map<String, Object> variables = processInstanceVariablesService.getProcessInstanceVariablesMap(processInstanceId);
            String referenceId = (String) variables.get("referenceId");

            // 🔥 Convert byte[] → MultipartFile
            MultipartFile multipartFile = new ByteArrayMultipartFile(
                    legalMemo,
                    "Closure summary report",
                    "closure-summary-report-"+referenceId+".pdf",
                    "application/pdf"
            );

            log.info("Uploading Closure summary report to mayan");
            Long documentTypeId = documentService.getDocumentTypeIdByName("Closure summary report" +
                    " ");
            sleep(300);
            log.info("Document type id for Closure summary report : {}", documentTypeId);

            String documentResponse = documentService.uploadDocument(documentTypeId, "Closure summary report", "System generated Closure summary report", multipartFile);
            sleep(300);
            log.info("documentResponse : {}", documentResponse);
            Map<String, Object> responseDocumentMap = JsonUtils.toMap(documentResponse);

            Long documentId = JsonUtils.getLong(responseDocumentMap, "id");

            //Adding document meta data
            Map<String, Long> metadataMap =  documentService.getAllMetadataTypes();
            Long processInstanceMetadataId = metadataMap.get("processInstanceId");
            Long uploadedByMetadataId = metadataMap.get("uploadedBy");
            Long statusMetadataId = metadataMap.get("status");

            sleep(400);

            documentService.addMetadata(documentId, processInstanceMetadataId, processInstanceId);
            sleep(400);

            documentService.addMetadata(documentId, uploadedByMetadataId, "System");
            sleep(400);

            documentService.addMetadata(documentId, statusMetadataId, "System generated");
            sleep(400);

            //Send email with attachment
            if(variables.get("email_id") != null) {
                String subject = "Closure summary report for loan application #"+referenceId;
                String emailBody = "Dear "+""+variables.get("full_name").toString()+", Please find attached closure summary report for your loan application";
                mailNotificationUtil.sendEmailWithAttachment(variables.get("email_id").toString(), subject, emailBody, legalMemo, "Closure summary report-"+referenceId+".pdf");
            }

        } catch (Exception e) {
            log.error("Generating closure summary report failed due to : {}", e.getMessage());
            //throw new RuntimeException(e);
        }
    }


    // =========================================================
    // COMMON VAR FETCH
    // =========================================================
    private Map<String, Object> getVars(String processInstanceId) {

        Map<String, Object> vars =
                processInstanceVariablesService.getProcessInstanceVariablesMap(processInstanceId);

        HistoricProcessInstance pi = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        vars.put("applicationDate", pi.getStartTime());

        return vars;
    }

    // =========================================================
    // ✅ FACILITY (CONVENTIONAL)
    // =========================================================
    private Context buildClosureSummaryReportContext(Map<String, Object> vars) {

        Context ctx = new Context();
        ctx.setVariable("productCategory", vars.get("product_type"));

        ctx.setVariable("applicationRef", vars.get("referenceId"));
        ctx.setVariable("agreementDate", LocalDate.now().toString());

        ctx.setVariable("customerName", vars.get("full_name"));
        ctx.setVariable("customerAddress", vars.get("postal_address"));
        ctx.setVariable("companyRegNo", vars.get("company_reg_no"));

        ctx.setVariable("facilityAmount", formatCurrency(vars.get("total_loan_amount")));
        ctx.setVariable("monthlyInstalment", formatCurrency(vars.get("monthly_installment")));

        ctx.setVariable("interestRate", vars.get("profit_rate"));
        ctx.setVariable("totalInterest", formatCurrency(vars.get("totalInterest")));
        ctx.setVariable("totalRepayable", formatCurrency(vars.get("total_loan_amount")));

        ctx.setVariable("tenorMonths", vars.get("financing_tenor"));
        ctx.setVariable("firstInstalmentDate", vars.get("firstInstalmentDate"));

        ctx.setVariable("borrowerBankName", vars.get("bank_name"));

        ctx.setVariable("approverName", vars.get("usertask_conventional_credit_officer_action_by"));
        ctx.setVariable("approverTitle", "Credit Manager");

        ctx.setVariable("director1Name", vars.get("director1Name"));
        ctx.setVariable("director1Id", vars.get("director1Id"));

        ctx.setVariable("securityItems",
                Optional.ofNullable((List<String>) vars.get("securityItems"))
                        .orElse(new ArrayList<>()));

        ctx.setVariable("covenants",
                Optional.ofNullable((List<String>) vars.get("covenants"))
                        .orElse(new ArrayList<>()));

        ctx.setVariable("repaymentSchedule", buildDummySchedule(vars));

        return ctx;
    }

    private byte[] renderToPdf(String html) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();

            builder.useFont(() ->
                            getClass().getResourceAsStream("/fonts/NotoSans-Regular.ttf"),
                    "NotoSans"
            );

            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();

            return os.toByteArray();

        } catch (Exception e) {
            log.error("PDF generation failed", e);
            throw new RuntimeException("PDF generation failed");
        }
    }

    // =========================================================
    // DUMMY SCHEDULE (you can replace later)
    // =========================================================
    private List<Map<String, Object>> buildDummySchedule(Map<String, Object> vars) {

        List<Map<String, Object>> list = new ArrayList<>();

        for (int i = 1; i <= 6; i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("month", i);
            row.put("dueDate", "2026-0" + i + "-30");
            row.put("instalment", "100000");
            row.put("interestOrProfit", "20000");
            row.put("principal", "80000");
            row.put("balance", "500000");
            list.add(row);
        }

        return list;
    }

    // =========================================================
    private String formatCurrency(Object value) {
        if (value == null) return "—";
        try {
            double d = Double.parseDouble(value.toString());
            return String.format("%,.2f", d);
        } catch (Exception e) {
            return value.toString();
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sleep interrupted", e);
        }
    }

}
