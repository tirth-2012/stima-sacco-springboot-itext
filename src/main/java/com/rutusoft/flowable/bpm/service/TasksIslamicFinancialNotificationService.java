package com.rutusoft.flowable.bpm.service;

import com.rutusoft.flowable.dto.GuarantorResponseDto;
import com.rutusoft.flowable.service.GuarantorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.engine.IdentityService;
import org.flowable.idm.api.User;
import org.springframework.stereotype.Service;
import org.flowable.identitylink.api.IdentityLink;
import com.rutusoft.flowable.utility.MailNotificationUtil;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Set;

@Service("tasksIslamicFinancialNotificationService")
@Slf4j
@RequiredArgsConstructor
public class TasksIslamicFinancialNotificationService {
    private final IdentityService identityService;
    private final MailNotificationUtil mailNotificationUtil;
    private final GuarantorService guarantorService;

    public void sendTaskCreatedNotification(DelegateTask task) {

        String eventName = task.getEventName();
        log.info("Task Event: {}", eventName);

        Set<IdentityLink> identityLinks = task.getCandidates();

        for (IdentityLink identityLink : identityLinks) {

            if ("candidate".equals(identityLink.getType())) {

                String groupId = identityLink.getGroupId();
                log.info("Group: {}", groupId);

                List<User> users = identityService
                        .createUserQuery()
                        .memberOfGroup(groupId)
                        .list();

                log.info("Total users in group: {}", users.size());

                for (User user : users) {

                    String email = user.getEmail();

                    if (email == null || email.isEmpty()) {
                        log.warn("User {} has no email", user.getId());
                        continue;
                    }

                    Context context = new Context();

                    // ===============================
                    // 👤 CUSTOMER
                    // ===============================
                    context.setVariable("fullName", getTaskValue(task,"full_name"));
                    context.setVariable("email", getTaskValue(task,"email_id"));
                    context.setVariable("cifNumber", getTaskValue(task,"cif_number"));
                    context.setVariable("requester", getTaskValue(task,"requester"));
                    context.setVariable("gender", getTaskValue(task,"gender"));
                    context.setVariable("dob", getTaskValue(task,"date_of_birth"));
                    context.setVariable("loanAmount", getTaskValue(task,"cost_price"));

                    context.setVariable("nationalId", getTaskValue(task,"national_id"));
                    context.setVariable("kraPin", getTaskValue(task,"kra_id"));
                    context.setVariable("nationality", getTaskValue(task,"nationality"));
                    context.setVariable("maritalStatus", getTaskValue(task,"marital_status"));

                    context.setVariable("mobile", getTaskValue(task,"mobile_number"));
                    context.setVariable("physicalAddress", getTaskValue(task,"physical_address"));
                    context.setVariable("postalAddress", getTaskValue(task,"postal_address"));

                    // ===============================
                    // 💰 PRODUCT
                    // ===============================
                    context.setVariable("productType", getTaskValue(task, "product_type"));
                    context.setVariable("productName", getTaskValue(task, "product_name"));
                    context.setVariable("assetDescription", getTaskValue(task, "asset_description"));
                    context.setVariable("tenure", getTaskValue(task, "financing_tenor"));
                    context.setVariable("paymentStructure", getTaskValue(task, "payment_structure"));
                    context.setVariable("financingAmount", getTaskValue(task, "cost_price"));
                    context.setVariable("costPrice", getTaskValue(task, "cost_price"));
                    context.setVariable("profitRate", getTaskValue(task, "profit_rate"));
                    context.setVariable("profitAmount", getTaskValue(task, "profit_amount"));
                    context.setVariable("monthlyInstallment", getTaskValue(task, "monthly_installment"));
                    context.setVariable("Sector", getTaskValue(task, "sector_name"));
                    context.setVariable("SubSector", getTaskValue(task, "sub_sector"));
                    context.setVariable("Category", getTaskValue(task, "category"));
                    context.setVariable("loan_purpose_description", getTaskValue(task, "loan_purpose_description"));
                    context.setVariable("existing_monthly_obligations", getTaskValue(task, "existing_monthly_obligations"));


                    // ===============================
                    // 🏢 BUSINESS
                    // ===============================
                    context.setVariable("customer_category", getTaskValue(task, "customer_category"));
                    context.setVariable("business_sector", getTaskValue(task, "business_sector"));
                    context.setVariable("monthly_net_income", getTaskValue(task, "monthly_net_income"));
                    context.setVariable("monthly_business_revenue", getTaskValue(task, "monthly_business_revenue"));
                    context.setVariable("annual_turnover", getTaskValue(task, "annual_turnover"));
                    context.setVariable("yearsOfBusiness", getTaskValue(task, "years_of_Business"));
                    context.setVariable("years_of_Business", getTaskValue(task, "years_of_Business"));
                    context.setVariable("existingFacilities", getTaskValue(task, "number_of_existing_facilities"));
                    context.setVariable("proposedInstallment", getTaskValue(task, "proposed_instalment"));
                    context.setVariable("afterFacility", getTaskValue(task, "after_this_facility"));

                    // ===============================
                    // 🔐 SECURITY
                    // ===============================
                    context.setVariable("collateralType", getTaskValue(task,"security_type"));
                    context.setVariable("securityDescription", getTaskValue(task,"security_description"));
                    context.setVariable("securityOwnership", getTaskValue(task,"security_ownership"));

                    // ===============================
                    // 🏦 BANK
                    // ===============================
                    context.setVariable("bankHolderName", getTaskValue(task,"bank_holder_name"));
                    context.setVariable("accountNumber", getTaskValue(task,"account_number"));
                    context.setVariable("accountType", getTaskValue(task,"account_type"));

                    // ===============================
                    // 🚗 / 🏠 ASSET DETAILS
                    // ===============================
                    context.setVariable("vehicleRegistration", getTaskValue(task,"vehicle_registration"));
                    context.setVariable("chassisNumber", getTaskValue(task,"chassis_number"));
                    context.setVariable("titleDeedNumber", getTaskValue(task,"title_deed_number"));
                    context.setVariable("propertyLocation", getTaskValue(task,"property_location"));

                    context.setVariable("valuerName", getTaskValue(task,"valuer_name"));
                    context.setVariable("valuationDate", getTaskValue(task,"valuation_date"));
                    context.setVariable("marketValue", getTaskValue(task,"estimated_market_value"));
                    context.setVariable("forcedSaleValue", getTaskValue(task,"forced_sale_value"));

                    // ===============================
                    // 👥 GUARANTOR
                    // ===============================
                    context.setVariable("guarantorCustomerId", getTaskValue(task,"guarantor_customer_id"));
                    context.setVariable("guarantorName", getTaskValue(task,"guarantor_full_name"));
                    context.setVariable("guarantorNationalId", getTaskValue(task,"guarantor_national_id"));
                    context.setVariable("guarantorAddress", getTaskValue(task,"guarantor_address"));
                    context.setVariable("guarantorMobile", getTaskValue(task,"guarantor_mobile"));
                    context.setVariable("guarantorEmail", getTaskValue(task,"guarantor_email"));
                    context.setVariable("guarantorStatus", getTaskValue(task,"guarantor_status"));

                    context.setVariable("employer", getTaskValue(task,"employer_name"));
                    context.setVariable("grossIncome", getTaskValue(task,"guarantor_gross_income"));
                    context.setVariable("netIncome", getTaskValue(task,"guarantor_net_income"));
                    context.setVariable("obligations", getTaskValue(task,"guarantor_obligations"));
                    context.setVariable("totalSalePrice", getTaskValue(task,"total_sale_price"));

                    // ===============================
                    // TASK INFO
                    // ===============================
                    context.setVariable("taskName", task.getName());
                    context.setVariable("taskDefinitionKey", task.getTaskDefinitionKey());
                    context.setVariable("taskId", task.getId());
                    context.setVariable("eventName", task.getEventName());
                    context.setVariable("processInstanceId", task.getProcessInstanceId());
                    context.setVariable("userName",
                            user.getFirstName() != null ? user.getFirstName() : user.getId());

                    context.setVariable(
                            "viewLink",
                            "http://localhost:5173/" + task.getTaskDefinitionKey() + "/task/" + task.getId()
                    );

                    // ===============================
                    // 👥 GUARANTORS LIST
                    // ===============================
                    String processInstanceId = task.getProcessInstanceId();

                    List<GuarantorResponseDto> guarantors =
                            guarantorService.getGuarantorsByProcessInstanceId(processInstanceId);

                    context.setVariable("guarantors", guarantors);

                    log.info("Task Guarantors : {}", guarantors);

                    String subject = "New Task Assigned: " + task.getName();

                    mailNotificationUtil.sendEmail(
                            email,
                            subject,
                            "email/task-created-notification",
                            context
                    );

                    log.info("Email sent to {}", email);
                }
            }
        }
    }


