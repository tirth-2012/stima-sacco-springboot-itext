package com.rutusoft.flowable.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.rutusoft.flowable.service.DocumentService;
import com.rutusoft.flowable.service.MemoService;
import com.rutusoft.flowable.service.ProcessInstanceVariablesService;
import com.rutusoft.flowable.utility.ByteArrayMultipartFile;
import com.rutusoft.flowable.utility.JsonUtils;
import com.rutusoft.flowable.utility.MailNotificationUtil;
import com.rutusoft.flowable.dto.GuarantorResponseDto;
import com.rutusoft.flowable.service.GuarantorService;
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
public class MemoServiceImpl implements MemoService {
    private final TemplateEngine templateEngine;
    private final HistoryService historyService;
    private final ProcessInstanceVariablesService processInstanceVariablesService;
    private final DocumentService documentService;
    private final MailNotificationUtil mailNotificationUtil;
    private final GuarantorService guarantorService;

    // =========================================================
    // MAIN METHODS
    // =========================================================
    @Override
    public byte[] generateLegalMemo(String processInstanceId) throws Exception {
        Map<String, Object> vars = getVars(processInstanceId);
        Context ctx = buildLegalContext(vars);
        String html = templateEngine.process("memos/legal_clearance_memo", ctx);
        return renderToPdf(html);
    }

    @Override
    public byte[] generateRcaMemo(String processInstanceId) throws Exception {
        Map<String, Object> variables = getVars(processInstanceId);
        Context ctx = buildRcaContext(variables);
        String html = templateEngine.process("memos/rca_memo", ctx);
        byte[] rcaMemo = renderToPdf(html);

        return rcaMemo;
    }

    public void generateAndUploadRCAMemo(DelegateExecution execution) {
        log.info("Generating and uploading RCA Memo to mayan");
        String processInstanceId = execution.getProcessInstanceId();
        try {
            byte[] rcaMemo = generateRcaMemo(processInstanceId);

            Map<String, Object> variables = processInstanceVariablesService.getProcessInstanceVariablesMap(processInstanceId);
            String referenceId = (String) variables.get("referenceId");

            //Upload to mayan
            //Map<String, Object> documentTypes = documentService.getDocumentTypes();
            //log.info("Document types : {}", documentTypes);

            // 🔥 Convert byte[] → MultipartFile
            MultipartFile multipartFile = new ByteArrayMultipartFile(
                    rcaMemo,
                    "RCA Memo",
                    "rca-memo-"+referenceId+".pdf",
                    "application/pdf"
            );

            log.info("Uploading RCA Memo to mayan");
            Long documentTypeId = documentService.getDocumentTypeIdByName("Credit Appraisal Memo");
            log.info("Document type id for RCA Memo : {}", documentTypeId);

            String documentResponse = documentService.uploadDocument(documentTypeId, "Credit Appraisal Memo", "System generated Credit Appraisal Memo", multipartFile);
            log.info("documentResponse : {}", documentResponse);

            Map<String, Object> responseDocumentMap = JsonUtils.toMap(documentResponse);
            Long documentId = JsonUtils.getLong(responseDocumentMap, "id");

            Map<String, Long> metadataMap =  documentService.getAllMetadataTypes();
            Long processInstanceMetadataId = metadataMap.get("processInstanceId");
            Long uploadedByMetadataId = metadataMap.get("uploadedBy");
            Long statusMetadataId = metadataMap.get("status");
            sleep(400);
            documentService.addMetadata(documentId, processInstanceMetadataId, processInstanceId);
            sleep(400);
            documentService.addMetadata(documentId, uploadedByMetadataId, "System");
            sleep(400);
            documentService.addMetadata(documentId, statusMetadataId, "Waiting for approval - "+LocalDate.now());

            //Send email with attachment
            if(variables.get("email_id") != null) {
                String subject = "Credit Appraisal Memo for loan application #"+referenceId;
                String emailBody = "Dear "+""+variables.get("full_name").toString()+", Please find attached Credit Appraisal Memo for your loan application";
                mailNotificationUtil.sendEmailWithAttachment(variables.get("email_id").toString(), subject, emailBody, rcaMemo, "Credit-Appraisal-Memo-"+referenceId+".pdf");
            }
        } catch (Exception e) {
            log.error("Generating Credit Appraisal Memo failed due to : ", e.getMessage());
            //throw new RuntimeException(e);
        }
    }

