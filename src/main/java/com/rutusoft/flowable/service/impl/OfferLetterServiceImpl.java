package com.rutusoft.flowable.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.rutusoft.flowable.service.DocumentService;
import com.rutusoft.flowable.service.OfferLetterService;
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
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfferLetterServiceImpl implements OfferLetterService {
    private final TemplateEngine templateEngine;
    private final HistoryService historyService;
    private final ProcessInstanceVariablesService processInstanceVariablesService;
    private final DocumentService documentService;
    private final MailNotificationUtil mailNotificationUtil;
    private final GuarantorService guarantorService;

    public void generateAndUploadOfferLetter(DelegateExecution execution) {
        log.info("Generating and uploading offer letter to mayan");
        String processInstanceId = execution.getProcessInstanceId();
        try {
            byte[] bytes = generateOfferLetter(processInstanceId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] generateOfferLetter(String processInstanceId) {

        log.info("START: Generating offer letter for processInstanceId={}", processInstanceId);

        try {
            // ----------------------------------------------------------------
            // Fetch Process Variables
            // ----------------------------------------------------------------
            Map<String, Object> variables =
                    processInstanceVariablesService.getProcessInstanceVariablesMap(processInstanceId);

            HistoricProcessInstance processInstance =
                    historyService.createHistoricProcessInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .singleResult();

            if (processInstance == null) {
                throw new RuntimeException("Process instance not found");
            }

            variables.put("applicationDate", processInstance.getStartTime());
            variables.put("processInstanceId", processInstanceId);

            String productCategory = (String) variables.get("product_type");
            String referenceId = (String) variables.get("referenceId");

            log.info("Product Category: {}, ReferenceId: {}", productCategory, referenceId);

            // ----------------------------------------------------------------
            // Template Selection
            // ----------------------------------------------------------------
            String templateName = productCategory.contains("Conventional")
                    ? "offer-letters/offer_letter_conventional"
                    : "offer-letters/offer_letter_wakala";

            log.info("Selected Template: {}", templateName);

            // ----------------------------------------------------------------
            // Generate HTML using Thymeleaf
            // ----------------------------------------------------------------
            Context ctx = buildContext(variables, productCategory);
            String html = templateEngine.process(templateName, ctx);

            log.debug("Generated HTML length: {}", html.length());

            // ----------------------------------------------------------------
            // Convert HTML → PDF
            // ----------------------------------------------------------------
            byte[] offerLetter = renderToPdf(html);

            log.info("PDF generated successfully for referenceId={}", referenceId);

            // ----------------------------------------------------------------
            // Upload to Mayan
            // ----------------------------------------------------------------
            MultipartFile multipartFile = new ByteArrayMultipartFile(
                    offerLetter,
                    "Offer Letter",
                    "offer-letter-" + referenceId + ".pdf",
                    "application/pdf"
            );

            Long documentTypeId = documentService.getDocumentTypeIdByName("Offer letter");
            log.info("DocumentTypeId: {}", documentTypeId);

            String documentResponse = documentService.uploadDocument(
                    documentTypeId,
                    "Offer Letter",
                    "System generated Offer letter",
                    multipartFile
            );

            log.info("Document uploaded successfully");

            Map<String, Object> responseDocumentMap = JsonUtils.toMap(documentResponse);
            Long documentId = JsonUtils.getLong(responseDocumentMap, "id");

            log.info("Generated DocumentId: {}", documentId);

            // ----------------------------------------------------------------
            // Add Metadata
            // ----------------------------------------------------------------
            Map<String, Long> metadataMap = documentService.getAllMetadataTypes();

            documentService.addMetadata(documentId, metadataMap.get("processInstanceId"), processInstanceId);
            sleep(400);

            documentService.addMetadata(documentId, metadataMap.get("uploadedBy"), "System");
            sleep(400);

            documentService.addMetadata(
                    documentId,
                    metadataMap.get("status"),
                    "Pending for signature - " + LocalDate.now()
            );
            sleep(400);

            log.info("Metadata added successfully");

            // ----------------------------------------------------------------
            // Send Email
            // ----------------------------------------------------------------
            if (variables.get("email_id") != null) {
                try {
                    String email = variables.get("email_id").toString();
                    String subject = "Offer letter for loan application #" + referenceId;

                    String emailBody = "Dear " + variables.get("full_name")
                            + ", Please find attached offer letter for your loan application.";

                    mailNotificationUtil.sendEmailWithAttachment(
                            email,
                            subject,
                            emailBody,
                            offerLetter,
                            "offer-letter-" + referenceId + ".pdf"
                    );

                    log.info("Email sent successfully to {}", email);

                } catch (Exception emailEx) {
                    log.error("FAILED to send email for processInstanceId={}", processInstanceId, emailEx);
                }
            } else {
                log.warn("Email not found in variables for processInstanceId={}", processInstanceId);
            }

            log.info("END: Offer letter generation completed successfully");

            return offerLetter;

        } catch (Exception e) {
            log.error("ERROR while generating offer letter for processInstanceId={}", processInstanceId, e);
            throw new RuntimeException("Failed to generate offer letter", e);
        }
    }

    private Context buildContext(Map<String, Object> vars, String productCategory) {
        Context ctx = new Context();

        // ── Shared variables (both products) ──────────────────────────────────
        ctx.setVariable("applicationRef",    vars.get("referenceId"));
        ctx.setVariable("offerDate",         LocalDate.now().toString());
        ctx.setVariable("applicationDate",   vars.get("applicationDate"));
        ctx.setVariable("customerName",      vars.get("full_name"));
        ctx.setVariable("contactPerson",     vars.get("full_name"));
        ctx.setVariable("customerAddress",   vars.get("postal_address"));
        ctx.setVariable("customerEmail",     vars.get("email_id"));
        ctx.setVariable("customerPhone",     vars.get("mobile_number"));

        ctx.setVariable("paymentMethod",     vars.get("payment_structure").toString().toLowerCase());


        ctx.setVariable("facilityAmount",    formatCurrency(vars.get("cost_price")));

        Map<String, Object> obligationsMap = JsonUtils.toMap(vars.get("obligations"));
        Long facilityAmount =  JsonUtils.getLong(obligationsMap, "total");
        ctx.setVariable("facilityAmountWords", vars.get("cost_price"));
        ctx.setVariable("facilityPurpose",   vars.get("asset_description"));
        ctx.setVariable("tenorMonths",       vars.get("financing_tenor"));
        ctx.setVariable("monthlyInstalment", formatCurrency(vars.get("monthly_installment")));
        ctx.setVariable("collateralDescription", vars.get("security_description"));
        String collateralRef =  "Title deed no.  : "+vars.get("title_deed_number")+ ", Location : "+vars.get("property_location");
        ctx.setVariable("collateralRef",     collateralRef);
        ctx.setVariable("collateralFSV",     formatCurrency(vars.get("forced_sale_value")));
        ctx.setVariable("coverageRatio",     vars.get("coverage_ratio"));
        ctx.setVariable("approverName",      "Fatuma Ochieng");
        ctx.setVariable("approverTitle",     "Sr. Credit Manager");
        ctx.setVariable("dateOfApproval",     LocalDate.now().toString());
        ctx.setVariable("director1Name",     vars.get("director1Name"));
        ctx.setVariable("director1Id",       vars.get("director1Id"));
        ctx.setVariable("director2Name",     vars.get("director2Name"));
        ctx.setVariable("director2Id",       vars.get("director2Id"));

        Double loanAmount = Double.parseDouble(vars.get("cost_price").toString());

        Double facilityFee = loanAmount * 0.03;
        ctx.setVariable("facilityFee", formatCurrency(facilityFee));

        Double exciseDuty = facilityFee * 0.20;
        ctx.setVariable("exciseDuty", formatCurrency(exciseDuty));

        ctx.setVariable("insurance", "Credit life insurance is mandatory, often charged around 0.035% of the loan value.");

        // Conditions precedent — stored as a List<String> in process variables
        //ctx.setVariable("conditionsPrecedent", (List<String>) vars.get("conditionsPrecedent"));
        // Covenants — stored as a List<String>
        //ctx.setVariable("covenants", (List<String>) vars.get("covenants"));

        // ----------------------------------------------------------------
        // GUARANTORS
        // ----------------------------------------------------------------
        String processInstanceId = vars.get("processInstanceId").toString();

        List<GuarantorResponseDto> guarantors =
                guarantorService.getGuarantorsByProcessInstanceId(processInstanceId);

        ctx.setVariable("guarantors", guarantors);

        log.info("Offer Letter Guarantors : {}", guarantors);

        if (productCategory.contains("Conventional")) {
            // ── Conventional-only variables ───────────────────────────────────
            ctx.setVariable("interestRate",      vars.get("profit_rate"));
            ctx.setVariable("totalRepayable",    formatCurrency(vars.get("total_loan_amount")));
            ctx.setVariable("totalInterest",     formatCurrency(vars.get("profit_amount")));
            Double creditLifeFee = loanAmount * 0.05;
            ctx.setVariable("creditLifeFee", formatCurrency(creditLifeFee));
            ctx.setVariable("securityItems",
                    (List<String>) vars.get("securityItems"));

        } else {
            // ── Islamic (Wakala)-only variables ───────────────────────────────
            ctx.setVariable("profitAmount",      formatCurrency(vars.get("profit_amount")));
            ctx.setVariable("totalSalePrice",    formatCurrency(vars.get("estimated_market_value")));
            ctx.setVariable("profitRate",        vars.get("profit_amount"));
            ctx.setVariable("firstInstalmentDate", vars.get("firstInstalmentDate"));
            ctx.setVariable("lastInstalmentDate",  vars.get("lastInstalmentDate"));
            ctx.setVariable("assetDescription",  vars.get("asset_description"));
            ctx.setVariable("supplierName",      vars.get("owner_name"));
            ctx.setVariable("supplierInvoiceRef",vars.get("supplierInvoiceRef"));
            ctx.setVariable("shariahApprovalRef",vars.get("shariahApprovalRef"));
            ctx.setVariable("shariahApprovalDate",vars.get("shariahApprovalDate"));
        }

        return ctx;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Flying Saucer PDF renderer
    // Dependency in pom.xml: com.openhtmltopdf:openhtmltopdf-pdfbox:1.0.10
    // ─────────────────────────────────────────────────────────────────────────
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
    private String formatCurrency(Object value) {
        if (value == null) return "—";
        try {
            double d = Double.parseDouble(value.toString());
            return String.format("KES %,.2f", d);
        } catch (NumberFormatException e) {
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
