Write-Host "==============================="
Write-Host "STEP 1: Authenticate"
Write-Host "==============================="

$loginBody = @{
    userId = "akhan"
    password = "test"
} | ConvertTo-Json

$response = Invoke-RestMethod -Method Post `
    -Uri "http://localhost:8098/flowable-service/users/authenticate" `
    -ContentType "application/json" `
    -Body $loginBody

$TOKEN = $response.token

if (-not $TOKEN) {
    Write-Host "ERROR: Token not fetched"
    exit
}

Write-Host "Token fetched successfully"

# ===============================
Write-Host "STEP 2: Create Document Types"
# ===============================

$labels = @(
    "KRA PIN Certificate",
    "Bank Statements 6 months",
    "Management Accounts 2024",
    "Proforma Invoice 3x Isuzu NQR",
    "Certificate of Incorporation",
    "CR12 Directors Shareholders",
    "Vehicle Log Books 3 units",
    "Guarantor National ID"
)

foreach ($label in $labels) {
    Write-Host "Creating: $label"

    Invoke-RestMethod -Method Post `
        -Uri "http://localhost:8098/flowable-service/documents/types?label=$label" `
        -Headers @{ Authorization = "Bearer $TOKEN" }
}

# ===============================
Write-Host "STEP 3: Create Metadata Type"
# ===============================

Invoke-RestMethod -Method Post `
    -Uri "http://localhost:8098/flowable-service/documents/metadata-types?label=processInstanceId&name=processInstanceId" `
    -Headers @{ Authorization = "Bearer $TOKEN" }

# ===============================
Write-Host "STEP 4: Attach Metadata"
# ===============================

for ($i = 1; $i -le 9; $i++) {
    Invoke-RestMethod -Method Post `
        -Uri "http://localhost:8098/flowable-service/documents/types/$i/metadata-types?metadataTypeId=1&required=false" `
        -Headers @{ Authorization = "Bearer $TOKEN" }
}
