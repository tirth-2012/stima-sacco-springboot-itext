package com.rutusoft.flowable.bpm.service;

import com.rutusoft.flowable.dto.GuarantorResponseDto;
import com.rutusoft.flowable.service.GuarantorService;
import com.rutusoft.flowable.utility.MailNotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.IdentityService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.idm.api.User;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;


@Service("processIslamicFinancialNotificationService")
@Slf4j
@RequiredArgsConstructor
public class ProcessIslamicFinancialNotificationService {
    private final IdentityService identityService;
    private final MailNotificationUtil mailNotificationUtil;
    private final GuarantorService guarantorService;

    public void sendRequestInitiatedNotification(DelegateExecution execution) {

        log.info("Sending process initiation email to customer");

        String requester = getValue(execution, "requester");
        User requesterUser = identityService
                .createUserQuery()
                .userId(requester)
                .singleResult();
        String requesterEmailId = null;
        if (requesterUser != null && requesterUser.getEmail() != null) {
            requesterEmailId = requesterUser.getEmail();
        }
        log.info("Requester Email Id is: {}", requesterEmailId);



        Context context = new Context();

        // ===============================
        // 👤 CUSTOMER
        // ===============================
        context.setVariable("fullName", getValue(execution,"full_name"));
        context.setVariable("email", getValue(execution,"email_id"));
        context.setVariable("cifNumber", getValue(execution,"cif_number"));
        context.setVariable("requester", getValue(execution,"requester"));
        context.setVariable("requesterEmailId", requesterEmailId);
        context.setVariable("gender", getValue(execution,"gender"));
        context.setVariable("dob", getValue(execution,"date_of_birth"));
        context.setVariable("loanAmount", getValue(execution,"cost_price"));

        context.setVariable("nationalId", getValue(execution,"national_id"));
        context.setVariable("kraPin", getValue(execution,"kra_id"));
        context.setVariable("nationality", getValue(execution,"nationality"));
        context.setVariable("maritalStatus", getValue(execution,"marital_status"));

        context.setVariable("mobile", getValue(execution,"mobile_number"));
        context.setVariable("physicalAddress", getValue(execution,"physical_address"));
        context.setVariable("postalAddress", getValue(execution,"postal_address"));

        // ===============================
        // 💰 PRODUCT
        // ===============================
        context.setVariable("productType", getValue(execution,"product_type"));
        context.setVariable("productName", getValue(execution,"product_name"));
        context.setVariable("assetDescription", getValue(execution,"asset_description"));
        context.setVariable("tenure", getValue(execution,"financing_tenor"));
        context.setVariable("paymentStructure", getValue(execution,"payment_structure"));
        context.setVariable("financingAmount", getValue(execution,"cost_price"));
        context.setVariable("costPrice", getValue(execution,"cost_price"));
        context.setVariable("profitRate", getValue(execution,"profit_rate"));
        context.setVariable("profitAmount", getValue(execution,"profit_amount"));
        context.setVariable("monthlyInstallment", getValue(execution,"monthly_installment"));
        context.setVariable("Sector", getValue(execution,"sector_name"));
        context.setVariable("SubSector", getValue(execution,"sub_sector"));
        context.setVariable("Category", getValue(execution,"category"));
        context.setVariable("loan_purpose_description", getValue(execution,"loan_purpose_description"));
        context.setVariable("existing_monthly_obligations", getValue(execution,"existing_monthly_obligations"));


        // ===============================
        // 🏢 BUSINESS
        // ===============================
        context.setVariable("customer_category", getValue(execution,"customer_category"));
        context.setVariable("business_sector", getValue(execution,"business_sector"));
        context.setVariable("monthly_net_income", getValue(execution,"monthly_net_income"));
        context.setVariable("monthly_business_revenue", getValue(execution,"monthly_business_revenue"));
        context.setVariable("annual_turnover", getValue(execution,"annual_turnover"));
        context.setVariable("yearsOfBusiness", getValue(execution,"years_of_Business"));
        context.setVariable("years_of_Business", getValue(execution,"years_of_Business"));
        context.setVariable("existingFacilities", getValue(execution,"number_of_existing_facilities"));
        context.setVariable("proposedInstallment", getValue(execution,"proposed_instalment"));
        context.setVariable("afterFacility", getValue(execution,"after_this_facility"));

        // ===============================
        // 🔐 SECURITY
        // ===============================
        context.setVariable("collateralType", getValue(execution,"security_type"));
        context.setVariable("securityDescription", getValue(execution,"security_description"));
        context.setVariable("securityOwnership", getValue(execution,"security_ownership"));

        // ===============================
        // 🏦 BANK
        // ===============================
        context.setVariable("bankHolderName", getValue(execution,"bank_holder_name"));
        context.setVariable("accountNumber", getValue(execution,"account_number"));
        context.setVariable("accountType", getValue(execution,"account_type"));

        // ===============================
        // 🚗 / 🏠 ASSET DETAILS
        // ===============================
        context.setVariable("vehicleRegistration", getValue(execution,"vehicle_registration"));
        context.setVariable("chassisNumber", getValue(execution,"chassis_number"));
        context.setVariable("titleDeedNumber", getValue(execution,"title_deed_number"));
        context.setVariable("propertyLocation", getValue(execution,"property_location"));

        context.setVariable("valuerName", getValue(execution,"valuer_name"));
        context.setVariable("valuationDate", getValue(execution,"valuation_date"));
        context.setVariable("marketValue", getValue(execution,"estimated_market_value"));
        context.setVariable("forcedSaleValue", getValue(execution,"forced_sale_value"));

        // ===============================
        // 👥 GUARANTOR
        // ===============================
        context.setVariable("guarantorCustomerId", getValue(execution,"guarantor_customer_id"));
        context.setVariable("guarantorName", getValue(execution,"guarantor_full_name"));
        context.setVariable("guarantorNationalId", getValue(execution,"guarantor_national_id"));
        context.setVariable("guarantorAddress", getValue(execution,"guarantor_address"));
        context.setVariable("guarantorMobile", getValue(execution,"guarantor_mobile"));
        context.setVariable("guarantorEmail", getValue(execution,"guarantor_email"));
        context.setVariable("guarantorStatus", getValue(execution,"guarantor_status"));

        context.setVariable("employer", getValue(execution,"employer_name"));
        context.setVariable("grossIncome", getValue(execution,"guarantor_gross_income"));
        context.setVariable("netIncome", getValue(execution,"guarantor_net_income"));
        context.setVariable("obligations", getValue(execution,"guarantor_obligations"));
        context.setVariable("totalSalePrice", getValue(execution,"total_sale_price"));

        // ===============================
        context.setVariable(
                "viewLink",
                "http://localhost:5173/applications/" + execution.getProcessInstanceId()
        );
        // ===============================
        // 👥 GUARANTORS LIST
        // ===============================
        String processInstanceId = execution.getProcessInstanceId();

        List<GuarantorResponseDto> guarantors =
                guarantorService.getGuarantorsByProcessInstanceId(processInstanceId);

        context.setVariable("guarantors", guarantors);

        log.info("guarantors is {}",guarantors);

        String subject = "Your Financing Request is Initiated";


        mailNotificationUtil.sendEmail(
                getValue(execution, "email_id"),
                subject,
                "email/islamic-financial-initiation",
                context
        );

        // ===============================
        // 📧 SEND TO REQUESTER
        // ===============================

        if (requesterEmailId != null && !requesterEmailId.isEmpty()) {

            mailNotificationUtil.sendEmail(
                    requesterEmailId,
                    subject,
                    "email/islamic-financial-initiation",
                    context
            );

            log.info("Initiation notification sent to REQUESTER {}", requesterEmailId);
        }
    }


