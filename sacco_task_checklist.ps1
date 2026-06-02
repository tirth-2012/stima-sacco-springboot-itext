# ================================
# CONFIG
# ================================
$baseUrl = "http://localhost/flowable-service"
$authUrl = "$baseUrl/users/authenticate"
$checklistUrl = "$baseUrl/api/checklists"

$username = "sacca.admin"
$password = "DIB@2026"

$sleepTime = 300

# ================================
# AUTHENTICATION
# ================================
try {

    Write-Host "Authenticating..."

    $authBody = @{
        userId   = $username
        password = $password
    } | ConvertTo-Json

    $authResponse = Invoke-RestMethod -Method POST `
        -Uri $authUrl `
        -Headers @{ "Content-Type" = "application/json" } `
        -Body $authBody

    $token = $authResponse.token

    if (-not $token) {
        throw "Token not received!"
    }

    Write-Host "Token received."
}
catch {

    Write-Host "Authentication failed:"
    Write-Host $_

    exit
}

$headers = @{
    "Content-Type"  = "application/json"
    "Authorization" = "Bearer $token"
}

# ================================
# RAW DATA
# ================================
$rawData = @"
1	"ELIGIBILITY CHECKLIST"	1	1	"Shares checked"	true	"Development Loan"	""	"usertask_eligibility_check"	"dev_eligibility_shares_checked"
2	"ELIGIBILITY CHECKLIST"	1	2	"Deposits checked"	true	"Development Loan"	""	"usertask_eligibility_check"	"dev_eligibility_deposits_checked"
3	"ELIGIBILITY CHECKLIST"	1	3	"Existing loan exposure checked"	true	"Development Loan"	""	"usertask_eligibility_check"	"dev_eligibility_exposure_checked"
4	"ELIGIBILITY CHECKLIST"	1	4	"Salary affordability calculated"	true	"Development Loan"	""	"usertask_eligibility_check"	"dev_eligibility_salary_affordability"
5	"ELIGIBILITY CHECKLIST"	1	5	"Credit score generated"	true	"Development Loan"	""	"usertask_eligibility_check"	"dev_eligibility_credit_score"
6	"ELIGIBILITY CHECKLIST"	1	6	"CRB status checked"	true	"Development Loan"	""	"usertask_eligibility_check"	"dev_eligibility_crb_status"
7	"ELIGIBILITY CHECKLIST"	1	7	"Eligible amount calculated"	true	"Development Loan"	""	"usertask_eligibility_check"	"dev_eligibility_amount_calculated"
8	"ELIGIBILITY CHECKLIST"	1	8	"Requested amount within policy"	true	"Development Loan"	""	"usertask_eligibility_check"	"dev_eligibility_policy_check"

9	"GUARANTOR CHECKLIST"	2	1	"Required guarantor count met"	true	"Development Loan"	""	"usertask_guarantor_verification"	"dev_guarantor_count_met"
10	"GUARANTOR CHECKLIST"	2	2	"Guarantor capacities validated"	true	"Development Loan"	""	"usertask_guarantor_verification"	"dev_guarantor_capacity_validated"
11	"GUARANTOR CHECKLIST"	2	3	"Guarantee amount fully allocated"	true	"Development Loan"	""	"usertask_guarantor_verification"	"dev_guarantor_amount_allocated"
12	"GUARANTOR CHECKLIST"	2	4	"Guarantor requests sent"	true	"Development Loan"	""	"usertask_guarantor_verification"	"dev_guarantor_requests_sent"
13	"GUARANTOR CHECKLIST"	2	5	"All guarantors responded"	true	"Development Loan"	""	"usertask_guarantor_verification"	"dev_guarantor_responded"
14	"GUARANTOR CHECKLIST"	2	6	"Minimum guarantee coverage achieved"	true	"Development Loan"	""	"usertask_guarantor_verification"	"dev_guarantor_coverage_achieved"

15	"CREDIT REVIEW CHECKLIST"	3	1	"Application reviewed"	true	"Development Loan"	""	"usertask_conventional_risk_credit_analyst"	"dev_credit_application_reviewed"
16	"CREDIT REVIEW CHECKLIST"	3	2	"Eligibility reviewed"	true	"Development Loan"	""	"usertask_conventional_risk_credit_analyst"	"dev_credit_eligibility_reviewed"
17	"CREDIT REVIEW CHECKLIST"	3	3	"Score reviewed"	true	"Development Loan"	""	"usertask_conventional_risk_credit_analyst"	"dev_credit_score_reviewed"
18	"CREDIT REVIEW CHECKLIST"	3	4	"Guarantors reviewed"	true	"Development Loan"	""	"usertask_conventional_risk_credit_analyst"	"dev_credit_guarantors_reviewed"
19	"CREDIT REVIEW CHECKLIST"	3	5	"Collateral reviewed"	true	"Development Loan"	""	"usertask_conventional_risk_credit_analyst"	"dev_credit_collateral_reviewed"
20	"CREDIT REVIEW CHECKLIST"	3	6	"Documents verified"	true	"Development Loan"	""	"usertask_conventional_risk_credit_analyst"	"dev_credit_documents_verified"
21	"CREDIT REVIEW CHECKLIST"	3	7	"Analyst comments captured"	true	"Development Loan"	""	"usertask_conventional_risk_credit_analyst"	"dev_credit_comments_captured"
22	"CREDIT REVIEW CHECKLIST"	3	8	"Recommendation selected"	true	"Development Loan"	""	"usertask_conventional_risk_credit_analyst"	"dev_credit_recommendation_selected"

23	"APPROVAL CHECKLIST"	4	1	"Approval authority confirmed"	true	"Development Loan"	""	"usertask_conventional_credit_officer"	"dev_approval_authority_confirmed"
24	"APPROVAL CHECKLIST"	4	2	"Exposure within limit"	true	"Development Loan"	""	"usertask_conventional_credit_officer"	"dev_approval_exposure_limit"
25	"APPROVAL CHECKLIST"	4	3	"Conditions reviewed"	true	"Development Loan"	""	"usertask_conventional_credit_officer"	"dev_approval_conditions_reviewed"
26	"APPROVAL CHECKLIST"	4	4	"Credit recommendation reviewed"	true	"Development Loan"	""	"usertask_conventional_credit_officer"	"dev_approval_credit_reviewed"
27	"APPROVAL CHECKLIST"	4	5	"Exceptions acknowledged"	true	"Development Loan"	""	"usertask_conventional_credit_officer"	"dev_approval_exceptions_acknowledged"
28	"APPROVAL CHECKLIST"	4	6	"Decision captured"	true	"Development Loan"	""	"usertask_conventional_credit_officer"	"dev_approval_decision_captured"

29	"APPROVAL CHECKLIST"	4	1	"Approval authority confirmed"	true	"Development Loan"	""	"usertask_conventional_senior_credit_manager"	"dev_senior_approval_authority_confirmed"
30	"APPROVAL CHECKLIST"	4	2	"Exposure within limit"	true	"Development Loan"	""	"usertask_conventional_senior_credit_manager"	"dev_senior_approval_exposure_limit"
31	"APPROVAL CHECKLIST"	4	3	"Conditions reviewed"	true	"Development Loan"	""	"usertask_conventional_senior_credit_manager"	"dev_senior_approval_conditions_reviewed"
32	"APPROVAL CHECKLIST"	4	4	"Credit recommendation reviewed"	true	"Development Loan"	""	"usertask_conventional_senior_credit_manager"	"dev_senior_approval_credit_reviewed"
33	"APPROVAL CHECKLIST"	4	5	"Exceptions acknowledged"	true	"Development Loan"	""	"usertask_conventional_senior_credit_manager"	"dev_senior_approval_exceptions_acknowledged"
34	"APPROVAL CHECKLIST"	4	6	"Decision captured"	true	"Development Loan"	""	"usertask_conventional_senior_credit_manager"	"dev_senior_approval_decision_captured"

35	"DISBURSEMENT CHECKLIST"	5	1	"Approval completed"	true	"Development Loan"	""	"usertask_conventional_disbursement"	"dev_disbursement_approval_completed"
36	"DISBURSEMENT CHECKLIST"	5	2	"Conditions satisfied"	true	"Development Loan"	""	"usertask_conventional_disbursement"	"dev_disbursement_conditions_satisfied"
37	"DISBURSEMENT CHECKLIST"	5	3	"Member account validated"	true	"Development Loan"	""	"usertask_conventional_disbursement"	"dev_disbursement_member_account"
38	"DISBURSEMENT CHECKLIST"	5	4	"CBS product mapped"	true	"Development Loan"	""	"usertask_conventional_disbursement"	"dev_disbursement_cbs_product"
39	"DISBURSEMENT CHECKLIST"	5	5	"Repayment schedule generated"	true	"Development Loan"	""	"usertask_conventional_disbursement"	"dev_disbursement_schedule_generated"
40	"DISBURSEMENT CHECKLIST"	5	6	"Disbursement account validated"	true	"Development Loan"	""	"usertask_conventional_disbursement"	"dev_disbursement_account_validated"
41	"DISBURSEMENT CHECKLIST"	5	7	"CBS posting successful"	true	"Development Loan"	""	"usertask_conventional_disbursement"	"dev_disbursement_posting_success"

42	"LEGAL VERIFICATION CHECKLIST"	6	1	"National ID verified"	true	"Development Loan"	""	"usertask_conventional_legal_verification"	"dev_legal_national_id"
43	"LEGAL VERIFICATION CHECKLIST"	6	2	"Payslip verified"	true	"Development Loan"	""	"usertask_conventional_legal_verification"	"dev_legal_payslip"
44	"LEGAL VERIFICATION CHECKLIST"	6	3	"Bank statement verified"	true	"Development Loan"	""	"usertask_conventional_legal_verification"	"dev_legal_bank_statement"
45	"LEGAL VERIFICATION CHECKLIST"	6	4	"Employer introduction letter verified"	true	"Development Loan"	""	"usertask_conventional_legal_verification"	"dev_legal_employer_letter"

46	"POST DISBURSEMENT CHECKLIST"	7	1	"Loan booked"	true	"Development Loan"	""	"usertask_conventional_post_disbursement"	"dev_post_loan_booked"
47	"POST DISBURSEMENT CHECKLIST"	7	2	"Repayment schedule active"	true	"Development Loan"	""	"usertask_conventional_post_disbursement"	"dev_post_schedule_active"
48	"POST DISBURSEMENT CHECKLIST"	7	3	"Member notified"	true	"Development Loan"	""	"usertask_conventional_post_disbursement"	"dev_post_member_notified"
49	"POST DISBURSEMENT CHECKLIST"	7	4	"Guarantors notified"	true	"Development Loan"	""	"usertask_conventional_post_disbursement"	"dev_post_guarantor_notified"
50	"POST DISBURSEMENT CHECKLIST"	7	5	"Documents archived"	true	"Development Loan"	""	"usertask_conventional_post_disbursement"	"dev_post_documents_archived"
51	"POST DISBURSEMENT CHECKLIST"	7	6	"Audit trail completed"	true	"Development Loan"	""	"usertask_conventional_post_disbursement"	"dev_post_audit_completed"

52	"ELIGIBILITY CHECKLIST"	1	1	"Shares checked"	true	"Emergency Loan"	""	"usertask_eligibility_check"	"emg_eligibility_shares_checked"
53	"ELIGIBILITY CHECKLIST"	1	2	"Deposits checked"	true	"Emergency Loan"	""	"usertask_eligibility_check"	"emg_eligibility_deposits_checked"
54	"ELIGIBILITY CHECKLIST"	1	3	"Existing loan exposure checked"	true	"Emergency Loan"	""	"usertask_eligibility_check"	"emg_eligibility_exposure_checked"
55	"ELIGIBILITY CHECKLIST"	1	4	"Salary affordability calculated"	true	"Emergency Loan"	""	"usertask_eligibility_check"	"emg_eligibility_salary_affordability"
56	"ELIGIBILITY CHECKLIST"	1	5	"Credit score generated"	true	"Emergency Loan"	""	"usertask_eligibility_check"	"emg_eligibility_credit_score"
57	"ELIGIBILITY CHECKLIST"	1	6	"CRB status checked"	true	"Emergency Loan"	""	"usertask_eligibility_check"	"emg_eligibility_crb_status"
58	"ELIGIBILITY CHECKLIST"	1	7	"Eligible amount calculated"	true	"Emergency Loan"	""	"usertask_eligibility_check"	"emg_eligibility_amount_calculated"
59	"ELIGIBILITY CHECKLIST"	1	8	"Requested amount within policy"	true	"Emergency Loan"	""	"usertask_eligibility_check"	"emg_eligibility_policy_check"

60	"GUARANTOR CHECKLIST"	2	1	"Required guarantor count met"	true	"Emergency Loan"	""	"usertask_guarantor_verification"	"emg_guarantor_count_met"
61	"GUARANTOR CHECKLIST"	2	2	"Guarantor capacities validated"	true	"Emergency Loan"	""	"usertask_guarantor_verification"	"emg_guarantor_capacity_validated"
62	"GUARANTOR CHECKLIST"	2	3	"Guarantee amount fully allocated"	true	"Emergency Loan"	""	"usertask_guarantor_verification"	"emg_guarantor_amount_allocated"
63	"GUARANTOR CHECKLIST"	2	4	"Guarantor requests sent"	true	"Emergency Loan"	""	"usertask_guarantor_verification"	"emg_guarantor_requests_sent"
64	"GUARANTOR CHECKLIST"	2	5	"All guarantors responded"	true	"Emergency Loan"	""	"usertask_guarantor_verification"	"emg_guarantor_responded"
65	"GUARANTOR CHECKLIST"	2	6	"Minimum guarantee coverage achieved"	true	"Emergency Loan"	""	"usertask_guarantor_verification"	"emg_guarantor_coverage_achieved"

66	"CREDIT REVIEW CHECKLIST"	3	1	"Application reviewed"	true	"Emergency Loan"	""	"usertask_conventional_risk_credit_analyst"	"emg_credit_application_reviewed"
67	"CREDIT REVIEW CHECKLIST"	3	2	"Eligibility reviewed"	true	"Emergency Loan"	""	"usertask_conventional_risk_credit_analyst"	"emg_credit_eligibility_reviewed"
68	"CREDIT REVIEW CHECKLIST"	3	3	"Score reviewed"	true	"Emergency Loan"	""	"usertask_conventional_risk_credit_analyst"	"emg_credit_score_reviewed"
69	"CREDIT REVIEW CHECKLIST"	3	4	"Guarantors reviewed"	true	"Emergency Loan"	""	"usertask_conventional_risk_credit_analyst"	"emg_credit_guarantors_reviewed"
70	"CREDIT REVIEW CHECKLIST"	3	5	"Collateral reviewed"	true	"Emergency Loan"	""	"usertask_conventional_risk_credit_analyst"	"emg_credit_collateral_reviewed"
71	"CREDIT REVIEW CHECKLIST"	3	6	"Documents verified"	true	"Emergency Loan"	""	"usertask_conventional_risk_credit_analyst"	"emg_credit_documents_verified"
72	"CREDIT REVIEW CHECKLIST"	3	7	"Analyst comments captured"	true	"Emergency Loan"	""	"usertask_conventional_risk_credit_analyst"	"emg_credit_comments_captured"
73	"CREDIT REVIEW CHECKLIST"	3	8	"Recommendation selected"	true	"Emergency Loan"	""	"usertask_conventional_risk_credit_analyst"	"emg_credit_recommendation_selected"

74	"APPROVAL CHECKLIST"	4	1	"Approval authority confirmed"	true	"Emergency Loan"	""	"usertask_conventional_credit_officer"	"emg_approval_authority_confirmed"
75	"APPROVAL CHECKLIST"	4	2	"Exposure within limit"	true	"Emergency Loan"	""	"usertask_conventional_credit_officer"	"emg_approval_exposure_limit"
76	"APPROVAL CHECKLIST"	4	3	"Conditions reviewed"	true	"Emergency Loan"	""	"usertask_conventional_credit_officer"	"emg_approval_conditions_reviewed"
77	"APPROVAL CHECKLIST"	4	4	"Credit recommendation reviewed"	true	"Emergency Loan"	""	"usertask_conventional_credit_officer"	"emg_approval_credit_reviewed"
78	"APPROVAL CHECKLIST"	4	5	"Exceptions acknowledged"	true	"Emergency Loan"	""	"usertask_conventional_credit_officer"	"emg_approval_exceptions_acknowledged"
79	"APPROVAL CHECKLIST"	4	6	"Decision captured"	true	"Emergency Loan"	""	"usertask_conventional_credit_officer"	"emg_approval_decision_captured"

80	"APPROVAL CHECKLIST"	4	1	"Approval authority confirmed"	true	"Emergency Loan"	""	"usertask_conventional_senior_credit_manager"	"emg_senior_approval_authority_confirmed"
81	"APPROVAL CHECKLIST"	4	2	"Exposure within limit"	true	"Emergency Loan"	""	"usertask_conventional_senior_credit_manager"	"emg_senior_approval_exposure_limit"
82	"APPROVAL CHECKLIST"	4	3	"Conditions reviewed"	true	"Emergency Loan"	""	"usertask_conventional_senior_credit_manager"	"emg_senior_approval_conditions_reviewed"
83	"APPROVAL CHECKLIST"	4	4	"Credit recommendation reviewed"	true	"Emergency Loan"	""	"usertask_conventional_senior_credit_manager"	"emg_senior_approval_credit_reviewed"
84	"APPROVAL CHECKLIST"	4	5	"Exceptions acknowledged"	true	"Emergency Loan"	""	"usertask_conventional_senior_credit_manager"	"emg_senior_approval_exceptions_acknowledged"
85	"APPROVAL CHECKLIST"	4	6	"Decision captured"	true	"Emergency Loan"	""	"usertask_conventional_senior_credit_manager"	"emg_senior_approval_decision_captured"

86	"DISBURSEMENT CHECKLIST"	5	1	"Approval completed"	true	"Emergency Loan"	""	"usertask_conventional_disbursement"	"emg_disbursement_approval_completed"
87	"DISBURSEMENT CHECKLIST"	5	2	"Conditions satisfied"	true	"Emergency Loan"	""	"usertask_conventional_disbursement"	"emg_disbursement_conditions_satisfied"
88	"DISBURSEMENT CHECKLIST"	5	3	"Member account validated"	true	"Emergency Loan"	""	"usertask_conventional_disbursement"	"emg_disbursement_member_account"
89	"DISBURSEMENT CHECKLIST"	5	4	"CBS product mapped"	true	"Emergency Loan"	""	"usertask_conventional_disbursement"	"emg_disbursement_cbs_product"
90	"DISBURSEMENT CHECKLIST"	5	5	"Repayment schedule generated"	true	"Emergency Loan"	""	"usertask_conventional_disbursement"	"emg_disbursement_schedule_generated"
91	"DISBURSEMENT CHECKLIST"	5	6	"Disbursement account validated"	true	"Emergency Loan"	""	"usertask_conventional_disbursement"	"emg_disbursement_account_validated"
92	"DISBURSEMENT CHECKLIST"	5	7	"CBS posting successful"	true	"Emergency Loan"	""	"usertask_conventional_disbursement"	"emg_disbursement_posting_success"

93	"LEGAL VERIFICATION CHECKLIST"	6	1	"National ID verified"	true	"Emergency Loan"	""	"usertask_conventional_legal_verification"	"emg_legal_national_id"
94	"LEGAL VERIFICATION CHECKLIST"	6	2	"Payslip verified"	true	"Emergency Loan"	""	"usertask_conventional_legal_verification"	"emg_legal_payslip"
95	"LEGAL VERIFICATION CHECKLIST"	6	3	"Bank statement verified"	true	"Emergency Loan"	""	"usertask_conventional_legal_verification"	"emg_legal_bank_statement"
96	"LEGAL VERIFICATION CHECKLIST"	6	4	"Employer introduction letter verified"	true	"Emergency Loan"	""	"usertask_conventional_legal_verification"	"emg_legal_employer_letter"

97	"POST DISBURSEMENT CHECKLIST"	7	1	"Loan booked"	true	"Emergency Loan"	""	"usertask_conventional_post_disbursement"	"emg_post_loan_booked"
98	"POST DISBURSEMENT CHECKLIST"	7	2	"Repayment schedule active"	true	"Emergency Loan"	""	"usertask_conventional_post_disbursement"	"emg_post_schedule_active"
99	"POST DISBURSEMENT CHECKLIST"	7	3	"Member notified"	true	"Emergency Loan"	""	"usertask_conventional_post_disbursement"	"emg_post_member_notified"
100	"POST DISBURSEMENT CHECKLIST"	7	4	"Guarantors notified"	true	"Emergency Loan"	""	"usertask_conventional_post_disbursement"	"emg_post_guarantor_notified"
101	"POST DISBURSEMENT CHECKLIST"	7	5	"Documents archived"	true	"Emergency Loan"	""	"usertask_conventional_post_disbursement"	"emg_post_documents_archived"
102	"POST DISBURSEMENT CHECKLIST"	7	6	"Audit trail completed"	true	"Emergency Loan"	""	"usertask_conventional_post_disbursement"	"emg_post_audit_completed"
"@

# ================================
# PARSE + TRANSFORM + CREATE
# ================================
$lines = $rawData -split "`n"

# duplicate prevention
$uniqueMap = @{}

foreach ($line in $lines) {

    if ([string]::IsNullOrWhiteSpace($line)) {
        continue
    }

    try {

        $parts = $line -split "`t"

        $clean = $parts | ForEach-Object {
            $_.Trim('"')
        }

        # ----------------------------
        # ORIGINAL VALUES
        # ----------------------------
        $category = $clean[1]
        $categoryOrder = [int]$clean[2]
        $checklistOrder = [int]$clean[3]
        $checklistTitle = $clean[4]
        $isMandatory = [System.Convert]::ToBoolean($clean[5])

        $productName = $clean[6]
        $stage = $clean[8]
        $variable = $clean[9]

        # ----------------------------
        # PRODUCT NAME CHANGES
        # ----------------------------
        if ($productName -eq "Murabaha") {
            $productName = "Development Loan"
        }

        if ($productName -eq "Term Loan") {
            $productName = "Emergency Loan"
        }

        # ----------------------------
        # REMOVE SHARIAH STAGE
        # ----------------------------
        if ($stage -eq "usertask_shariah_officer") {

            Write-Host "Skipping shariah checklist: $variable"
            continue
        }

        # ----------------------------
        # STAGE MAPPING
        # ----------------------------
        if (
            $stage -eq "usertask_risk_credit_analyst" -or
            $stage -eq "usertask_conventional_risk_credit_analyst"
        ) {

            $stage = "usertask_conventional_risk_credit_analyst"
        }

        elseif (
            $stage -eq "usertask_legal_verification" -or
            $stage -eq "usertask_conventional_legal_verification"
        ) {

            $stage = "usertask_conventional_legal_verification"
        }

        elseif (
            $stage -eq "usertask_disbursement" -or
            $stage -eq "usertask_conventional_disbursement"
        ) {

            $stage = "usertask_conventional_disbursement"
        }

        # ----------------------------
        # DUPLICATE CHECK
        # ----------------------------
        $duplicateKey = "$productName|$stage|$variable"

        if ($uniqueMap.ContainsKey($duplicateKey)) {

            Write-Host "Skipping duplicate: $duplicateKey"
            continue
        }

        $uniqueMap[$duplicateKey] = $true

        # ----------------------------
        # FINAL BODY
        # ----------------------------
        $body = @{
            id = 0
            category = $category
            categoryOrder = $categoryOrder
            checklistOrder = $checklistOrder
            checklistTitle = $checklistTitle
            isMandatory = $isMandatory
            productName = $productName
            productType = $null
            stage = $stage
            variable = $variable
        } | ConvertTo-Json -Depth 5

        Write-Host "Creating checklist:"
        Write-Host "Product : $productName"
        Write-Host "Stage   : $stage"
        Write-Host "Variable: $variable"

        Invoke-RestMethod -Method POST `
            -Uri $checklistUrl `
            -Headers $headers `
            -Body $body

        Write-Host "Success"
    }
    catch {

        Write-Host "Failed line:"
        Write-Host $line

        Write-Host $_
    }

    Start-Sleep -Milliseconds $sleepTime
}

Write-Host "====================================="
Write-Host "ALL CHECKLISTS CREATED SUCCESSFULLY"
Write-Host "====================================="