    public void generateAndUploadLegalMemo(DelegateExecution execution) {
        log.info("Generating and uploading Legal Memo to mayan");

        String processInstanceId = execution.getProcessInstanceId();
        try {
            byte[] legalMemo = generateLegalMemo(processInstanceId);

            Map<String, Object> variables = processInstanceVariablesService.getProcessInstanceVariablesMap(processInstanceId);
            String referenceId = (String) variables.get("referenceId");

            //Upload to mayan
            //Map<String, Object> documentTypes = documentService.getDocumentTypes();
            //log.info("Document types : {}", documentTypes);

            // 🔥 Convert byte[] → MultipartFile
            MultipartFile multipartFile = new ByteArrayMultipartFile(
                    legalMemo,
                    "Legal Memo",
                    "legal-memo-"+referenceId+".pdf",
                    "application/pdf"
            );

            log.info("Uploading Legal Memo to mayan");

            Long documentTypeId = documentService.getDocumentTypeIdByName("Legal Clearance Memo");
            log.info("Document type id for Legal Clearance Memo : {}", documentTypeId);

            String documentResponse = documentService.uploadDocument(documentTypeId, "Legal Memo", "System generated Offer letter", multipartFile);
            log.info("documentResponse : {}", documentResponse);

            Map<String, Object> responseDocumentMap = JsonUtils.toMap(documentResponse);
            Long documentId = JsonUtils.getLong(responseDocumentMap, "id");


            Long processInstanceMetadataId = documentService.getMetadataTypeIdByName("processInstanceId");
            documentService.addMetadata(documentId, processInstanceMetadataId, processInstanceId);

            Long uploadedByMetadataId = documentService.getMetadataTypeIdByName("uploadedBy");
            documentService.addMetadata(documentId, uploadedByMetadataId, "System");

            Long statusMetadataId = documentService.getMetadataTypeIdByName("status");
            documentService.addMetadata(documentId, statusMetadataId, "Waiting for approval - "+LocalDate.now());

            //Send email with attachment
            if(variables.get("email_id") != null) {
                String subject = "Legal Memo for loan application #"+referenceId;
                String emailBody = "Dear "+""+variables.get("full_name").toString()+", Please find attached Legal Memo for your loan application";
                mailNotificationUtil.sendEmailWithAttachment(variables.get("email_id").toString(), subject, emailBody, legalMemo, "Legal-Memo-"+referenceId+".pdf");
            }

        } catch (Exception e) {
            log.error("Generating Legal Memo failed due to : {}", e.getMessage());
            //throw new RuntimeException(e);
        }
    }

    // =========================================================
    // COMMON VAR FETCH
    // =========================================================
    private Map<String, Object> getVars(String processInstanceId) {
        Map<String, Object> vars = processInstanceVariablesService.getProcessInstanceVariablesMap(processInstanceId);

        HistoricProcessInstance pi = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        vars.put("applicationDate", pi.getStartTime());
        vars.put("processInstanceId", processInstanceId);
        return vars;
    }

    // =========================================================
    // SAFE HELPERS (CORE FIX)
    // =========================================================
    private String safe(Object val) {
        return (val == null || val.toString().trim().isEmpty()) ? "—" : val.toString();
    }

    private String safeNum(Object val) {
        try {
            if (val == null) return "0";
            return String.valueOf(Double.parseDouble(val.toString()));
        } catch (Exception e) {
            return "0";
        }
    }

    private String formatCurrency(Object value) {
        try {
            if (value == null) return "0.00";
            double d = Double.parseDouble(value.toString());
            return String.format("%,.2f", d);
        } catch (Exception e) {
            return "0.00";
        }
    }