    public void sendRequestCompletedNotification(DelegateExecution execution) {

        log.info("Sending process completion email to requester");

        String requester = getValue(execution, "requester");
        User requesterUser = identityService
                .createUserQuery()
                .userId(requester)
                .singleResult();
        String requesterEmailId = null;
        if (requesterUser != null && requesterUser.getEmail() != null) {
            requesterEmailId = requesterUser.getEmail();
        }


        Context context = new Context();

        // ===============================
        // 👤 CUSTOMER
        // ===============================
        context.setVariable("fullName", getValue(execution,"full_name"));
        context.setVariable("email", getValue(execution,"email_id"));
        context.setVariable("cifNumber", getValue(execution,"cif_number"));
        context.setVariable("requester", getValue(execution,"requester"));
        context.setVariable("requesterEmailId", requesterEmailId);
        context.setVariable("gender", getValue(execution,"gender"));
        context.setVariable("dob", getValue(execution,"date_of_birth"));
        context.setVariable("loanAmount", getValue(execution,"cost_price"));

        context.setVariable("nationalId", getValue(execution,"national_id"));
        context.setVariable("kraPin", getValue(execution,"kra_id"));
        context.setVariable("nationality", getValue(execution,"nationality"));
        context.setVariable("maritalStatus", getValue(execution,"marital_status"));

        context.setVariable("mobile", getValue(execution,"mobile_number"));
        context.setVariable("physicalAddress", getValue(execution,"physical_address"));
        context.setVariable("postalAddress", getValue(execution,"postal_address"));

        // ===============================
        // 💰 PRODUCT
        // ===============================
        context.setVariable("productType", getValue(execution,"product_type"));
        context.setVariable("productName", getValue(execution,"product_name"));
        context.setVariable("assetDescription", getValue(execution,"asset_description"));
        context.setVariable("tenure", getValue(execution,"financing_tenor"));
        context.setVariable("paymentStructure", getValue(execution,"payment_structure"));
        context.setVariable("financingAmount", getValue(execution,"cost_price"));
        context.setVariable("costPrice", getValue(execution,"cost_price"));
        context.setVariable("profitRate", getValue(execution,"profit_rate"));
        context.setVariable("profitAmount", getValue(execution,"profit_amount"));
        context.setVariable("monthlyInstallment", getValue(execution,"monthly_installment"));
        context.setVariable("Sector", getValue(execution,"sector_name"));
        context.setVariable("SubSector", getValue(execution,"sub_sector"));
        context.setVariable("Category", getValue(execution,"category"));
        context.setVariable("loan_purpose_description", getValue(execution,"loan_purpose_description"));
        context.setVariable("existing_monthly_obligations", getValue(execution,"existing_monthly_obligations"));


        // ===============================
        // 🏢 BUSINESS
        // ===============================
        context.setVariable("customer_category", getValue(execution,"customer_category"));
        context.setVariable("business_sector", getValue(execution,"business_sector"));
        context.setVariable("monthly_net_income", getValue(execution,"monthly_net_income"));
        context.setVariable("monthly_business_revenue", getValue(execution,"monthly_business_revenue"));
        context.setVariable("annual_turnover", getValue(execution,"annual_turnover"));
        context.setVariable("yearsOfBusiness", getValue(execution,"years_of_Business"));
        context.setVariable("years_of_Business", getValue(execution,"years_of_Business"));
        context.setVariable("existingFacilities", getValue(execution,"number_of_existing_facilities"));
        context.setVariable("proposedInstallment", getValue(execution,"proposed_instalment"));
        context.setVariable("afterFacility", getValue(execution,"after_this_facility"));

        // ===============================
        // 🔐 SECURITY
        // ===============================
        context.setVariable("collateralType", getValue(execution,"security_type"));
        context.setVariable("securityDescription", getValue(execution,"security_description"));
        context.setVariable("securityOwnership", getValue(execution,"security_ownership"));

        // ===============================
        // 🏦 BANK
        // ===============================
        context.setVariable("bankHolderName", getValue(execution,"bank_holder_name"));
        context.setVariable("accountNumber", getValue(execution,"account_number"));
        context.setVariable("accountType", getValue(execution,"account_type"));

        // ===============================
        // 🚗 / 🏠 ASSET DETAILS
        // ===============================
        context.setVariable("vehicleRegistration", getValue(execution,"vehicle_registration"));
        context.setVariable("chassisNumber", getValue(execution,"chassis_number"));
        context.setVariable("titleDeedNumber", getValue(execution,"title_deed_number"));
        context.setVariable("propertyLocation", getValue(execution,"property_location"));

        context.setVariable("valuerName", getValue(execution,"valuer_name"));
        context.setVariable("valuationDate", getValue(execution,"valuation_date"));
        context.setVariable("marketValue", getValue(execution,"estimated_market_value"));
        context.setVariable("forcedSaleValue", getValue(execution,"forced_sale_value"));

        // ===============================
        // 👥 GUARANTOR
        // ===============================
        context.setVariable("guarantorCustomerId", getValue(execution,"guarantor_customer_id"));
        context.setVariable("guarantorName", getValue(execution,"guarantor_full_name"));
        context.setVariable("guarantorNationalId", getValue(execution,"guarantor_national_id"));
        context.setVariable("guarantorAddress", getValue(execution,"guarantor_address"));
        context.setVariable("guarantorMobile", getValue(execution,"guarantor_mobile"));
        context.setVariable("guarantorEmail", getValue(execution,"guarantor_email"));
        context.setVariable("guarantorStatus", getValue(execution,"guarantor_status"));

        context.setVariable("employer", getValue(execution,"employer_name"));
        context.setVariable("grossIncome", getValue(execution,"guarantor_gross_income"));
        context.setVariable("netIncome", getValue(execution,"guarantor_net_income"));
        context.setVariable("obligations", getValue(execution,"guarantor_obligations"));
        context.setVariable("totalSalePrice", getValue(execution,"total_sale_price"));

        context.setVariable("processInstanceId", execution.getProcessInstanceId());
        context.setVariable("completionTime", java.time.LocalDateTime.now());

        // ===============================
        // 📧 SUBJECT
        // ===============================
        context.setVariable(
                "viewLink",
                "http://localhost:5173/applications/" + execution.getProcessInstanceId()
        );
        // ===============================
        // 👥 GUARANTORS LIST
        // ===============================
        String processInstanceId = execution.getProcessInstanceId();

        List<GuarantorResponseDto> guarantors =
                guarantorService.getGuarantorsByProcessInstanceId(processInstanceId);

        context.setVariable("guarantors", guarantors);

        log.info("guarantors is {}",guarantors);

        String subject = "Your Financing Request has been Completed";

        // ===============================
        // 📩 SEND EMAIL TO REQUESTER
        // ===============================
        mailNotificationUtil.sendEmail(
                getValue(execution, "email_id"),
                subject,
                "email/islamic-financial-completed",
                context
        );

        // ===============================
        // 📧 SEND TO REQUESTER
        // ===============================

        if (requesterEmailId != null && !requesterEmailId.isEmpty()) {

            mailNotificationUtil.sendEmail(
                    requesterEmailId,
                    subject,
                    "email/islamic-financial-initiation",
                    context
            );

            log.info("Initiation notification sent to REQUESTER {}", requesterEmailId);
        }
    }

