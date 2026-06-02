# ================================
# SSL BYPASS (self-signed cert)
# ================================
if ($PSVersionTable.PSVersion.Major -ge 6) {
    # PowerShell 6+ supports -SkipCertificateCheck per call (handled below)
} else {
    # PowerShell 5 - global bypass
    Add-Type @"
        using System.Net;
        using System.Security.Cryptography.X509Certificates;
        public class TrustAll : ICertificatePolicy {
            public bool CheckValidationResult(
                ServicePoint sp, X509Certificate cert,
                WebRequest req, int problem) { return true; }
        }
"@
    [System.Net.ServicePointManager]::CertificatePolicy = New-Object TrustAll
    [System.Net.ServicePointManager]::SecurityProtocol = `
        [System.Net.SecurityProtocolType]::Tls12 -bor `
        [System.Net.SecurityProtocolType]::Tls13
}

# Reusable splat for all Invoke-RestMethod calls
$restDefaults = @{}
if ($PSVersionTable.PSVersion.Major -ge 6) {
    $restDefaults["SkipCertificateCheck"] = $true
}

# ================================
# STEP 1: Authenticate
# ================================
Write-Host "================================"
Write-Host "STEP 1: Authenticate"
Write-Host "================================"

$baseUrl = "https://localhost/flowable-service"

$loginBody = @{
    userId   = "akipchoge"
    password = "DIB@2026"
} | ConvertTo-Json

$response = Invoke-RestMethod -Method Post `
    -Uri "$baseUrl/users/authenticate" `
    -ContentType "application/json" `
    -Body $loginBody `
    @restDefaults

$token = $response.token

$headers = @{
    Authorization = "Bearer $token"
}

Write-Host "✅ Authenticated. Token acquired."

# ================================
# STEP 2: Metadata Setup
# ================================
Write-Host ""
Write-Host "STEP 2: Metadata Setup"

$metadataNames = @(
    "processInstanceId",
    "status",
    "uploadedBy"
)

$metadataMap = @{}

function Get-OrCreate-Metadata($name) {
    try {
        $resp = Invoke-RestMethod -Method Post `
            -Uri "$baseUrl/documents/metadata-types?label=$name&name=$name" `
            -Headers $headers `
            @restDefaults
        return $resp.id
    }
    catch {
        $resp = Invoke-RestMethod -Method Get `
            -Uri "$baseUrl/documents/metadata-types?name=$name" `
            -Headers $headers `
            @restDefaults
        return $resp.id
    }
    Start-Sleep -Milliseconds 300
}

foreach ($name in $metadataNames) {
    $id = Get-OrCreate-Metadata $name
    if ($id) {
        $metadataMap[$name] = $id
        Write-Host "Metadata [$name]: $id"
    }
}

# ================================
# STEP 3: Document Lists
# ================================

$mayan_documents = @(
    @{ name = "Audited Financial Statements - minimum 2 years"; mandatory = $true },
    @{ name = "Collateral document (MOTOR_VEHICLE)"; mandatory = $true },
    @{ name = "Director IDs national ID or passport for each director"; mandatory = $true },
    @{ name = "Collateral document (EQUIPMENT)"; mandatory = $false },
    @{ name = "CRB Report (auto-fetched via Metropol API)"; mandatory = $false },
    @{ name = "Collateral document (LAND)"; mandatory = $false },
    @{ name = "Business Registration Certificate (CPR)"; mandatory = $false },
    @{ name = "Business premises confirmation lease agreement or ownership"; mandatory = $false },
    @{ name = "KRA PIN Certificate"; mandatory = $false },
    @{ name = "Bank statements minimum 6 months, all active accounts"; mandatory = $false },
    @{ name = "Offer letter"; mandatory = $false },
    @{ name = "Wakala Agreement"; mandatory = $false },
    @{ name = "Facility Agreement"; mandatory = $false },
    @{ name = "RCA Memo"; mandatory = $false },
    @{ name = "Legal Clearance Memo"; mandatory = $false }
)

$islamic_documents = @(
    @{ name = "Audited Financial Statements - minimum 2 years"; mandatory = $true },
    @{ name = "Collateral document (MOTOR_VEHICLE)"; mandatory = $true },
    @{ name = "Director IDs national ID or passport for each director"; mandatory = $true },
    @{ name = "Collateral document (EQUIPMENT)"; mandatory = $false },
    @{ name = "CRB Report (auto-fetched via Metropol API)"; mandatory = $false },
    @{ name = "Collateral document (LAND)"; mandatory = $false },
    @{ name = "Business Registration Certificate (CPR)"; mandatory = $false },
    @{ name = "Business premises confirmation lease agreement or ownership"; mandatory = $false },
    @{ name = "KRA PIN Certificate"; mandatory = $false },
    @{ name = "Bank statements minimum 6 months, all active accounts"; mandatory = $false },
    @{ name = "Offer letter"; mandatory = $false },
    @{ name = "Wakala Agreement"; mandatory = $false },
    @{ name = "Facility Agreement"; mandatory = $false },
    @{ name = "RCA Memo"; mandatory = $false },
    @{ name = "Legal Clearance Memo"; mandatory = $false }
)