    // =========================================================
    // LEGAL CONTEXT (FIXED)
    // =========================================================
    private Context buildLegalContext(Map<String, Object> vars) {

        Context ctx = new Context();

        String productType = safe(vars.get("product_type"));

        ctx.setVariable("productCategory",
                productType.contains("Islamic") ? "ISLAMIC" : "CONVENTIONAL");

        ctx.setVariable("applicationRef", safe(vars.get("referenceId")));
        ctx.setVariable("clearanceRef", "LC-" + safe(vars.get("referenceId")));
        ctx.setVariable("clearanceDate", LocalDate.now().toString());

        ctx.setVariable("customerName", safe(vars.get("full_name")));
        ctx.setVariable("cifNumber", safe(vars.get("cif_number")));
        ctx.setVariable("productType", safe(vars.get("product_name")));

        ctx.setVariable("facilityAmount", formatCurrency(vars.get("total_loan_amount")));
        ctx.setVariable("monthlyInstalment", formatCurrency(vars.get("monthly_installment")));

        ctx.setVariable("creditScore", safeNum(vars.get("ai_risk_score")));
        ctx.setVariable("riskBand", safe(vars.get("ai_risk_rating")));

        ctx.setVariable("approvalStage", "AUTO");
        ctx.setVariable("approvalDate", LocalDate.now().toString());
        ctx.setVariable("approverName",
                safe(vars.get("usertask_conventional_credit_officer_action_by")));

        ctx.setVariable("legalOfficerName",
                safe(vars.get("usertask_conventional_legal_verification_action_by")));

        // Collateral
        ctx.setVariable("collateralType", safe(vars.get("security_type")));
        ctx.setVariable("collateralDescription", safe(vars.get("security_description")));
        ctx.setVariable("collateralFSV", formatCurrency(vars.get("forced_sale_value")));
        ctx.setVariable("coverageRatio", safe(vars.get("coverage_ratio")));

        // Bank
        ctx.setVariable("borrowerBank", safe(vars.get("bank_name")));
        ctx.setVariable("borrowerAccountNumber", safe(vars.get("account_number")));

        // Supplier
        ctx.setVariable("supplierName", safe(vars.get("owner_name")));
        ctx.setVariable("supplierBank", safe(vars.get("bank_name")));
        ctx.setVariable("supplierAccountNumber", safe(vars.get("account_number")));

        // Insurance
        ctx.setVariable("insuredValue", formatCurrency(vars.get("estimated_market_value")));
        ctx.setVariable("insurerName", "AUTO");
        ctx.setVariable("policyNumber", "AUTO");
        ctx.setVariable("policyRef", "AUTO");

        // CP LIST
        ctx.setVariable("conditionsPrecedentStatus",
                buildCPList((List<Map<String, Object>>) vars.get("LVchecklist")));

        ctx.setVariable("legalNarrative",
                List.of("All legal conditions satisfied.", "Ready for disbursement."));

        ctx.setVariable("chargeRef", "AUTO");
        ctx.setVariable("chargeDate", LocalDate.now().toString());
        ctx.setVariable("facilityFeeAmount", "0");

        Double loanAmount = Double.parseDouble(vars.get("cost_price").toString());
        Double facilityFee = loanAmount * 0.03;
        ctx.setVariable("facilityFee", formatCurrency(facilityFee));

        Double exciseDuty = facilityFee * 0.20;
        ctx.setVariable("exciseDuty", formatCurrency(exciseDuty));

        ctx.setVariable("insurance", "Credit life insurance is mandatory, often charged around 0.035% of the loan value.");

        ctx.setVariable("legalOfficerUsername",
                safe(vars.get("usertask_conventional_legal_verification_action_by")));

        // ----------------------------------------------------------------
        // GUARANTORS
        // ----------------------------------------------------------------

        String processInstanceId = vars.get("processInstanceId").toString();

        List<GuarantorResponseDto> guarantors =
                guarantorService.getGuarantorsByProcessInstanceId(processInstanceId);

        ctx.setVariable("guarantors", guarantors);

        log.info("Legal Memo Guarantors : {}", guarantors);

        return ctx;
    }