    public void  slaViolationNotification(DelegateExecution execution) {
        //TO DO

    }

    public void declineConfirmationNotification(DelegateExecution execution) {
        log.info("Sending request declined notification due to CRB Bureau score thresh hold violated email to requester");

        String requester = getValue(execution, "requester");
        User requesterUser = identityService
                .createUserQuery()
                .userId(requester)
                .singleResult();
        String requesterEmailId = null;
        if (requesterUser != null && requesterUser.getEmail() != null) {
            requesterEmailId = requesterUser.getEmail();
        }
        Context context = new Context();

        // ===============================
        // 👤 CUSTOMER
        // ===============================
        context.setVariable("fullName", getValue(execution,"full_name"));
        context.setVariable("email", getValue(execution,"email_id"));
        context.setVariable("cifNumber", getValue(execution,"cif_number"));
        context.setVariable("requester", getValue(execution,"requester"));
        context.setVariable("requesterEmailId", requesterEmailId);
        context.setVariable("gender", getValue(execution,"gender"));
        context.setVariable("dob", getValue(execution,"date_of_birth"));
        context.setVariable("loanAmount", getValue(execution,"cost_price"));

        context.setVariable("nationalId", getValue(execution,"national_id"));
        context.setVariable("kraPin", getValue(execution,"kra_id"));
        context.setVariable("nationality", getValue(execution,"nationality"));
        context.setVariable("maritalStatus", getValue(execution,"marital_status"));

        context.setVariable("mobile", getValue(execution,"mobile_number"));
        context.setVariable("physicalAddress", getValue(execution,"physical_address"));
        context.setVariable("postalAddress", getValue(execution,"postal_address"));

        // ===============================
        // 💰 PRODUCT
        // ===============================
        context.setVariable("productType", getValue(execution,"product_type"));
        context.setVariable("productName", getValue(execution,"product_name"));
        context.setVariable("assetDescription", getValue(execution,"asset_description"));
        context.setVariable("tenure", getValue(execution,"financing_tenor"));
        context.setVariable("paymentStructure", getValue(execution,"payment_structure"));
        context.setVariable("financingAmount", getValue(execution,"cost_price"));
        context.setVariable("costPrice", getValue(execution,"cost_price"));
        context.setVariable("profitRate", getValue(execution,"profit_rate"));
        context.setVariable("profitAmount", getValue(execution,"profit_amount"));
        context.setVariable("monthlyInstallment", getValue(execution,"monthly_installment"));
        context.setVariable("Sector", getValue(execution,"sector_name"));
        context.setVariable("SubSector", getValue(execution,"sub_sector"));
        context.setVariable("Category", getValue(execution,"category"));
        context.setVariable("loan_purpose_description", getValue(execution,"loan_purpose_description"));
        context.setVariable("existing_monthly_obligations", getValue(execution,"existing_monthly_obligations"));


        // ===============================
        // 🏢 BUSINESS
        // ===============================
        context.setVariable("customer_category", getValue(execution,"customer_category"));
        context.setVariable("business_sector", getValue(execution,"business_sector"));
        context.setVariable("monthly_net_income", getValue(execution,"monthly_net_income"));
        context.setVariable("monthly_business_revenue", getValue(execution,"monthly_business_revenue"));
        context.setVariable("annual_turnover", getValue(execution,"annual_turnover"));
        context.setVariable("yearsOfBusiness", getValue(execution,"years_of_Business"));
        context.setVariable("years_of_Business", getValue(execution,"years_of_Business"));
        context.setVariable("existingFacilities", getValue(execution,"number_of_existing_facilities"));
        context.setVariable("proposedInstallment", getValue(execution,"proposed_instalment"));
        context.setVariable("afterFacility", getValue(execution,"after_this_facility"));

        // ===============================
        // 🔐 SECURITY
        // ===============================
        context.setVariable("collateralType", getValue(execution,"security_type"));
        context.setVariable("securityDescription", getValue(execution,"security_description"));
        context.setVariable("securityOwnership", getValue(execution,"security_ownership"));

        // ===============================
        // 🏦 BANK
        // ===============================
        context.setVariable("bankHolderName", getValue(execution,"bank_holder_name"));
        context.setVariable("accountNumber", getValue(execution,"account_number"));
        context.setVariable("accountType", getValue(execution,"account_type"));

        // ===============================
        // 🚗 / 🏠 ASSET DETAILS
        // ===============================
        context.setVariable("vehicleRegistration", getValue(execution,"vehicle_registration"));
        context.setVariable("chassisNumber", getValue(execution,"chassis_number"));
        context.setVariable("titleDeedNumber", getValue(execution,"title_deed_number"));
        context.setVariable("propertyLocation", getValue(execution,"property_location"));

        context.setVariable("valuerName", getValue(execution,"valuer_name"));
        context.setVariable("valuationDate", getValue(execution,"valuation_date"));
        context.setVariable("marketValue", getValue(execution,"estimated_market_value"));
        context.setVariable("forcedSaleValue", getValue(execution,"forced_sale_value"));

        // ===============================
        // 👥 GUARANTOR
        // ===============================
        context.setVariable("guarantorCustomerId", getValue(execution,"guarantor_customer_id"));
        context.setVariable("guarantorName", getValue(execution,"guarantor_full_name"));
        context.setVariable("guarantorNationalId", getValue(execution,"guarantor_national_id"));
        context.setVariable("guarantorAddress", getValue(execution,"guarantor_address"));
        context.setVariable("guarantorMobile", getValue(execution,"guarantor_mobile"));
        context.setVariable("guarantorEmail", getValue(execution,"guarantor_email"));
        context.setVariable("guarantorStatus", getValue(execution,"guarantor_status"));

        context.setVariable("employer", getValue(execution,"employer_name"));
        context.setVariable("grossIncome", getValue(execution,"guarantor_gross_income"));
        context.setVariable("netIncome", getValue(execution,"guarantor_net_income"));
        context.setVariable("obligations", getValue(execution,"guarantor_obligations"));
        context.setVariable("totalSalePrice", getValue(execution,"total_sale_price"));

        context.setVariable("processInstanceId", execution.getProcessInstanceId());
        context.setVariable("completionTime", java.time.LocalDateTime.now());

        // ===============================
        // 📧 SUBJECT
        // ===============================
        context.setVariable(
                "viewLink",
                "http://localhost:5173/applications/" + execution.getProcessInstanceId()
        );

        // ===============================
        // 👥 GUARANTORS LIST
        // ===============================
        String processInstanceId = execution.getProcessInstanceId();

        List<GuarantorResponseDto> guarantors =
                guarantorService.getGuarantorsByProcessInstanceId(processInstanceId);

        context.setVariable("guarantors", guarantors);

        log.info("guarantors is {}",guarantors);

        String subject = "Your Financing Request has been declined due to CRB Bureau Score thresh hold violated";

        // ===============================
        // 📩 SEND EMAIL TO REQUESTER
        // ===============================
        mailNotificationUtil.sendEmail(
                getValue(execution, "email_id"),
                subject,
                "email/islamic-financial-completed",
                context
        );
    }

