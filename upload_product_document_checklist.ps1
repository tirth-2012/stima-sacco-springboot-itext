Write-Host "================================"
Write-Host "STEP 1: Authenticate"
Write-Host "================================"

$baseUrl = "http://localhost:8098/flowable-service"

$loginBody = @{
    userId = "akipchoge"
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
    @{ name = "Audited Financial Statements - minimum 2 years"; mandatory = $true },
    @{ name = "Collateral document (MOTOR_VEHICLE)"; mandatory = $false },
    @{ name = "Collateral document (CASH)"; mandatory = $false },
    @{ name = "Director IDs national ID or passport for each director"; mandatory = $false },
    @{ name = "Collateral document (EQUIPMENT)"; mandatory = $false },
    @{ name = "CRB Report (auto-fetched via Metropol API)"; mandatory = $false },
    @{ name = "Collateral document (LAND)"; mandatory = $false },
    @{ name = "Business Registration Certificate (CPR)"; mandatory = $false },
    @{ name = "Business premises confirmation lease agreement or ownership"; mandatory = $false },
    @{ name = "KRA PIN Certificate"; mandatory = $true },
    @{ name = "Bank statements minimum 6 months, all active accounts"; mandatory = $true },
    @{ name = "Offer letter"; mandatory = $false },
    @{ name = "Wakala Agreement"; mandatory = $false },
    @{ name = "Facility Agreement"; mandatory = $false },
    @{ name = "RCA Memo"; mandatory = $false },
    @{ name = "Legal Clearance Memo"; mandatory = $false }
)

$islamic_documents =  @(
     @{ name = "Audited Financial Statements - minimum 2 years"; mandatory = $true },
     @{ name = "Collateral document (MOTOR_VEHICLE)"; mandatory = $false },
     @{ name = "Collateral document (CASH)"; mandatory = $false },
     @{ name = "Director IDs national ID or passport for each director"; mandatory = $false },
     @{ name = "Collateral document (EQUIPMENT)"; mandatory = $false },
     @{ name = "CRB Report (auto-fetched via Metropol API)"; mandatory = $false },
     @{ name = "Collateral document (LAND)"; mandatory = $false },
     @{ name = "Business Registration Certificate (CPR)"; mandatory = $false },
     @{ name = "Business premises confirmation lease agreement or ownership"; mandatory = $false },
     @{ name = "KRA PIN Certificate"; mandatory = $true },
     @{ name = "Bank statements minimum 6 months, all active accounts"; mandatory = $true },
     @{ name = "Offer letter"; mandatory = $false },
     @{ name = "Wakala Agreement"; mandatory = $false },
     @{ name = "Facility Agreement"; mandatory = $false },
     @{ name = "RCA Memo"; mandatory = $false },
     @{ name = "Legal Clearance Memo"; mandatory = $false }
    )
$conventional_documents =  @(
      @{ name = "Audited Financial Statements - minimum 2 years"; mandatory = $true },
      @{ name = "Collateral document (MOTOR_VEHICLE)"; mandatory = $false },
      @{ name = "Collateral document (CASH)"; mandatory = $false },
      @{ name = "Director IDs national ID or passport for each director"; mandatory = $false },
      @{ name = "Collateral document (EQUIPMENT)"; mandatory = $false },
      @{ name = "CRB Report (auto-fetched via Metropol API)"; mandatory = $false },
      @{ name = "Collateral document (LAND)"; mandatory = $false },
      @{ name = "Business Registration Certificate (CPR)"; mandatory = $false },
      @{ name = "Business premises confirmation lease agreement or ownership"; mandatory = $false },
      @{ name = "KRA PIN Certificate"; mandatory = $true },
      @{ name = "Bank statements minimum 6 months, all active accounts"; mandatory = $true },
      @{ name = "Offer letter"; mandatory = $false },
      @{ name = "Facility Agreement"; mandatory = $false },
      @{ name = "RCA Memo"; mandatory = $false },
      @{ name = "Legal Clearance Memo"; mandatory = $false }
    )

# -------------------------------
# STEP 4: DOC TYPE FUNCTIONS
# -------------------------------
$docTypeMap = @{}

function Get-OrCreate-DocType($name) {

    # Return cached value
    if ($docTypeMap.ContainsKey($name)) {
        return $docTypeMap[$name]
    }

    # FIRST SEARCH EXISTING TYPES
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

    # CREATE ONLY IF NOT FOUND
    $resp = Invoke-RestMethod -Method Post `
        -Uri "$baseUrl/documents/types?label=$name" `
        -Headers $headers

    $docTypeMap[$name] = $resp.id

    Write-Host "Created DocType: $name -> ID: $($resp.id)"
    Write-Host "---- docTypeMap contents ----"

    foreach ($key in $docTypeMap.Keys) {
        Write-Host "Name: $key | ID: $($docTypeMap[$key])"
    }

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
# STEP 5: CREATE DOC TYPES + METADATA
# -------------------------------
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

# ADD THIS BLOCK HERE
Write-Host "Fix: Attaching metadata to DEFAULT document type (ID=1)"
Attach-Metadata 1

# -------------------------------
# STEP 6: CHECKLIST CREATION
# -------------------------------
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
            productType = $productType
            productName = $productName
            documentType = $doc.name
            documentTypeId = $docTypeId
            isMandatory = $doc.mandatory
            isActive = $true
            createdAt = $now
            updatedAt = $now
        } | ConvertTo-Json

        Write-Host "✅ API Request body: $($body)"
        Write-Host "✅ Document type id : $($docTypeId)"

        Invoke-RestMethod -Method Post `
            -Uri $urlChecklist `
            -Headers $headers `
            -ContentType "application/json" `
            -Body $body

        Write-Host "✅ Checklist created: $($doc.name)"

        Start-Sleep -Milliseconds 500
    }
}

Write-Host "STEP 6: Creating Checklists"

Create-Checklist $islamic_documents "Islamic Financing Products" "Murabaha"
Create-Checklist $conventional_documents "Conventional Financing Products" "Term Loan"

# -------------------------------
# DONE
# -------------------------------
Write-Host "================================"
Write-Host "🎉 DONE SUCCESSFULLY"
Write-Host "================================"