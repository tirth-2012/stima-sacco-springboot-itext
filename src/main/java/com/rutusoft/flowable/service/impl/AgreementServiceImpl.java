package com.rutusoft.flowable.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.rutusoft.flowable.service.AgreementService;
import com.rutusoft.flowable.service.DocumentService;
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
public class AgreementServiceImpl implements AgreementService {

    private final TemplateEngine templateEngine;
    private final HistoryService historyService;
    private final ProcessInstanceVariablesService processInstanceVariablesService;
    private final DocumentService documentService;
    private final MailNotificationUtil mailNotificationUtil;
    private final GuarantorService guarantorService;

    // =========================================================
    // ✅ CONVENTIONAL AGREEMENT
    // =========================================================
    @Override
    public byte[] generateFacilityAgreement(String processInstanceId) throws Exception {

        Map<String, Object> vars = getVars(processInstanceId);

        Context ctx = buildFacilityContext(vars);

        String html = templateEngine.process("agreements/facility_agreement", ctx);

        return renderToPdf(html);
    }

    // =========================================================
    // ✅ WAKALA AGREEMENT (ISLAMIC)
    // =========================================================
    @Override
    public byte[] generateWakalaAgreement(String processInstanceId) throws Exception {

        Map<String, Object> vars = getVars(processInstanceId);

        Context ctx = buildWakalaContext(vars);

        String html = templateEngine.process("agreements/wakala_agreement", ctx);

        return renderToPdf(html);
    }

    public void generateAndUploadFacilityAgreement(DelegateExecution execution) {
        log.info("Generating and uploading facility agreement to mayan");

        String processInstanceId = execution.getProcessInstanceId();
        try {
            byte[] legalMemo = generateFacilityAgreement(processInstanceId);

            Map<String, Object> variables = processInstanceVariablesService.getProcessInstanceVariablesMap(processInstanceId);
            String referenceId = (String) variables.get("referenceId");

            //Upload to mayan
            //Map<String, Object> documentTypes = documentService.getDocumentTypes();
            //log.info("Document types : {}", documentTypes);

            // 🔥 Convert byte[] → MultipartFile
            MultipartFile multipartFile = new ByteArrayMultipartFile(
                    legalMemo,
                    "Facility agreement",
                    "facility-agreement-"+referenceId+".pdf",
                    "application/pdf"
            );

            log.info("Uploading Facility agreement to mayan");
            Long documentTypeId = documentService.getDocumentTypeIdByName("Facility Agreement");
            sleep(300);
            log.info("Document type id for Facility Agreement : {}", documentTypeId);

            String documentResponse = documentService.uploadDocument(documentTypeId, "Facility agreement", "System generated Facility agreement", multipartFile);
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

            documentService.addMetadata(documentId, statusMetadataId, "Pending for signature - "+LocalDate.now());
            sleep(400);

            //Send email with attachment
            if(variables.get("email_id") != null) {
                String subject = "Facility agreement for loan application #"+referenceId;
                String emailBody = "Dear "+""+variables.get("full_name").toString()+", Please find attached Facility agreement for your loan application";
                mailNotificationUtil.sendEmailWithAttachment(variables.get("email_id").toString(), subject, emailBody, legalMemo, "Facility-agreement-"+referenceId+".pdf");
            }

        } catch (Exception e) {
            log.error("Generating Facility agreement failed due to : {}", e.getMessage());
            //throw new RuntimeException(e);
        }
    }

