Write-Host "==============================="
Write-Host "STEP 1: Authenticate"
Write-Host "==============================="

$loginBody = @{
    userId = "akhan"
    password = "test"
} | ConvertTo-Json

$response = Invoke-RestMethod -Method Post `
    -Uri "http://102.37.105.250/flowable-service/users/authenticate" `
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
        -Uri "http://102.37.105.250/flowable-service/documents/types?label=$label" `
        -Headers @{ Authorization = "Bearer $TOKEN" }
}

# ===============================
Write-Host "STEP 3: Create Metadata Type"
# ===============================

Invoke-RestMethod -Method Post `
    -Uri "http://102.37.105.250/flowable-service/documents/metadata-types?label=processInstanceId&name=processInstanceId" `
    -Headers @{ Authorization = "Bearer $TOKEN" }

# ===============================
Write-Host "STEP 4: Upload Documents"
# ===============================

$filePath = "Test.docx"

for ($i = 2; $i -le 9; $i++) {
    Write-Host "Uploading for docTypeId=$i"

    Invoke-RestMethod -Method Post `
        -Uri "http://102.37.105.250/flowable-service/documents/upload?documentTypeId=$i&label=test" `
        -Headers @{ Authorization = "Bearer $TOKEN" } `
        -Form @{ file = Get-Item $filePath }
}

# ===============================
Write-Host "STEP 5: Attach Metadata"
# ===============================

for ($i = 1; $i -le 9; $i++) {
    Invoke-RestMethod -Method Post `
        -Uri "http://102.37.105.250/flowable-service/documents/types/$i/metadata-types?metadataTypeId=1&required=false" `
        -Headers @{ Authorization = "Bearer $TOKEN" }
}

# ===============================
Write-Host "STEP 6: Add Metadata to Documents"
# ===============================

for ($i = 1; $i -le 9; $i++) {
    Invoke-RestMethod -Method Post `
        -Uri "http://102.37.105.250/flowable-service/documents/$i/metadata?metadataTypeId=1&value=processInstanceId" `
        -Headers @{ Authorization = "Bearer $TOKEN" }
}

Write-Host "==============================="
Write-Host "ALL DONE SUCCESSFULLY 🎉"
Write-Host "==============================="