    // =========================================================
    // RCA CONTEXT (FULLY FIXED)
    // =========================================================
    private Context buildRcaContext(Map<String, Object> vars) {

        Context ctx = new Context();

        String productName = safe(vars.get("product_name"));

        ctx.setVariable("productCategory", "SACCO");

        ctx.setVariable("applicationRef", safe(vars.get("referenceId")));
        ctx.setVariable("memoDate", LocalDate.now().toString());

        ctx.setVariable("customerName", safe(vars.get("full_name")));
        ctx.setVariable("cifNumber", safe(vars.get("cif_number")));

        ctx.setVariable("productName", productName);
        ctx.setVariable("productType", productName);

        ctx.setVariable("facilityAmount",
                formatCurrency(vars.get("total_loan_amount")));

        ctx.setVariable("monthlyInstalment",
                formatCurrency(vars.get("monthly_installment")));

        // Insurance
        ctx.setVariable("insuredValue", formatCurrency(vars.get("estimated_market_value")));
        ctx.setVariable("insurerName", "AUTO");
        ctx.setVariable("policyNumber", "AUTO");
        ctx.setVariable("policyRef", "AUTO");

        Double loanAmount = Double.parseDouble(vars.get("cost_price").toString());
        Double facilityFee = loanAmount * 0.03;
        ctx.setVariable("facilityFee", formatCurrency(facilityFee));

        Double exciseDuty = facilityFee * 0.20;
        ctx.setVariable("exciseDuty", formatCurrency(exciseDuty));

        ctx.setVariable("insurance", "Credit life insurance is mandatory, often charged around 0.035% of the loan value.");


        // Analyst
        ctx.setVariable("analystName",
                safe(vars.get("usertask_conventional_risk_credit_analyst_action_by")));
        ctx.setVariable("analystUsername",
                safe(vars.get("usertask_conventional_risk_credit_analyst_action_by")));

        ctx.setVariable("nextStage", "Credit Manager");

        // Summary
        ctx.setVariable("executiveSummary",
                safe(vars.get("ai_summary")));
        ctx.setVariable("rcaDecision", "APPROVE_WITH_CONDITIONS");
        ctx.setVariable("rcaConditionCount", 1);

        // Customer Profile
        ctx.setVariable("industry", safe(vars.get("business_sector")));
        ctx.setVariable("yearsInOperation", safe(vars.get("years_of_Business")));
        ctx.setVariable("directors", safe(vars.get("owner_name")));
        ctx.setVariable("kraPIN", safe(vars.get("kra_id")));

        ctx.setVariable("crbScore", safeNum(vars.get("ai_risk_score")));
        ctx.setVariable("crbRef", "AUTO");
        ctx.setVariable("crbAge", "1 day");

        // Financials
        ctx.setVariable("avgMonthlyRevenue", safeNum(vars.get("monthly_business_revenue")));
        ctx.setVariable("monthlyNOI", safeNum(vars.get("monthly_net_income")));
        ctx.setVariable("existingObligations", safeNum(vars.get("existing_monthly_obligations")));
        ctx.setVariable("totalObligations", safeNum(vars.get("after_this_facility")));
        ctx.setVariable("dsr", safeNum(vars.get("deb_service_ratio")));
        ctx.setVariable("dsrStatus", safe(vars.get("dsr_comment")));

        // Obligations (FIXED NESTED)
        Map<String, Object> oblMap = (Map<String, Object>) vars.get("obligations");

        String total = "0";
        String monthly = "0";

        if (oblMap != null) {
            total = safeNum(oblMap.get("total"));
            monthly = safeNum(oblMap.get("total_proposed_instalment"));
        }

        List<Map<String, Object>> obligations = new ArrayList<>();
        Map<String, Object> obl = new HashMap<>();
        obl.put("lender", "DIB Bank");
        obl.put("facilityType", "Loan");
        obl.put("outstanding", total);
        obl.put("monthly", monthly);
        obl.put("source", "System");
        obligations.add(obl);

        ctx.setVariable("obligations", obligations);
        ctx.setVariable("totalObligationsOutstanding", total);

        // Scorecard
        ctx.setVariable("scorecardRows", new ArrayList<>());
        ctx.setVariable("compositeScore", safeNum(vars.get("ai_risk_score")));
        ctx.setVariable("compositeWeighted", safeNum(vars.get("ai_risk_score")));
        ctx.setVariable("riskBand", safe(vars.get("ai_risk_rating")));

        // Collateral
        ctx.setVariable("collateralType", safe(vars.get("security_type")));
        ctx.setVariable("collateralDescription", safe(vars.get("security_description")));
        ctx.setVariable("collateralMarketValue", safeNum(vars.get("estimated_market_value")));
        ctx.setVariable("collateralFSV", safeNum(vars.get("forced_sale_value")));
        ctx.setVariable("coverageRatio", safe(vars.get("coverage_ratio")));

        ctx.setVariable("valuer", "AUTO");
        ctx.setVariable("collateralSearchResult", "Verified");

        // Conditions
        ctx.setVariable("rcaConditions",
                Optional.ofNullable((List<String>) vars.get("suggested_conditions"))
                        .orElse(List.of("Standard condition")));

        // Decision
        ctx.setVariable("aiRecommendation",
                safe(vars.get("ai_recommendation")));
        ctx.setVariable("aiOverride", "None");

        return ctx;
    }

    // =========================================================
    // CP LIST
    // =========================================================
    private List<Map<String, Object>> buildCPList(List<Map<String, Object>> checklist) {

        List<Map<String, Object>> list = new ArrayList<>();
        if (checklist == null) return list;

        int i = 1;

        for (Map<String, Object> item : checklist) {

            Map<String, Object> cp = new HashMap<>();

            cp.put("number", "CP " + i++);
            cp.put("description", safe(item.get("label")));
            cp.put("type", "Standard");
            cp.put("evidenceRef", "SYSTEM");
            cp.put("satisfiedDate", LocalDate.now().toString());

            boolean checked = Boolean.TRUE.equals(item.get("checked"));
            cp.put("status", checked ? "SATISFIED" : "PENDING");

            list.add(cp);
        }

        return list;
    }

    // =========================================================
    // PDF RENDER
    // =========================================================
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
            throw new RuntimeException("PDF generation failed", e);
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