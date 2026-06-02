Write-Host "================================"
Write-Host "STEP 1: Authenticate"
Write-Host "================================"

$baseUrl = "http://localhost/flowable-service"

$loginBody = @{
    userId = "sacca.admin"
    password = "DIB@2026"
} | ConvertTo-Json

$response = Invoke-RestMethod -Method Post `
    -Uri "$baseUrl/users/authenticate" `
    -ContentType "application/json" `
    -Body $loginBody

$token = $response.token

$headers = @{
    Authorization = "Bearer $token"
}

# -------------------------------
# STEP 2: METADATA
# -------------------------------
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
            -Headers $headers

        return $resp.id
    }
    catch {
        $resp = Invoke-RestMethod -Method Get `
            -Uri "$baseUrl/documents/metadata-types?name=$name" `
            -Headers $headers

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

# -------------------------------
# STEP 3: DOCUMENT LIST
# -------------------------------
$mayan_documents = @(

    @{ name = "Copy of National ID"; mandatory = $true },

    @{ name = "Latest 3 payslips"; mandatory = $true },

    @{ name = "Employer confirmation letter"; mandatory = $false },

    @{ name = "Valuation report (if collateral)"; mandatory = $false },

    @{ name = "Insurance policy (if applicable)"; mandatory = $false },

    @{ name = "Share Certificate"; mandatory = $false },

    @{ name = "Collateral (Fix Deposit)"; mandatory = $false }

    @{ name = "Offer letter"; mandatory = $false }

    @{ name = "Agreement"; mandatory = $false }

    @{ name = "Facility Agreement"; mandatory = $false }

    @{ name = "Credit Appraisal Memo"; mandatory = $false }

    @{ name = "Legal Clearance Memo"; mandatory = $false }
)

# -------------------------------
# STEP 4: DOC TYPE FUNCTIONS
# -------------------------------
$docTypeMap = @{}

function Get-OrCreate-DocType($name) {

    if ($docTypeMap.ContainsKey($name)) {
        return $docTypeMap[$name]
    }

    $all = Invoke-RestMethod -Method Get `
        -Uri "$baseUrl/documents/types" `
        -Headers $headers

    $results = if ($all.results) { $all.results } else { $all }

    $existing = $results | Where-Object { $_.label -eq $name }

    if ($existing) {

        $docTypeMap[$name] = $existing.id

        Write-Host "Using existing DocType: $name -> ID: $($existing.id)"

        return $existing.id
    }

    $resp = Invoke-RestMethod -Method Post `
        -Uri "$baseUrl/documents/types?label=$name" `
        -Headers $headers

    $docTypeMap[$name] = $resp.id

    Write-Host "Created DocType: $name -> ID: $($resp.id)"

    return $resp.id
}

function Attach-Metadata($docTypeId) {

    foreach ($metaKey in $metadataMap.Keys) {

        $metaId = $metadataMap[$metaKey]

        try {

            Invoke-RestMethod -Method Post `
                -Uri "$baseUrl/documents/types/$docTypeId/metadata-types?metadataTypeId=$metaId&required=false" `
                -Headers $headers

            Write-Host "Attached metadata: $metaKey"
        }
        catch {

            Write-Host "Metadata already attached: $metaKey"
        }

        Start-Sleep -Milliseconds 300
    }
}

# -------------------------------
# STEP 5: CREATE DOC TYPES
# -------------------------------
Write-Host "STEP 5: Creating Document Types"

foreach ($doc in $mayan_documents) {

    Write-Host "Processing: $($doc.name)"

    $docTypeId = Get-OrCreate-DocType $doc.name

    if (-not $docTypeId) {

        Write-Host "Failed DocType: $($doc.name)"
        continue
    }

    Attach-Metadata $docTypeId
}

# -------------------------------
# STEP 6: CREATE CHECKLIST
# -------------------------------
Write-Host "STEP 6: Creating Checklist"

$urlChecklist = "$baseUrl/api/product-checklist"

$productNames = @(
    "Development Loan",
    "Emergency Loan"
)

foreach ($productName in $productNames) {

    Write-Host "===================================="
    Write-Host "Creating checklist for: $productName"
    Write-Host "===================================="

    foreach ($doc in $mayan_documents) {

        $docTypeId = $docTypeMap[$doc.name]

        if (-not $docTypeId) {

            Write-Host "Missing docTypeId for $($doc.name)"
            continue
        }

        $now = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss.fff")

        $body = @{
            productName = $productName
            productType = $null

            documentType = $doc.name
            documentTypeId = $docTypeId

            isMandatory = $doc.mandatory
            isActive = $true

            createdAt = $now
            updatedAt = $now
        } | ConvertTo-Json

        Write-Host "Creating checklist:"
        Write-Host "Product : $productName"
        Write-Host "Document: $($doc.name)"

        Invoke-RestMethod -Method Post `
            -Uri $urlChecklist `
            -Headers $headers `
            -ContentType "application/json" `
            -Body $body

        Write-Host "Checklist created successfully"

        Start-Sleep -Milliseconds 500
    }
}

# -------------------------------
# DONE
# -------------------------------
Write-Host "================================"
Write-Host "DONE SUCCESSFULLY"
Write-Host "================================"