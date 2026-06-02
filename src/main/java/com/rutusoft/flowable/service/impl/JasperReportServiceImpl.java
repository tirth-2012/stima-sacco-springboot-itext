package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.service.JasperReportService;
import lombok.extern.slf4j.Slf4j;
//import net.sf.jasperreports.engine.*;
//import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JasperReportServiceImpl implements JasperReportService {
    @Override
    public byte[] generateLegalMemo(String processInstanceId) {
        return new byte[0];
    }

    /*private JasperReport creditMemoReport;

    @PostConstruct
    public void init() {
        try (InputStream is = new ClassPathResource("reports/legal_memo-v2.jrxml").getInputStream()) {

            this.creditMemoReport = JasperCompileManager.compileReport(is);

            log.info("Credit Memo Jasper report compiled successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load Credit Memo report", e);
        }
    }

    @Override
    public byte[] generateLegalMemo(String processInstanceId) {

        try {
            if (processInstanceId == null || processInstanceId.isEmpty()) {
                throw new IllegalArgumentException("processInstanceId is required");
            }

            // 🔹 Dummy data (replace with DB later)
            double monthlyIncome = 50000.0;
            double existingEMI = 10000.0;
            double proposedEMI = 12000.0;

            Map<String, Object> params = new HashMap<>();

            params.put("logo", getClass().getResource("/reports/logo/logo.svg").toString()); // ✅ FIX


            params.put("branchAddress", "Westlands Branch");

            // Borrower
            params.put("customerId", "CIF-3041");
            params.put("borrowerName", "Ramesh Patel");
            params.put("applicationId", "APP-2026-0001");

            // Loan
            params.put("loanType", "Home Loan");
            params.put("loanAmount", 1500000.0);
            params.put("interestRate", 10.5);
            params.put("tenureMonths", 60);
            params.put("sanctionDate", "26-Mar-2026");

            //Property
            params.put("propertyAddress", "Flat No. 402, Shree Residency,\n" +
                    "Satellite, Ahmedabad – 380015");
            params.put("propertyType", "Residential Flat");
            params.put("propertyArea", "500 sqft");


            // Financial
            params.put("monthlyIncome", 85000.0);
            params.put("existingEMI", 15000.0);
            params.put("proposedEMI", 20000.0);

            // Credit
            params.put("cibilScore", 765);
            params.put("loanEligibility", 1800000.0);

            // Legal specific
            params.put("propertyAddress", "Ahmedabad, Gujarat");
            params.put("legalClearance", "CLEAR & MARKETABLE TITLE");
            params.put("advocateName", "M/s Shah & Associates");
            params.put("legalRemarks", "All property documents verified. No encumbrance found.");
            params.put("legalOpinion", "Based on the documents reviewed and verification conducted,\n" +
                    "the property has a clear and marketable title and is legally acceptable for mortgage.");
            params.put("legalObservation", "Minor deviation in built-up area (within acceptable limits)\n" +
                    "No litigation found");
            // Recommendation
            params.put("recommendation", "Loan can be safely sanctioned based on legal and financial evaluation.");

            //Encumbrance
            params.put("encumbranceStatus", "No encumbrances observed");

            // Footer
            params.put("preparedBy", "Credit Manager");
            params.put("preparedDate", "26-Apr-2026");
            params.put("enrollmentNo", "156");

            String qrText = "http://localhost:8098/flowable-service/reports/legal-memo/1";
            params.put("qrText", qrText);


            // 🔹 Fill report
            JasperPrint print = JasperFillManager.fillReport(
                    creditMemoReport,
                    params,
                    new JREmptyDataSource()
            );

            // 🔹 Export PDF
            return JasperExportManager.exportReportToPdf(print);

        } catch (Exception e) {
            log.error("Error generating credit memo for processInstanceId={}", processInstanceId, e);
            throw new RuntimeException("Error generating Credit Memo", e);
        }*/

}