    public void generateAndUploadWakalaAgreement(DelegateExecution execution) {
        log.info("Generating and uploading legal agreement to mayan");

        String processInstanceId = execution.getProcessInstanceId();
        try {
            byte[] legalMemo = generateFacilityAgreement(processInstanceId);

            Map<String, Object> variables = processInstanceVariablesService.getProcessInstanceVariablesMap(processInstanceId);
            String referenceId = (String) variables.get("referenceId");

            //Upload to mayan
            //Map<String, Object> documentTypes = documentService.getDocumentTypes();
            //log.info("Document types : {}", documentTypes);

            // 🔥 Convert byte[] → MultipartFile
            MultipartFile multipartFile = new ByteArrayMultipartFile(
                    legalMemo,
                    "Wakala agreement",
                    "wakala-agreement-"+referenceId+".pdf",
                    "application/pdf"
            );

            //Adding document meta data
            Map<String, Long> metadataMap =  documentService.getAllMetadataTypes();
            Long processInstanceMetadataId = metadataMap.get("processInstanceId");
            Long uploadedByMetadataId = metadataMap.get("uploadedBy");
            Long statusMetadataId = metadataMap.get("status");

            log.info("Uploading Wakala agreement to mayan");
            Long documentTypeId = documentService.getDocumentTypeIdByName("Wakala Agreement");
            sleep(300);
            log.info("Document type id for Wakala Agreement : {}", documentTypeId);

            String documentResponse = documentService.uploadDocument(documentTypeId, "Wakala Agreement", "System generated Wakala Agreement", multipartFile);
            sleep(300);
            log.info("documentResponse : {}", documentResponse);
            Map<String, Object> responseDocumentMap = JsonUtils.toMap(documentResponse);

            Long documentId = JsonUtils.getLong(responseDocumentMap, "id");

            documentService.addMetadata(documentId, processInstanceMetadataId, processInstanceId);
            sleep(400);

            documentService.addMetadata(documentId, uploadedByMetadataId, "System");
            sleep(400);

            documentService.addMetadata(documentId, statusMetadataId, "Pending for signature - "+LocalDate.now());
            sleep(400);

            //Send email with attachment
            if(variables.get("email_id") != null) {
                String subject = "Legal agreement for loan application #"+referenceId;
                String emailBody = "Dear "+""+variables.get("full_name").toString()+", Please find attached Wakala Agreement for your loan application";
                mailNotificationUtil.sendEmailWithAttachment(variables.get("email_id").toString(), subject, emailBody, legalMemo, "Wakala-agreement-"+referenceId+".pdf");
            }

        } catch (Exception e) {
            log.error("Generating Wakala agreement failed due to : {}", e.getMessage());
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
        vars.put("processInstanceId", processInstanceId);

        return vars;
    }

    // =========================================================
    // ✅ FACILITY (CONVENTIONAL)
    // =========================================================
    private Context buildFacilityContext(Map<String, Object> vars) {

        Context ctx = new Context();

        ctx.setVariable("productCategory", "CONVENTIONAL");

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

        // =========================================================
        // GUARANTORS
        // =========================================================

        String processInstanceId =
                vars.get("processInstanceId").toString();

        List<GuarantorResponseDto> guarantors =
                guarantorService.getGuarantorsByProcessInstanceId(processInstanceId);

        ctx.setVariable("guarantors", guarantors);

        log.info("Facility Agreement Guarantors : {}", guarantors);

        return ctx;
    }

    // =========================================================
    // ✅ WAKALA (ISLAMIC)
    // =========================================================
    private Context buildWakalaContext(Map<String, Object> vars) {

        Context ctx = new Context();

        ctx.setVariable("productCategory", "ISLAMIC");

        ctx.setVariable("applicationRef", vars.get("referenceId"));
        ctx.setVariable("agreementDate", LocalDate.now().toString());

        ctx.setVariable("customerName", vars.get("full_name"));
        ctx.setVariable("customerAddress", vars.get("postal_address"));
        ctx.setVariable("companyRegNo", vars.get("company_reg_no"));

        ctx.setVariable("assetDescription", vars.get("asset_description"));
        ctx.setVariable("supplierInvoiceRef", vars.get("supplierInvoiceRef"));
        ctx.setVariable("supplierName", vars.get("owner_name"));

        ctx.setVariable("facilityAmount", formatCurrency(vars.get("total_loan_amount")));
        ctx.setVariable("profitAmount", formatCurrency(vars.get("profit_amount")));
        ctx.setVariable("totalSalePrice", formatCurrency(vars.get("estimated_market_value")));

        ctx.setVariable("monthlyInstalment", formatCurrency(vars.get("monthly_installment")));
        ctx.setVariable("tenorMonths", vars.get("financing_tenor"));
        ctx.setVariable("firstInstalmentDate", vars.get("firstInstalmentDate"));

        ctx.setVariable("collateralDescription", vars.get("security_description"));
        ctx.setVariable("collateralFSV", formatCurrency(vars.get("forced_sale_value")));

        ctx.setVariable("shariahApprovalRef", vars.get("shariahApprovalRef"));
        ctx.setVariable("shariahApprovalDate", vars.get("shariahApprovalDate"));

        ctx.setVariable("assetLocation", vars.get("asset_location"));

        ctx.setVariable("approverName", vars.get("usertask_conventional_credit_officer_action_by"));
        ctx.setVariable("approverTitle", "Credit Manager");

        ctx.setVariable("director1Name", vars.get("director1Name"));
        ctx.setVariable("director1Id", vars.get("director1Id"));

        // =========================================================
        // GUARANTORS
        // =========================================================

        String processInstanceId =
                vars.get("processInstanceId").toString();

        List<GuarantorResponseDto> guarantors =
                guarantorService.getGuarantorsByProcessInstanceId(processInstanceId);

        ctx.setVariable("guarantors", guarantors);

        log.info("Wakala Agreement Guarantors : {}", guarantors);

        return ctx;
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

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sleep interrupted", e);
        }
    }
}