    public void sendTaskClaimedNotification(DelegateTask task) {

        try {
            String eventName = task.getEventName(); // claim
            log.info("Task Claimed Event: {}", eventName);

            Set<IdentityLink> identityLinks = task.getCandidates();

            String assignee = task.getAssignee();

            if (assignee == null) {
                log.info("Task unclaimed - skipping email");
                return;
            }
            String claimedUserId = assignee;

            log.info("claimedUserId: {}", claimedUserId);

            String claimedUserName = "";

            if (claimedUserId != null) {
                User claimedUser = identityService
                        .createUserQuery()
                        .userId(claimedUserId)
                        .singleResult();

                claimedUserName = (claimedUser != null && claimedUser.getId() != null)
                        ? claimedUser.getFirstName()
                        : claimedUserId;
            }


            for (IdentityLink identityLink : identityLinks) {

                if ("candidate".equals(identityLink.getType())) {

                    String groupId = identityLink.getGroupId();
                    log.info("Group Id: {}", groupId);

                    List<User> users = identityService
                            .createUserQuery()
                            .memberOfGroup(groupId)
                            .list();

                    for (User user : users) {

                        String email = user.getEmail();

                        if (email == null || email.isEmpty()) {
                            log.warn("User {} has no email", user.getId());
                            continue;
                        }

                        Context context = new Context();

                        // ===============================
                        // 👤 CLAIM INFO (IMPORTANT)
                        // ===============================
                        context.setVariable("claimedBy", claimedUserName);
                        context.setVariable("groupName", groupId);

                        // ===============================
                        // 👤 CUSTOMER
                        // ===============================
                        context.setVariable("fullName", getTaskValue(task, "full_name"));
                        context.setVariable("email", getTaskValue(task, "email_id"));
                        context.setVariable("cifNumber", getTaskValue(task, "cif_number"));
                        context.setVariable("mobile", getTaskValue(task, "mobile_number"));
                        context.setVariable("loanAmount", getTaskValue(task, "cost_price"));

                        // ===============================
                        // 💰 PRODUCT
                        // ===============================
                        context.setVariable("productType", getTaskValue(task, "product_type"));
                        context.setVariable("productName", getTaskValue(task, "product_name"));
                        context.setVariable("assetDescription", getTaskValue(task, "asset_description"));
                        context.setVariable("tenure", getTaskValue(task, "financing_tenor"));
                        context.setVariable("paymentStructure", getTaskValue(task, "payment_structure"));
                        context.setVariable("financingAmount", getTaskValue(task, "cost_price"));
                        context.setVariable("costPrice", getTaskValue(task, "cost_price"));
                        context.setVariable("profitRate", getTaskValue(task, "profit_rate"));
                        context.setVariable("profitAmount", getTaskValue(task, "profit_amount"));
                        context.setVariable("monthlyInstallment", getTaskValue(task, "monthly_installment"));
                        context.setVariable("Sector", getTaskValue(task, "sector_name"));
                        context.setVariable("SubSector", getTaskValue(task, "sub_sector"));
                        context.setVariable("Category", getTaskValue(task, "category"));
                        context.setVariable("loan_purpose_description", getTaskValue(task, "loan_purpose_description"));
                        context.setVariable("existing_monthly_obligations", getTaskValue(task, "existing_monthly_obligations"));


                        // ===============================
                        // 🏢 BUSINESS
                        // ===============================
                        context.setVariable("customer_category", getTaskValue(task, "customer_category"));
                        context.setVariable("business_sector", getTaskValue(task, "business_sector"));
                        context.setVariable("monthly_net_income", getTaskValue(task, "monthly_net_income"));
                        context.setVariable("monthly_business_revenue", getTaskValue(task, "monthly_business_revenue"));
                        context.setVariable("annual_turnover", getTaskValue(task, "annual_turnover"));
                        context.setVariable("yearsOfBusiness", getTaskValue(task, "years_of_Business"));
                        context.setVariable("years_of_Business", getTaskValue(task, "years_of_Business"));
                        context.setVariable("existingFacilities", getTaskValue(task, "number_of_existing_facilities"));
                        context.setVariable("proposedInstallment", getTaskValue(task, "proposed_instalment"));
                        context.setVariable("afterFacility", getTaskValue(task, "after_this_facility"));

                        // ===============================
                        // 🔐 SECURITY
                        // ===============================
                        context.setVariable("collateralType", getTaskValue(task, "security_type"));
                        context.setVariable("securityDescription", getTaskValue(task, "security_description"));
                        context.setVariable("securityOwnership", getTaskValue(task, "security_ownership"));

                        // ===============================
                        // 🏦 BANK
                        // ===============================
                        context.setVariable("bankHolderName", getTaskValue(task, "bank_holder_name"));
                        context.setVariable("accountNumber", getTaskValue(task, "account_number"));
                        context.setVariable("accountType", getTaskValue(task, "account_type"));

                        // ===============================
                        // 🚗 / 🏠 ASSET DETAILS
                        // ===============================
                        context.setVariable("vehicleRegistration", getTaskValue(task, "vehicle_registration"));
                        context.setVariable("chassisNumber", getTaskValue(task, "chassis_number"));
                        context.setVariable("titleDeedNumber", getTaskValue(task, "title_deed_number"));
                        context.setVariable("propertyLocation", getTaskValue(task, "property_location"));

                        context.setVariable("valuerName", getTaskValue(task, "valuer_name"));
                        context.setVariable("valuationDate", getTaskValue(task, "valuation_date"));
                        context.setVariable("marketValue", getTaskValue(task, "estimated_market_value"));
                        context.setVariable("forcedSaleValue", getTaskValue(task, "forced_sale_value"));

                        // ===============================
                        // 👥 GUARANTOR
                        // ===============================
                        context.setVariable("guarantorCustomerId", getTaskValue(task, "guarantor_customer_id"));
                        context.setVariable("guarantorName", getTaskValue(task, "guarantor_full_name"));
                        context.setVariable("guarantorNationalId", getTaskValue(task, "guarantor_national_id"));
                        context.setVariable("guarantorAddress", getTaskValue(task, "guarantor_address"));
                        context.setVariable("guarantorMobile", getTaskValue(task, "guarantor_mobile"));
                        context.setVariable("guarantorEmail", getTaskValue(task, "guarantor_email"));
                        context.setVariable("guarantorStatus", getTaskValue(task, "guarantor_status"));

                        context.setVariable("employer", getTaskValue(task, "employer_name"));
                        context.setVariable("grossIncome", getTaskValue(task, "guarantor_gross_income"));
                        context.setVariable("netIncome", getTaskValue(task, "guarantor_net_income"));
                        context.setVariable("obligations", getTaskValue(task, "guarantor_obligations"));
                        context.setVariable("totalSalePrice", getTaskValue(task, "total_sale_price"));


                        // ===============================
                        // TASK INFO
                        // ===============================
                        context.setVariable("taskName", task.getName());
                        context.setVariable("taskDefinitionKey", task.getTaskDefinitionKey());
                        context.setVariable("taskId", task.getId());
                        context.setVariable("processInstanceId", task.getProcessInstanceId());
                        context.setVariable("eventName", eventName);
                        context.setVariable("userName",
                                user.getFirstName() != null ? user.getFirstName() : user.getId());

                        // ===============================
                        // 👥 GUARANTORS LIST
                        // ===============================
                        String processInstanceId = task.getProcessInstanceId();

                        List<GuarantorResponseDto> guarantors =
                                guarantorService.getGuarantorsByProcessInstanceId(processInstanceId);

                        context.setVariable("guarantors", guarantors);

                        log.info("Task Guarantors : {}", guarantors);

                        String subject = "Task Claimed by " + claimedUserName + ": " + task.getName();

                        context.setVariable(
                                "viewLink",
                                "http://localhost:5173/" + task.getTaskDefinitionKey() + "/task/" + task.getId()
                        );

                        mailNotificationUtil.sendEmail(
                                email,
                                subject,
                                "email/task-claimed-notification",
                                context
                        );

                        log.info("Claim notification sent to {}", email);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occured while sending claim task notification : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void sendTaskCompletedNotification(DelegateTask task) {

        String eventName = task.getEventName();
        log.info("Task Completed Event: {}", eventName);
        String taskKey = task.getTaskDefinitionKey();
        String requester = getTaskValue(task, "requester");
        String taskAction = getTaskValue(task, taskKey + "_action");
        log.info("Task Action is: {}", taskAction);
        String taskActionBy = getTaskValue(task, taskKey + "_action_by");
        log.info("Task Action BY is: {}", taskActionBy);
        String taskActionReason = getTaskValue(task, taskKey + "_action_reason");
        log.info("Task Action Reason is: {}", taskActionReason);
        String customerEmailId = getTaskValue(task, "email_id");
        log.info("Customer Email Id is: {}", customerEmailId);


        User requesterUser = identityService
                .createUserQuery()
                .userId(requester)
                .singleResult();
        String requesterEmailId = null;
        if (requesterUser != null && requesterUser.getEmail() != null) {
            requesterEmailId = requesterUser.getEmail();
        }
        log.info("Requester Email Id is: {}", requesterEmailId);


        Set<IdentityLink> identityLinks = task.getCandidates();

        String completedUserId = task.getAssignee();

        if (completedUserId == null) {
            log.warn("Task completed without assignee");
            completedUserId = "SYSTEM";
        }

        log.info("completedUserId: {}", completedUserId);

        User completedUser = identityService
                .createUserQuery()
                .userId(completedUserId)
                .singleResult();

        String completedBy = (completedUser != null && completedUser.getFirstName() != null)
                ? completedUser.getFirstName()
                : completedUserId;

        Context context = new Context();

        // ===============================
        // 👤 COMPLETION INFO
        // ===============================
        context.setVariable("completedBy", completedBy);

        // ===============================
        // 👤 CUSTOMER
        // ===============================

        context.setVariable("fullName", getTaskValue(task, "full_name"));
        context.setVariable("cifNumber", getTaskValue(task, "cif_number"));
        context.setVariable("mobile", getTaskValue(task, "mobile_number"));
        context.setVariable("loanAmount", getTaskValue(task, "cost_price"));

        // ===============================
        // 💰 PRODUCT
        // ===============================
        context.setVariable("productType", getTaskValue(task, "product_type"));
        context.setVariable("productName", getTaskValue(task, "product_name"));
        context.setVariable("assetDescription", getTaskValue(task, "asset_description"));
        context.setVariable("tenure", getTaskValue(task, "financing_tenor"));
        context.setVariable("paymentStructure", getTaskValue(task, "payment_structure"));
        context.setVariable("financingAmount", getTaskValue(task, "cost_price"));
        context.setVariable("costPrice", getTaskValue(task, "cost_price"));
        context.setVariable("profitRate", getTaskValue(task, "profit_rate"));
        context.setVariable("profitAmount", getTaskValue(task, "profit_amount"));
        context.setVariable("monthlyInstallment", getTaskValue(task, "monthly_installment"));
        context.setVariable("Sector", getTaskValue(task, "sector_name"));
        context.setVariable("SubSector", getTaskValue(task, "sub_sector"));
        context.setVariable("Category", getTaskValue(task, "category"));
        context.setVariable("loan_purpose_description", getTaskValue(task, "loan_purpose_description"));
        context.setVariable("existing_monthly_obligations", getTaskValue(task, "existing_monthly_obligations"));


        // ===============================
        // 🏢 BUSINESS
        // ===============================
        context.setVariable("customer_category", getTaskValue(task, "customer_category"));
        context.setVariable("business_sector", getTaskValue(task, "business_sector"));
        context.setVariable("monthly_net_income", getTaskValue(task, "monthly_net_income"));
        context.setVariable("monthly_business_revenue", getTaskValue(task, "monthly_business_revenue"));
        context.setVariable("annual_turnover", getTaskValue(task, "annual_turnover"));
        context.setVariable("yearsOfBusiness", getTaskValue(task, "years_of_Business"));
        context.setVariable("years_of_Business", getTaskValue(task, "years_of_Business"));
        context.setVariable("existingFacilities", getTaskValue(task, "number_of_existing_facilities"));
        context.setVariable("proposedInstallment", getTaskValue(task, "proposed_instalment"));
        context.setVariable("afterFacility", getTaskValue(task, "after_this_facility"));

        // ===============================
        // 🔐 SECURITY
        // ===============================
        context.setVariable("collateralType", getTaskValue(task, "security_type"));
        context.setVariable("securityDescription", getTaskValue(task, "security_description"));
        context.setVariable("securityOwnership", getTaskValue(task, "security_ownership"));

        // ===============================
        // 🏦 BANK
        // ===============================
        context.setVariable("bankHolderName", getTaskValue(task, "bank_holder_name"));
        context.setVariable("accountNumber", getTaskValue(task, "account_number"));
        context.setVariable("accountType", getTaskValue(task, "account_type"));

        // ===============================
        // 🚗 / 🏠 ASSET DETAILS
        // ===============================
        context.setVariable("vehicleRegistration", getTaskValue(task, "vehicle_registration"));
        context.setVariable("chassisNumber", getTaskValue(task, "chassis_number"));
        context.setVariable("titleDeedNumber", getTaskValue(task, "title_deed_number"));
        context.setVariable("propertyLocation", getTaskValue(task, "property_location"));

        context.setVariable("valuerName", getTaskValue(task, "valuer_name"));
        context.setVariable("valuationDate", getTaskValue(task, "valuation_date"));
        context.setVariable("marketValue", getTaskValue(task, "estimated_market_value"));
        context.setVariable("forcedSaleValue", getTaskValue(task, "forced_sale_value"));

        // ===============================
        // 👥 GUARANTOR
        // ===============================
        context.setVariable("guarantorCustomerId", getTaskValue(task, "guarantor_customer_id"));
        context.setVariable("guarantorName", getTaskValue(task, "guarantor_full_name"));
        context.setVariable("guarantorNationalId", getTaskValue(task, "guarantor_national_id"));
        context.setVariable("guarantorAddress", getTaskValue(task, "guarantor_address"));
        context.setVariable("guarantorMobile", getTaskValue(task, "guarantor_mobile"));
        context.setVariable("guarantorEmail", getTaskValue(task, "guarantor_email"));
        context.setVariable("guarantorStatus", getTaskValue(task, "guarantor_status"));

        context.setVariable("employer", getTaskValue(task, "employer_name"));
        context.setVariable("grossIncome", getTaskValue(task, "guarantor_gross_income"));
        context.setVariable("netIncome", getTaskValue(task, "guarantor_net_income"));
        context.setVariable("obligations", getTaskValue(task, "guarantor_obligations"));
        context.setVariable("totalSalePrice", getTaskValue(task, "total_sale_price"));

        context.setVariable("taskAction", getTaskValue(task, taskKey + "_action"));
        context.setVariable("taskActionBy", getTaskValue(task, taskKey + "_action_by"));
        context.setVariable("taskActionReason", getTaskValue(task, taskKey + "_action_reason"));
        context.setVariable("customerEmailId", getTaskValue(task, "email_id"));
        context.setVariable("requesterEmailId", requesterEmailId);


        // ===============================
        // TASK INFO
        // ===============================
        context.setVariable("taskName", task.getName());
        context.setVariable("taskId", task.getId());
        context.setVariable("processInstanceId", task.getProcessInstanceId());
        context.setVariable("eventName", eventName);
        context.setVariable("taskDefinitionKey", task.getTaskDefinitionKey());
        context.setVariable(
                "viewLink",
                "http://localhost:5173/" + task.getTaskDefinitionKey() + "/task/" + task.getId()
        );

        // ===============================
        // 👥 GUARANTORS LIST
        // ===============================
        String processInstanceId = task.getProcessInstanceId();

        List<GuarantorResponseDto> guarantors =
                guarantorService.getGuarantorsByProcessInstanceId(processInstanceId);

        context.setVariable("guarantors", guarantors);

        log.info("Task Guarantors : {}", guarantors);

        String subject = "Task " + taskAction + " - " + task.getName();


        for (IdentityLink identityLink : identityLinks) {

            if ("candidate".equals(identityLink.getType())) {

                String groupId = identityLink.getGroupId();
                log.info("Group: {}", groupId);

                List<User> users = identityService
                        .createUserQuery()
                        .memberOfGroup(groupId)
                        .list();

                for (User user : users) {

                    String email = user.getEmail();

                    if (email == null || email.isEmpty()) {
                        log.warn("User {} has no email", user.getId());
                        continue;
                    }


                    mailNotificationUtil.sendEmail(
                            email,
                            subject,
                            "email/task-completed-notification",
                            context
                    );

                    log.info("Completion notification sent to {}", email);


                }
            }
        }

        // ===============================
        // 📧 SEND TO CUSTOMER
        // ===============================
        if (customerEmailId != null && !customerEmailId.isEmpty()) {

            mailNotificationUtil.sendEmail(
                    customerEmailId,
                    subject,
                    "email/task-completed-notification",
                    context
            );

            log.info("Completion notification sent to CUSTOMER {}", customerEmailId);
        }

        // ===============================
        // 📧 SEND TO REQUESTER
        // ===============================
        if (requesterEmailId != null && !requesterEmailId.isEmpty()) {


            mailNotificationUtil.sendEmail(
                    requesterEmailId,
                    subject,
                    "email/task-completed-notification",
                    context
            );

            log.info("Completion notification sent to REQUESTER {}", requesterEmailId);
            log.info("REQUESTER Name is {}", requester);

        }
    }

    private String getTaskValue(DelegateTask task, String key) {
        return task.getVariable(key) == null ? "" : task.getVariable(key).toString();
    }
}