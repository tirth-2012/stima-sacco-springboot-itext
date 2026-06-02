# ================================
# CONFIG
# ================================
$baseUrl = "https://dib-demo.nlsbanking.com/flowable-service"
$authUrl = "$baseUrl/users/authenticate"
$checklistUrl = "$baseUrl/api/checklists"

$username = "akipchoge"
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

    $authResponse = Invoke-RestMethod -Method POST -Uri $authUrl `
        -Headers @{ "Content-Type" = "application/json" } `
        -Body $authBody

    $token = $authResponse.token

    if (-not $token) {
        throw "Token not received!"
    }

    Write-Host "Token received."
}
catch {
    Write-Host "Auth failed:" $_
    exit
}

$headers = @{
    "Content-Type"  = "application/json"
    "Authorization" = "Bearer $token"
}

# ================================
# FULL RAW DATA (EXACT COPY)
# ================================
$rawData = @"
1	"Document Verification"	1	1	"Business registration confirmed valid and current"	true	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"business_registration"
2	"Document Verification"	1	2	"KRA PIN confirmed active on iTax portal"	true	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"kra_pin"
3	"Document Verification"	1	3	"Audited financials (FY2025) reviewed - signed by licensed auditor"	true	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"audited_financials"
4	"Document Verification"	1	4	"Equipment invoice (Wakala asset) verified - supplier, amount, and specification confirmed"	true	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"equipment_invoice"
5	"Credit & CRB Verification"	2	5	"CRB Metropol report reviewed - score and adverse status noted"	true	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"crb_report"
6	"Credit & CRB Verification"	2	6	"Internal DIB facilities confirmed from CBS - all DIB obligations entered in obligations table"	true	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"dib_facilities"
7	"Credit & CRB Verification"	2	7	"External obligations from bank statements reconciled against declared obligations - discrepancies noted"	true	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"external_obligations"
8	"Risk Assessment"	3	8	"DSR computed on net operating income (not gross revenue) and confirmed within policy or waiver documented"	true	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"dsr_check"
9	"Risk Assessment"	3	9	"Collateral coverage ratio verified - LR number, FSV, encumbrance status, and charge registration status confirmed"	true	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"collateral_coverage"
10	"Islamic / Wakala Specific"	4	10	"Wakala asset confirmed as tangible, halal, and clearly identified - consistent with Shariah requirements"	false	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"wakala_asset"
11	"Islamic / Wakala Specific"	4	11	"Shariah documentation (Wakala agreement template) confirmed available for legal perfection stage"	false	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"shariah_docs"
16	"Document Verification"	1	1	"Business registration validated for lease eligibility"	true	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"Murabaha_business_registration"
17	"Document Verification"	1	2	"KRA PIN verified and compliant with tax authority"	true	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"Murabaha_kra_pin"
18	"Document Verification"	1	3	"Audited financial statements reviewed for lease capacity"	true	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"Murabaha_audited_financials"
19	"Document Verification"	1	4	"Leased asset invoice validated - supplier and lease structure confirmed"	true	"Murabaha"	"Islamic Financing Products"	"usertask_risk_credit_analyst"	"Murabaha_asset_invoice"
20	"Document & Agreement Review"	1	1	"Offer letter verified field-by-field against BCC approval - all amounts, tenor, product name and conditions correct"	true	"Murabaha"	"Islamic Financing Products"	"usertask_legal_verification"	"Murabaha_offer_letter_check"
21	"Document & Agreement Review"	1	2	"Facility agreement (FA v1.1) reviewed - all RCA conditions in Clauses 18 & 19 confirmed"	true	"Murabaha"	"Islamic Financing Products"	"usertask_legal_verification"	"Murabaha_fa_review"
22	"Document & Agreement Review"	1	3	"Wakala Agency Agreement reviewed - bank principal / borrower agent structure confirmed Shariah-compliant"	true	"Murabaha"	"Islamic Financing Products"	"usertask_legal_verification"	"Murabaha_wakala_agreement"
23	"Document & Agreement Review"	1	4	"All three documents fully executed and uploaded to Mayan EDMS"	true	"Murabaha"	"Islamic Financing Products"	"usertask_legal_verification"	"Murabaha_documents_uploaded"
24	"Collateral & Security Perfection"	2	1	"LRA Form 3 official search received from Machakos County Lands Registry - confirmed clear"	true	"Murabaha"	"Islamic Financing Products"	"usertask_legal_verification"	"Murabaha_lra_form3"
25	"Collateral & Security Perfection"	2	2	"Section 79 LRA 2012 charge deed executed - both borrower and DIB authorised officer have signed"	true	"Murabaha"	"Islamic Financing Products"	"usertask_legal_verification"	"Murabaha_charge_deed"
26	"Collateral & Security Perfection"	2	3	"Stamp duty paid via KRA eCitizen portal - payment receipt filed in Mayan EDMS"	true	"Murabaha"	"Islamic Financing Products"	"usertask_legal_verification"	"Murabaha_stamp_duty"
27	"Collateral & Security Perfection"	2	4	"First charge registered at Machakos County Lands Registry - certificate of charge received and filed in DMS"	true	"Murabaha"	"Islamic Financing Products"	"usertask_legal_verification"	"Murabaha_charge_registration"
28	"Insurance"	3	1	"Wakala asset insurance (KES 60M min) - all 5 insurance requirements confirmed · original policy schedule filed"	true	"Murabaha"	"Islamic Financing Products"	"usertask_legal_verification"	"Murabaha_asset_insurance"
29	"Insurance"	3	2	"Land and structures insurance (Athi River site) - DIB named as first loss payee · original schedule filed"	true	"Murabaha"	"Islamic Financing Products"	"usertask_legal_verification"	"Murabaha_land_insurance"
30	"Wakala "	4	1	"Wakala Agency Agreement executed before any instruction to supplier (Alphaline Solutions Ltd) or asset delivery"	false	"Murabaha"	"Islamic Financing Products"	"usertask_legal_verification"	"Murabaha_wakala_execution"
31	"Wakala "	4	2	"Supplier payment instruction prepared for Finance stage - Alphaline bank details confirmed"	false	"Murabaha"	"Islamic Financing Products"	"usertask_legal_verification"	"Murabaha_supplier_payment"
32	"Wakala / Islamic-Specific"	4	1	"Wakala Agency Agreement executed before any instruction to supplier (Alphaline Solutions Ltd) or asset delivery"	false	"Murabaha"	"Islamic Financing Products"	"usertask_legal_verification"	"Murabaha_wakala_execution"
33	"Wakala / Islamic-Specific"	4	2	"Supplier payment instruction prepared for Finance stage - Alphaline bank details confirmed"	false	"Murabaha"	"Islamic Financing Products"	"usertask_legal_verification"	"Murabaha_supplier_payment"
34	"Shariah Compliance"	1	1	"Financing structure is Shariah-compliant - no Riba (interest) element"	true	"Murabaha"	"Islamic Financing Products"	"usertask_shariah_officer"	"Murabaha_shariah_riba_check"
35	"Shariah Compliance"	1	2	"Asset is halal and clearly defined in the application"	true	"Murabaha"	"Islamic Financing Products"	"usertask_shariah_officer"	"Murabaha_asset_halal"
36	"Shariah Compliance"	1	3	"Ownership confirmed before sale (Murabaha / Wakala requirement)"	true	"Murabaha"	"Islamic Financing Products"	"usertask_shariah_officer"	"Murabaha_ownership_check"
37	"Shariah Compliance"	1	4	"Cost price and profit disclosed separately - not combined as a single interest rate"	true	"Murabaha"	"Islamic Financing Products"	"usertask_shariah_officer"	"Murabaha_price_transparency"
38	"Shariah Compliance"	1	5	"No penalty interest clause present in the financing agreement"	true	"Murabaha"	"Islamic Financing Products"	"usertask_shariah_officer"	"Murabaha_no_penalty_interest"
39	"Shariah Compliance"	1	6	"Customer has been informed of their rights under the contract"	true	"Murabaha"	"Islamic Financing Products"	"usertask_shariah_officer"	"Murabaha_customer_rights"
40	"Shariah Compliance"	1	7	"All Shariah documentation is complete and attached to this application"	true	"Murabaha"	"Islamic Financing Products"	"usertask_shariah_officer"	"Murabaha_docs_complete"
41	"Conditions Precedent"	1	1	"Executed Murabaha Agreement"	true	"Murabaha"	"Islamic Financing Products"	"usertask_disbursement"	"disb_exec_murabaha"
42	"Conditions Precedent"	1	2	"Vehicle log books endorsed to DIB"	true	"Murabaha"	"Islamic Financing Products"	"usertask_disbursement"	"disb_logbooks_endorsed"
43	"Conditions Precedent"	1	3	"Vehicle charge instruments executed"	true	"Murabaha"	"Islamic Financing Products"	"usertask_disbursement"	"disb_vehicle_charge"
44	"Conditions Precedent"	1	4	"Comprehensive insurance - all 3 units"	true	"Murabaha"	"Islamic Financing Products"	"usertask_disbursement"	"disb_insurance"
45	"Conditions Precedent"	1	5	"Stamp duty payment receipt"	true	"Murabaha"	"Islamic Financing Products"	"usertask_disbursement"	"disb_stamp_duty"
46	"Conditions Precedent"	1	6	"Direct Debit Mandate executed"	true	"Murabaha"	"Islamic Financing Products"	"usertask_disbursement"	"disb_dd_mandate"
47	"Conditions Precedent"	1	7	"NTSA charge notation confirmed"	true	"Murabaha"	"Islamic Financing Products"	"usertask_disbursement"	"disb_ntsa_charge"
48	"Conditions Precedent"	1	8	"Final tax invoice from Isuzu Kenya"	true	"Murabaha"	"Islamic Financing Products"	"usertask_disbursement"	"disb_final_invoice"
49	"Document Validation"	1	1	"Company incorporation documents verified with registrar records"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_risk_credit_analyst"	"tl_company_docs"
50	"Document Validation"	1	2	"Tax identification and latest tax filings reviewed for compliance"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_risk_credit_analyst"	"tl_tax_compliance"
51	"Document Validation"	1	3	"Latest audited financial statements analyzed for profitability and stability"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_risk_credit_analyst"	"tl_financial_review"
52	"Document Validation"	1	4	"Loan utilization plan and supporting invoices validated"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_risk_credit_analyst"	"tl_utilization_plan"
53	"Credit Checks"	2	5	"Credit bureau report assessed for repayment history and score"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_risk_credit_analyst"	"tl_credit_report"
54	"Credit Checks"	2	6	"Existing loan exposures verified from internal system records"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_risk_credit_analyst"	"tl_internal_exposure"
55	"Credit Checks"	2	7	"Liabilities with external lenders reconciled with financial disclosures"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_risk_credit_analyst"	"tl_external_liabilities"
56	"Risk Analysis"	3	8	"Debt Service Coverage Ratio (DSCR) calculated and validated"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_risk_credit_analyst"	"tl_dscr"
57	"Risk Analysis"	3	9	"Collateral valuation checked with latest market value and legal status"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_risk_credit_analyst"	"tl_collateral_value"
58	"Loan Compliance"	4	10	"Loan agreement terms reviewed and approved as per policy guidelines"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_risk_credit_analyst"	"tl_agreement_check"
59	"Loan Compliance"	4	11	"All pre-disbursement conditions verified and documented"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_risk_credit_analyst"	"tl_pre_disbursement"
60	"Document & Agreement Review"	1	1	"Offer letter cross-checked with approved terms - loan amount, tenure, and conditions verified"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_legal_verification"	"tl_offer_letter_check"
61	"Document & Agreement Review"	1	2	"Facility agreement reviewed thoroughly - all approval conditions validated against policy clauses"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_legal_verification"	"tl_facility_agreement"
62	"Document & Agreement Review"	1	3	"Loan agreement structure validated - borrower obligations and bank rights clearly defined"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_legal_verification"	"tl_loan_structure"
63	"Document & Agreement Review"	1	4	"All required agreements signed and uploaded into document management system"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_legal_verification"	"tl_docs_uploaded"
64	"Collateral & Security Perfection"	2	5	"Latest land registry search obtained - property ownership and encumbrance verified"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_legal_verification"	"tl_land_search"
65	"Collateral & Security Perfection"	2	6	"Charge documents executed correctly - borrower and bank representatives have signed"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_legal_verification"	"tl_charge_execution"
66	"Collateral & Security Perfection"	2	7	"Stamp duty payment confirmed - receipt recorded in system"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_legal_verification"	"tl_stamp_duty"
67	"Collateral & Security Perfection"	2	8	"Security interest registered with authority - proof of registration verified and stored"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_legal_verification"	"tl_security_registration"
68	"Insurance Compliance"	3	9	"Financed asset insurance verified - coverage meets minimum policy requirements"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_legal_verification"	"tl_asset_insurance"
69	"Insurance Compliance"	3	10	"Property insurance confirmed - bank listed as primary beneficiary where applicable"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_legal_verification"	"tl_property_insurance"
70	"Disbursement Readiness"	4	11	"All legal conditions satisfied prior to disbursement approval"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_legal_verification"	"tl_legal_clearance"
71	"Disbursement Readiness"	4	12	"Final verification completed - case ready for disbursement processing"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_legal_verification"	"tl_ready_for_disbursement"
72	"Conditions Precedent"	1	1	"Loan agreement executed and signed by all parties"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_disbursement"	"tl_exec_loan_agreement"
73	"Conditions Precedent"	1	2	"Asset ownership documents endorsed in favor of the bank"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_disbursement"	"tl_asset_endorsement"
74	"Conditions Precedent"	1	3	"Security/charge documents executed and verified"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_disbursement"	"tl_security_execution"
75	"Conditions Precedent"	1	4	"Comprehensive insurance policy validated - coverage meets approval requirements"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_disbursement"	"tl_insurance_check"
76	"Conditions Precedent"	1	5	"Stamp duty payment confirmed and receipt documented"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_disbursement"	"tl_stamp_receipt"
77	"Conditions Precedent"	1	6	"Auto-debit or repayment mandate completed and authorized"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_disbursement"	"tl_dd_mandate"
78	"Conditions Precedent"	1	7	"Charge registration or lien notation confirmed with relevant authority"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_disbursement"	"tl_charge_notation"
79	"Conditions Precedent"	1	8	"Final supplier invoice reviewed and matched with approved financing amount"	true	"Term Loan"	"Conventional Financing Products"	"usertask_conventional_disbursement"	"tl_final_invoice"
"@

# ================================
# PARSE + API CALL
# ================================
$lines = $rawData -split "`n"

foreach ($line in $lines) {

    if ([string]::IsNullOrWhiteSpace($line)) { continue }

    try {
        $parts = $line -split "`t"

        $clean = $parts | ForEach-Object { $_.Trim('"') }

        $body = @{
            id = 0
            category = $clean[1]
            categoryOrder = [int]$clean[2]
            checklistOrder = [int]$clean[3]
            checklistTitle = $clean[4]
            isMandatory = [System.Convert]::ToBoolean($clean[5])
            productName = $clean[6]
            productType = $clean[7]
            stage = $clean[8]
            variable = $clean[9]
        } | ConvertTo-Json -Depth 5

        Write-Host "Creating:" $clean[9]

        Invoke-RestMethod -Method POST `
            -Uri $checklistUrl `
            -Headers $headers `
            -Body $body

        Write-Host "Success"
    }
    catch {
        Write-Host "Failed:"
        Write-Host $line
        Write-Host $_
    }

    Start-Sleep -Milliseconds $sleepTime
}

Write-Host "ALL CHECKLISTS CREATED"