    public void sendGuarantorApprovalNotification(DelegateExecution execution) {
        log.info("Sending guarantor approval email");
        Context context = new Context();

        // ===============================
        // CUSTOMER DETAILS
        // ===============================
        context.setVariable("fullName", getValue(execution, "full_name"));
        context.setVariable("email", getValue(execution, "email_id"));
        context.setVariable("mobile", getValue(execution, "mobile_number"));

        // ===============================
        // FINANCING DETAILS
        // ===============================
        context.setVariable("productName", getValue(execution, "product_name"));
        context.setVariable("loanAmount", getValue(execution, "cost_price"));
        context.setVariable("monthlyInstallment", getValue(execution, "monthly_installment"));
        context.setVariable("profitRate", getValue(execution, "profit_rate"));
        context.setVariable("totalSalePrice", getValue(execution, "total_sale_price"));
        String referenceId = getValue(execution, "referenceId");


        // ===============================
        // 👥 CURRENT GUARANTOR DETAILS
        // ===============================
        String processInstanceId = execution.getProcessInstanceId();

        List<GuarantorResponseDto> guarantors =
                guarantorService.getGuarantorsByProcessInstanceId(processInstanceId);

        log.info("Guarantors : {}", guarantors);

        for (GuarantorResponseDto guarantorResponseDto : guarantors) {

            // create separate context for each guarantor
            Context guarantorContext = new Context();

            // ===============================
            // CUSTOMER DETAILS
            // ===============================
            guarantorContext.setVariable("fullName", getValue(execution, "full_name"));
            guarantorContext.setVariable("email", getValue(execution, "email_id"));
            guarantorContext.setVariable("mobile", getValue(execution, "mobile_number"));


            // ===============================
            // FINANCING DETAILS
            // ===============================
            guarantorContext.setVariable("productName", getValue(execution, "product_name"));
            guarantorContext.setVariable("loanAmount", getValue(execution, "cost_price"));
            guarantorContext.setVariable("monthlyInstallment", getValue(execution, "monthly_installment"));
            guarantorContext.setVariable("profitRate", getValue(execution, "profit_rate"));
            guarantorContext.setVariable("totalSalePrice", getValue(execution, "total_sale_price"));

            // ===============================
            // CURRENT GUARANTOR DETAILS ONLY
            // ===============================

            guarantorContext.setVariable("guarantorName", guarantorResponseDto.getCustomerName());
            guarantorContext.setVariable("guarantorEmail", guarantorResponseDto.getCustomerEmail());
            guarantorContext.setVariable("guarantorMobile", guarantorResponseDto.getCustomerMobile());
            // guarantee amount
            guarantorContext.setVariable("guaranteeAmount", guarantorResponseDto.getGuarantorAmount());
            guarantorContext.setVariable("guarantorMemberNumber", guarantorResponseDto.getMemberNumber());
            guarantorContext.setVariable("applicant_username", guarantorResponseDto.getGuaranteeId());

            // ===============================
            // VIEW LINK
            // ===============================
            guarantorContext.setVariable(
                    "viewLink",
                    "http://localhost:5173/guarantor-consent"
            );

            String subject = "Guarantor : "+guarantorResponseDto.getId()+" Approval Request Notification for loan application no : "+referenceId;

            mailNotificationUtil.sendEmail(
                    guarantorResponseDto.getCustomerEmail(),
                    subject,
                    "email/guarantor-approval",
                    guarantorContext
            );

            log.info("Guarantor approval notification sent to {}",
                    guarantorResponseDto.getCustomerEmail());
        }
    }

    private String getValue(DelegateExecution execution, String key) {
        return execution.getVariable(key) == null ? "" : execution.getVariable(key).toString();
    }
}