$conventional_documents = @(
    @{ name = "Audited Financial Statements - minimum 2 years"; mandatory = $true },
    @{ name = "Collateral document (MOTOR_VEHICLE)"; mandatory = $true },
    @{ name = "Director IDs national ID or passport for each director"; mandatory = $true },
    @{ name = "Collateral document (EQUIPMENT)"; mandatory = $false },
    @{ name = "CRB Report (auto-fetched via Metropol API)"; mandatory = $false },
    @{ name = "Collateral document (LAND)"; mandatory = $false },
    @{ name = "Business Registration Certificate (CPR)"; mandatory = $false },
    @{ name = "Business premises confirmation lease agreement or ownership"; mandatory = $false },
    @{ name = "KRA PIN Certificate"; mandatory = $false },
    @{ name = "Bank statements minimum 6 months, all active accounts"; mandatory = $false },
    @{ name = "Offer letter"; mandatory = $false },
    @{ name = "Facility Agreement"; mandatory = $false },
    @{ name = "RCA Memo"; mandatory = $false },
    @{ name = "Legal Clearance Memo"; mandatory = $false }
)

# ================================
# STEP 4: Doc Type Functions
# ================================

$docTypeMap = @{}

function Get-OrCreate-DocType($name) {
    if ($docTypeMap.ContainsKey($name)) {
        return $docTypeMap[$name]
    }

    try {
        $resp = Invoke-RestMethod -Method Post `
            -Uri "$baseUrl/documents/types?label=$name" `
            -Headers $headers `
            @restDefaults

        $docTypeMap[$name] = $resp.id
        return $resp.id
    }
    catch {
        $all = Invoke-RestMethod -Method Get `
            -Uri "$baseUrl/documents/types" `
            -Headers $headers `
            @restDefaults

        $id = ($all | Where-Object { $_.label -eq $name }).id
        $docTypeMap[$name] = $id
        return $id
    }
    Start-Sleep -Milliseconds 300
}

function Attach-Metadata($docTypeId) {
    foreach ($metaKey in $metadataMap.Keys) {
        $metaId = $metadataMap[$metaKey]

        try {
            Invoke-RestMethod -Method Post `
                -Uri "$baseUrl/documents/types/$docTypeId/metadata-types?metadataTypeId=$metaId&required=false" `
                -Headers $headers `
                @restDefaults

            Write-Host "  Attached metadata: $metaKey"
        }
        catch {
            Write-Host "  Metadata already attached: $metaKey"
        }
        Start-Sleep -Milliseconds 300
    }
}

# ================================
# STEP 5: Create Doc Types + Metadata
# ================================
Write-Host ""
Write-Host "STEP 5: Creating Document Types"

foreach ($doc in $mayan_documents) {
    Write-Host "Processing: $($doc.name)"

    $docTypeId = Get-OrCreate-DocType $doc.name

    if (-not $docTypeId) {
        Write-Host "❌ Failed DocType: $($doc.name)"
        continue
    }

    Attach-Metadata $docTypeId
}

# ================================
# STEP 6: Checklist Creation
# ================================

$urlChecklist = "$baseUrl/api/product-checklist"

function Create-Checklist($documents, $productType, $productName) {
    foreach ($doc in $documents) {
        $docTypeId = $docTypeMap[$doc.name]

        if (-not $docTypeId) {
            Write-Host "❌ Missing docTypeId for $($doc.name)"
            continue
        }

        $now = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss.fff")

        $body = @{
            productType    = $productType
            productName    = $productName
            documentType   = $doc.name
            documentTypeId = $docTypeId
            isMandatory    = $doc.mandatory
            isActive       = $true
            createdAt      = $now
            updatedAt      = $now
        } | ConvertTo-Json

        Invoke-RestMethod -Method Post `
            -Uri $urlChecklist `
            -Headers $headers `
            -ContentType "application/json" `
            -Body $body `
            @restDefaults

        Write-Host "✅ Checklist created: $($doc.name)"
        Start-Sleep -Milliseconds 500
    }
}

Write-Host ""
Write-Host "STEP 6: Creating Checklists"

Create-Checklist $islamic_documents "Islamic Financing Products" "Murabaha"
Create-Checklist $conventional_documents "Conventional Financing Products" "Term Loan"

# ================================
# DONE
# ================================
Write-Host ""
Write-Host "================================"
Write-Host "🎉 DONE SUCCESSFULLY"
Write-Host "================================"