package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.DocumentDto;
import com.rutusoft.flowable.dto.VariableInstanceDto;
import com.rutusoft.flowable.entity.AppNotification;
import com.rutusoft.flowable.repository.AppNotificationRepository;
import com.rutusoft.flowable.repository.MayanDocumentRepository;
import com.rutusoft.flowable.service.DocumentService;
import com.rutusoft.flowable.service.ProcessInstanceVariablesService;
import com.rutusoft.flowable.service.TokenService;
import com.rutusoft.flowable.utility.JsonUtils;
import com.rutusoft.flowable.utility.MailNotificationUtil;
import com.rutusoft.flowable.utility.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private static final String DOCUMENTS_API = "/documents/";
    private final RestTemplate restTemplate;
    private final TokenService tokenService;
    private final MayanDocumentRepository mayanDocumentRepository;
    private final ProcessInstanceVariablesService processInstanceVariablesService;
    private final SecurityUtil securityUtil;
    private final Map<Long, Map<String, Object>> fileCache = new ConcurrentHashMap<>();


    @Value("${mayan.base-url}")
    private String baseUrl;

    @Autowired
    private MailNotificationUtil mailNotificationUtil;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private AppNotificationRepository notificationRepository;

    // ------------------------------------------------------------------------
    // Common Headers
    // ------------------------------------------------------------------------
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Token " + tokenService.getToken());
        return headers;
    }

    // ------------------------------------------------------------------------
    // Upload Document
    // ------------------------------------------------------------------------
    @Override
    public String uploadDocument(Long documentTypeId, String label, String description, MultipartFile file) {
        log.info("Uploading document");
        try {
            HttpHeaders headers = getHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("document_type_id", documentTypeId.toString());
            body.add("label", label);
            body.add("description", description);
            body.add("file", file.getResource());

            HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/documents/upload/",
                    HttpMethod.POST,
                    request,
                    String.class
            );
            return response.getBody();
        } catch (Exception ex) {
            log.error("Document upload failed for document type Id : {}, label : {}, description : {}, Error : {}", documentTypeId, label, description, ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    // ------------------------------------------------------------------------
    // Get All Documents
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> getAllDocuments(int page, int size) {

        if (page <= 0) page = 1;
        if (size <= 0) size = 10;

        String url = baseUrl + DOCUMENTS_API + "?page=" + page + "&page_size=" + size;

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

        ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Object.class
        );

        Object body = response.getBody();

        List<Map<String, Object>> documents = new ArrayList<>();
        int totalCount = 0;

        if (body instanceof Map) {
            Map<String, Object> responseBody = (Map<String, Object>) body;

            if (responseBody.get("results") instanceof List) {
                documents = (List<Map<String, Object>>) responseBody.get("results");
            }

            if (responseBody.get("count") != null) {
                totalCount = ((Number) responseBody.get("count")).intValue();
            }

        } else if (body instanceof List) {
            documents = (List<Map<String, Object>>) body;
            totalCount = documents.size();
        }

        // 🔹 Attach metadata
        for (Map<String, Object> document : documents) {
            try {
                Number id = (Number) document.get("id");
                Long docId = id.longValue();

                Map<String, Object> metadataResponse = getMetadata(docId);

                if (metadataResponse != null && metadataResponse.get("results") instanceof List) {
                    document.put("metadata", metadataResponse.get("results"));
                } else {
                    document.put("metadata", Collections.emptyList());
                }

            } catch (Exception e) {
                document.put("metadata", Collections.emptyList());
            }
        }

        // 🔹 Final response
        Map<String, Object> finalResponse = new HashMap<>();

        int totalPages = (int) Math.ceil((double) totalCount / size);

        finalResponse.put("page", page);
        finalResponse.put("size", size);
        finalResponse.put("totalElements", totalCount);
        finalResponse.put("totalPages", totalPages);
        finalResponse.put("data", documents);

        return finalResponse;
    }

    // ------------------------------------------------------------------------
    // Get Document By ID
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> getDocumentById(Long id) {

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + DOCUMENTS_API + id + "/",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    // ------------------------------------------------------------------------
    // Delete Document
    // ------------------------------------------------------------------------
    @Override
    public void deleteDocument(Long id) {

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

        restTemplate.exchange(
                baseUrl + DOCUMENTS_API + id + "/",
                HttpMethod.DELETE,
                entity,
                Void.class
        );
    }

    // ------------------------------------------------------------------------
    // Add Metadata
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> addMetadata(Long documentId, Long metadataTypeId, String value) {

        log.info("Adding metadata for documentId : {}", documentId);

        int maxRetries = 3;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {

            try {
                HttpHeaders headers = getHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> body = new HashMap<>();
                body.put("metadata_type_id", metadataTypeId);
                body.put("value", value);

                HttpEntity<Map<String, Object>> request =
                        new HttpEntity<>(body, headers);

                // ✅ FIXED HERE
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                        baseUrl + "/documents/" + documentId + "/metadata/",
                        HttpMethod.POST,
                        request,
                        new ParameterizedTypeReference<Map<String, Object>>() {}
                );

                log.info("Added metadata successfully for documentId : {}", documentId);

                //sleep(500);

                return response.getBody(); // ✅ NO .get(0)

            } catch (HttpClientErrorException.TooManyRequests ex) {

                log.warn("429 Too Many Requests. Attempt {}/{}. Retrying...",
                        attempt, maxRetries);

                sleep(1000);

            } catch (Exception ex) {
                log.error("Error adding metadata", ex);
                throw ex;
            }
        }

        throw new RuntimeException("Failed to add metadata after retries");
    }

    // ------------------------------------------------------------------------
    // Add Multiple Metadata
    // ------------------------------------------------------------------------
    @Override
    public void addMultipleMetadata(Long documentId, Map<String, String> metadataMap) {
        HttpHeaders headers = getHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // ----------------------------------------------------------------
        // 1️⃣ Fetch metadata types
        // ----------------------------------------------------------------
        ResponseEntity<Object> metaTypeResponse = restTemplate.exchange(
                baseUrl + "/metadata_types/",
                HttpMethod.GET,
                request,
                Object.class
        );

        Object metaBody = metaTypeResponse.getBody();

        List<Map<String, Object>> metadataTypes = new ArrayList<>();

        if (metaBody instanceof Map) {

            Object results = ((Map<?, ?>) metaBody).get("results");

            if (results instanceof List) {
                metadataTypes = (List<Map<String, Object>>) results;
            }

        } else if (metaBody instanceof List) {

            metadataTypes = (List<Map<String, Object>>) metaBody;
        }

        Map<String, Long> labelToIdMap = new HashMap<>();

        for (Map<String, Object> type : metadataTypes) {

            String label = (String) type.get("label");
            Number id = (Number) type.get("id");

            if (label != null && id != null) {
                labelToIdMap.put(label, id.longValue());
            }
        }

        // ----------------------------------------------------------------
        // 2️⃣ Fetch existing metadata
        // ----------------------------------------------------------------
        ResponseEntity<Object> existingResponse = restTemplate.exchange(
                baseUrl + "/documents/" + documentId + "/metadata/",
                HttpMethod.GET,
                request,
                Object.class
        );

        Object existingBody = existingResponse.getBody();

        List<Map<String, Object>> existingList = new ArrayList<>();

        if (existingBody instanceof Map) {

            Object results = ((Map<?, ?>) existingBody).get("results");

            if (results instanceof List) {
                existingList = (List<Map<String, Object>>) results;
            }

        } else if (existingBody instanceof List) {

            existingList = (List<Map<String, Object>>) existingBody;
        }

        Map<Long, String> existingMetadata = new HashMap<>();

        for (Map<String, Object> item : existingList) {

            Map<String, Object> metaType =
                    (Map<String, Object>) item.get("metadata_type");

            if (metaType == null) {
                continue;
            }

            Number typeId = (Number) metaType.get("id");

            String value = item.get("value") != null
                    ? item.get("value").toString()
                    : null;

            if (typeId != null) {
                existingMetadata.put(typeId.longValue(), value);
            }
        }

        // ----------------------------------------------------------------
        // 3️⃣ Add metadata
        // ----------------------------------------------------------------
        for (Map.Entry<String, String> entry : metadataMap.entrySet()) {

            String label = entry.getKey();
            String value = entry.getValue();

            Long metadataTypeId = labelToIdMap.get(label);

            if (metadataTypeId == null) {
                throw new IllegalArgumentException(
                        "Metadata type not found in Mayan: " + label
                );
            }

            // Skip existing metadata
            if (existingMetadata.containsKey(metadataTypeId)) {

                log.info("Skipping existing metadata: {}", label);

                continue;
            }

            addMetadata(documentId, metadataTypeId, value);
        }
    }

    // ------------------------------------------------------------------------
    // Update Metadata
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> updateMetadata(Long documentId,
                                              Long metadataId,
                                              String value) {

        log.info("Updating metadata using PUT. documentId={}, metadataId={}", documentId, metadataId);

        try {
            HttpHeaders headers = getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("value", value);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            String url = baseUrl + "/documents/" + documentId +
                    "/metadata/" + metadataId + "/";

            // ✅ FIX: Use Object instead of Map
            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    request,
                    Object.class
            );

            Object responseBody = response.getBody();

            if (responseBody instanceof Map) {
                return (Map<String, Object>) responseBody;
            }

            // fallback (rare but safe)
            return Collections.emptyMap();

        } catch (Exception e) {
            log.error("Error updating metadata", e);
            throw new RuntimeException(e);
        }
    }

    // ------------------------------------------------------------------------
// Update Multiple Metadata
// ------------------------------------------------------------------------
    @Override
    public void updateMultipleMetadata(Long documentId,
                                       Map<String, String> metadataMap) {

        try {

            HttpHeaders headers = getHeaders();
            HttpEntity<Void> request = new HttpEntity<>(headers);

            // ----------------------------------------------------------------
            // 1️⃣ Fetch metadata types
            // ----------------------------------------------------------------
            ResponseEntity<Object> metaTypeResponse = restTemplate.exchange(
                    baseUrl + "/metadata_types/",
                    HttpMethod.GET,
                    request,
                    Object.class
            );

            Object metaBody = metaTypeResponse.getBody();

            List<Map<String, Object>> metadataTypes = new ArrayList<>();

            if (metaBody instanceof Map) {

                Object results = ((Map<?, ?>) metaBody).get("results");

                if (results instanceof List) {
                    metadataTypes = (List<Map<String, Object>>) results;
                }

            } else if (metaBody instanceof List) {

                metadataTypes = (List<Map<String, Object>>) metaBody;
            }

            // label -> metadataTypeId
            Map<String, Long> labelToTypeId = new HashMap<>();

            for (Map<String, Object> type : metadataTypes) {

                Number id = (Number) type.get("id");
                String label = (String) type.get("label");

                if (id != null && label != null) {
                    labelToTypeId.put(label, id.longValue());
                }
            }

            // ----------------------------------------------------------------
            // 2️⃣ Fetch document metadata
            // ----------------------------------------------------------------
            ResponseEntity<Object> existingResponse = restTemplate.exchange(
                    baseUrl + "/documents/" + documentId + "/metadata/",
                    HttpMethod.GET,
                    request,
                    Object.class
            );

            Object existingBody = existingResponse.getBody();

            List<Map<String, Object>> existingList = new ArrayList<>();

            if (existingBody instanceof Map) {

                Object results = ((Map<?, ?>) existingBody).get("results");

                if (results instanceof List) {
                    existingList = (List<Map<String, Object>>) results;
                }

            } else if (existingBody instanceof List) {

                existingList = (List<Map<String, Object>>) existingBody;
            }

            // metadataTypeId -> metadataId
            Map<Long, Long> typeIdToMetadataId = new HashMap<>();

            for (Map<String, Object> item : existingList) {

                Map<String, Object> metaType =
                        (Map<String, Object>) item.get("metadata_type");

                if (metaType == null) {
                    continue;
                }

                Number typeIdNum = (Number) metaType.get("id");
                Number metadataIdNum = (Number) item.get("id");

                if (typeIdNum != null && metadataIdNum != null) {

                    typeIdToMetadataId.put(
                            typeIdNum.longValue(),
                            metadataIdNum.longValue()
                    );
                }
            }

            // ----------------------------------------------------------------
            // 3️⃣ Update metadata
            // ----------------------------------------------------------------
            for (Map.Entry<String, String> entry : metadataMap.entrySet()) {

                String label = entry.getKey();
                String value = entry.getValue();

                if (value == null || value.trim().isEmpty()) {
                    continue;
                }

                Long metadataTypeId = labelToTypeId.get(label);

                if (metadataTypeId == null) {

                    log.warn("Metadata type not found: {}", label);
                    continue;
                }

                Long metadataId = typeIdToMetadataId.get(metadataTypeId);

                // metadata not attached -> create it
                if (metadataId == null) {

                    log.info("Metadata not found. Creating metadata for label={}", label);

                    addMetadata(documentId, metadataTypeId, value);

                    continue;
                }

                // metadata exists -> update it
                updateMetadata(documentId, metadataId, value);

                log.info("Updated metadata. label={}, value={}", label, value);
            }

        } catch (Exception e) {

            log.error("Error updating multiple metadata", e);

            throw new RuntimeException(
                    "Failed to update multiple metadata: " + e.getMessage(),
                    e
            );
        }
    }

    // ------------------------------------------------------------------------
    // Get document with Metadata
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> getMetadata(Long documentId) {

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

        ResponseEntity<Object> response = restTemplate.exchange(
                baseUrl + DOCUMENTS_API + documentId + "/metadata/",
                HttpMethod.GET,
                entity,
                Object.class
        );

        Object body = response.getBody();

        if (body instanceof Map) {
            return (Map<String, Object>) body;
        }

        // handle array response
        if (body instanceof List) {
            return Map.of("results", body);
        }

        return Collections.emptyMap();
    }

    // ------------------------------------------------------------------------
    // Create Document Type
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> createDocumentType(String label) {

        try {
            HttpHeaders headers = getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            String body = String.format("{\"label\": \"%s\"}", label);

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    baseUrl + "/document_types/",
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Error occurred while creating new document type : {}", label, e);
            throw new RuntimeException(e);
        }
    }

    // ------------------------------------------------------------------------
    // Get Document Types
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> getDocumentTypes() {

        log.info("Fetching document types from mayan");
        String url = baseUrl + "/document_types/?page_size=100";

        try {
            HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to fetch document types. HTTP Status: "
                        + response.getStatusCode());
            }

            Object body = response.getBody();

            if (body instanceof Map) {
                return (Map<String, Object>) body;
            }

            if (body instanceof List) {
                return Map.of("results", body);
            }

            return Collections.emptyMap();

        } catch (Exception e) {
            log.error("Error calling Document Types API. URL: {}", url, e);
            throw new RuntimeException("Failed to fetch document types from external service", e);
        }
    }

    // ------------------------------------------------------------------------
    // Create Metadata Type
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> createMetadataType(String label, String name) {

        HttpHeaders headers = getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"label\": \"%s\", \"name\": \"%s\"}",
                label, name
        );

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "/metadata_types/",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    // ------------------------------------------------------------------------
    // Add Metadata Type to Document Type
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> addMetadataTypeToDocumentType(Long documentTypeId,
                                                             Long metadataTypeId,
                                                             boolean required) {

        String url = String.format("%s/document_types/%d/metadata_types/", baseUrl, documentTypeId);

        try {
            HttpHeaders headers = getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = Map.of(
                    "metadata_type_id", metadataTypeId,
                    "required", required
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            //  DEBUG LOG - Request
            log.debug("Calling Mayan API: POST {}", url);
            log.debug("Request Headers: {}", headers);
            log.debug("Request Body: {}", requestBody);

            long start = System.currentTimeMillis();

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {}
            );

            long duration = System.currentTimeMillis() - start;

            //  DEBUG LOG - Response
            log.debug("Mayan API Response Status: {}", response.getStatusCode());
            log.debug("Mayan API Response Body: {}", response.getBody());
            log.debug("Mayan API Call Duration: {} ms", duration);

            return response.getBody();

        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("Rate limit hit while calling Mayan API. documentTypeId={}, metadataTypeId={}",
                    documentTypeId, metadataTypeId);
            throw new RuntimeException("Mayan API rate limit exceeded", e);

        } catch (HttpClientErrorException e) {
            log.error("Client error while calling Mayan API. Status={}, Body={}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Mayan API client error", e);

        } catch (HttpServerErrorException e) {
            log.error("Server error from Mayan API. Status={}, Body={}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Mayan API server error", e);

        } catch (ResourceAccessException e) {
            log.error("Connection/timeout error while calling Mayan API: {}", url, e);
            throw new RuntimeException("Mayan API connection error", e);

        } catch (Exception e) {
            log.error("Unexpected error while adding metadata to documentTypeId={}", documentTypeId, e);
            throw new RuntimeException("Unexpected error", e);
        }
    }

    // ------------------------------------------------------------------------
    // Create Cabinet
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> createCabinet(String label, Long parentId) {

        HttpHeaders headers = getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = (parentId == null)
                ? String.format("{\"label\": \"%s\"}", label)
                : String.format("{\"label\": \"%s\", \"parent\": %d}", label, parentId);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "/cabinets/",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    // ------------------------------------------------------------------------
    // Add Document to Cabinet
    // ------------------------------------------------------------------------
    @Override
    public void addDocumentToCabinet(Long cabinetId, Long documentId) {

        HttpHeaders headers = getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("{\"document\": %d}", documentId);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        restTemplate.exchange(
                baseUrl + "/cabinets/" + cabinetId + "/documents/add/",
                HttpMethod.POST,
                request,
                Void.class
        );
    }

    // ------------------------------------------------------------------------
    // Upload New Version File to Document
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> uploadNewVersionDocumentFile(Long documentId,
                                                            MultipartFile file,
                                                            String comment,
                                                            String actionName) {

        HttpHeaders headers = getHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("action_name", actionName); // replace / append
        body.add("file_new", file.getResource());
        body.add("comment", comment);

        HttpEntity<LinkedMultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "/documents/" + documentId + "/files/",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    // ------------------------------------------------------------------------
// Get Document Versions (ALL versions of a document)
// ------------------------------------------------------------------------
    @Override
    public List<DocumentDto> getDocumentVersions(Long documentId) {

        log.debug("Starting getDocumentVersions for documentId: {}", documentId);

        List<DocumentDto> documentVersionDtos = new ArrayList<>();
        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

        List<Map<String, Object>> results = Collections.emptyList();

        // ================================
        // 🔹 STEP 1: GET VERSIONS
        // ================================
        try {

            //throttle();

            String url = baseUrl + "/documents/" + documentId +
                    "/versions/?page_size=100&_ordering=-id";

            log.debug("Calling Versions API: {}", url);

            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );

            Object body = response.getBody();

            if (body instanceof Map) {

                Object resultsObj = ((Map<?, ?>) body).get("results");

                results = resultsObj instanceof List
                        ? (List<Map<String, Object>>) resultsObj
                        : new ArrayList<>();

            } else if (body instanceof List) {

                results = (List<Map<String, Object>>) body;

            } else {

                results = new ArrayList<>();
            }

            log.debug("Versions fetched. Count: {}", results.size());

        } catch (HttpClientErrorException | HttpServerErrorException ex) {

            log.error("Error calling Versions API. Status: {}, Body: {}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString(),
                    ex);

            throw new RuntimeException("Failed to fetch document versions", ex);

        } catch (ResourceAccessException ex) {

            log.error("Versions API unreachable", ex);

            throw new RuntimeException("Versions API unreachable", ex);

        } catch (Exception ex) {

            log.error("Unexpected error while fetching versions", ex);

            throw new RuntimeException("Unexpected error while fetching versions", ex);
        }

        // ================================
        // 🔹 STEP 2: GET FILES
        // ================================
        List<Map<String, Object>> files = new ArrayList<>();

        try {

            Map<String, Object> filesResponse = fileCache.computeIfAbsent(
                    documentId,
                    id -> {
                        log.debug("Fetching files from API for documentId: {}", id);
                        //throttle();
                        return getDocumentFiles(id);
                    }
            );

            Object filesObj = filesResponse.get("results");

            if (filesObj instanceof List) {
                files = (List<Map<String, Object>>) filesObj;
            }

            log.debug("Files fetched. Count: {}", files.size());

        } catch (Exception ex) {

            log.error("Error fetching files for documentId: {}", documentId, ex);
        }

        // ================================
        // 🔹 STEP 3: GET METADATA
        // ================================
        Map<String, String> metadataMap = new HashMap<>();

        try {

            String url = baseUrl + "/documents/" + documentId + "/metadata/";

            log.debug("Calling Metadata API: {}", url);

            ResponseEntity<Object> metadataResponse = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );

            Object body = metadataResponse.getBody();

            List<Map<String, Object>> metadataResults = new ArrayList<>();

            if (body instanceof Map) {

                Object resultsObj = ((Map<?, ?>) body).get("results");

                if (resultsObj instanceof List) {
                    metadataResults = (List<Map<String, Object>>) resultsObj;
                }

            } else if (body instanceof List) {

                metadataResults = (List<Map<String, Object>>) body;
            }

            log.debug("Metadata fetched. Count: {}", metadataResults.size());

            for (Map<String, Object> item : metadataResults) {

                Map<String, Object> type =
                        (Map<String, Object>) item.get("metadata_type");

                if (type == null) {
                    continue;
                }

                String label = String.valueOf(type.get("label"));
                String value = String.valueOf(item.get("value"));

                metadataMap.put(label, value);
            }

        } catch (Exception ex) {

            log.error("Metadata parsing failed", ex);
        }

        // ================================
        // 🔹 STEP 4: GET DOCUMENT DETAILS FROM DB
        // ================================
        DocumentDto dbDocument = null;

        try {

            List<DocumentDto> docs =
                    mayanDocumentRepository.getDocumentsByProcessInstanceId(
                            mayanDocumentRepository.getProcessInstanceIdByDocumentId(documentId)
                    );

            if (docs != null) {

                for (DocumentDto dto : docs) {

                    if (dto.getId() != null &&
                            dto.getId().equals(documentId)) {

                        dbDocument = dto;
                        break;
                    }
                }
            }

        } catch (Exception ex) {

            log.error("Failed to fetch DB document details", ex);
        }

        // ================================
        // 🔹 STEP 5: BUILD DTO
        // ================================
        for (int i = 0; i < results.size(); i++) {

            Map<String, Object> result = results.get(i);

            try {

                Long versionId = JsonUtils.getLong(result, "id");

                Boolean active = JsonUtils.getBoolean(result, "active");

                String comment = JsonUtils.getString(result, "comment");

                Map<String, Object> pagesFirst =
                        JsonUtils.getMap(result, "pages_first");

                Long pageId =
                        JsonUtils.getLong(pagesFirst, "object_id");

                Boolean isOcrPerformed = false;

                if (pageId != null) {
                    isOcrPerformed =
                            mayanDocumentRepository.isOcrPerformed(pageId);
                }

                LocalDateTime createdAt =
                        JsonUtils.getLocalDateTime(result, "timestamp");

                // ✅ FIXED FILE ID
                Long fileId = null;

                if (files != null && i < files.size()) {

                    Map<String, Object> file = files.get(i);

                    fileId = JsonUtils.getLong(file, "id");
                }

                // fallback
                if (fileId == null) {
                    fileId = versionId;
                }

                DocumentDto documentDto = new DocumentDto();

                documentDto.setId(documentId);

                documentDto.setFileId(fileId);

                documentDto.setPageId(pageId);

                // ✅ FIXED DOCUMENT NAME
                if (dbDocument != null) {

                    documentDto.setDocumentName(
                            dbDocument.getDocumentName()
                    );

                    documentDto.setDocumentType(
                            dbDocument.getDocumentType()
                    );

                } else {

                    documentDto.setDocumentName("NOT Found");

                    documentDto.setDocumentType("NOT Found");
                }

                documentDto.setDescription(comment);

                documentDto.setIsActive(active);

                documentDto.setCreatedAt(createdAt);

                documentDto.setIsOcrPerformed(isOcrPerformed);

                documentDto.setMetadata(metadataMap);

                documentVersionDtos.add(documentDto);

            } catch (Exception ex) {

                log.error("Error processing version record: {}", result, ex);
            }
        }

        log.debug("Finished getDocumentVersions. Total DTOs: {}",
                documentVersionDtos.size());

        return documentVersionDtos;
    }

    // ------------------------------------------------------------------------
    // Get File List
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> getDocumentFiles(Long documentId) {

        int maxRetries = 5;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                        baseUrl + "/documents/" + documentId + "/files/?page=1&page_size=10&_ordering=-id",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

                return response.getBody() != null
                        ? response.getBody()
                        : Collections.emptyMap();

            } catch (HttpClientErrorException.TooManyRequests ex) {
                attempt++;

                if (attempt >= maxRetries) throw ex;

                try {
                    Thread.sleep(1000L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted", ie);
                }
            }
        }

        throw new RuntimeException("Failed after retries");
    }


    // ------------------------------------------------------------------------
    // Download File
    // ------------------------------------------------------------------------
    @Override
    public ResponseEntity<byte[]> downloadDocumentFile(Long documentId, Long fileId) {

        HttpHeaders headers = getHeaders();

        // ✅ CRITICAL FIX
        headers.setAccept(List.of(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            String url = baseUrl + "/documents/" + documentId + "/files/" + fileId + "/download/";

            log.debug("Downloading file from: {}", url);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    byte[].class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to download file: " + response.getStatusCode());
            }

            if (response.getBody() == null || response.getBody().length == 0) {
                throw new RuntimeException("Downloaded file is empty");
            }

            return response;

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Download API error: {} | Body: {}",
                    ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            throw new RuntimeException("Download failed", ex);

        } catch (Exception e) {
            log.error("Unexpected error during file download", e);
            throw new RuntimeException("Error while downloading file", e);
        }
    }

    // ------------------------------------------------------------------------
    // OCR Submit
    // ------------------------------------------------------------------------
    @Override
    public void submitOCR(Long documentId) {

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

        restTemplate.exchange(
                baseUrl + DOCUMENTS_API + documentId + "/ocr/submit/",
                HttpMethod.POST,
                entity,
                Void.class
        );
    }

    // ------------------------------------------------------------------------
    // OCR Content Text
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> getOCRText(Long documentId,
                                          Long versionId,
                                          Long pageId) {

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

        ResponseEntity<Object> response = restTemplate.exchange(
                baseUrl + "/documents/" + documentId +
                        "/versions/" + versionId +
                        "/pages/" + pageId + "/ocr/",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        Object body = response.getBody();

        if (body instanceof Map) {
            return (Map<String, Object>) body;
        } else if (body instanceof List) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) body;
            return list.isEmpty() ? Collections.emptyMap() : list.get(0);
        }

        return Collections.emptyMap();
    }

    // ------------------------------------------------------------------------
    // Get Document File Pages
    // ------------------------------------------------------------------------
    @Override
    public List<Map<String, Object>> getDocumentFilePages(Long documentId,
                                                          Long fileId) {

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

        ResponseEntity<Object> response = restTemplate.exchange(
                baseUrl + "/documents/" + documentId +
                        "/files/" + fileId + "/pages/",
                HttpMethod.GET,
                entity,
                Object.class
        );

        Object body = response.getBody();

        // ✅ CASE 1: PAGINATED OBJECT
        if (body instanceof Map) {

            Object results =
                    ((Map<?, ?>) body).get("results");

            if (results instanceof List) {
                return (List<Map<String, Object>>) results;
            }
        }

        // ✅ CASE 2: DIRECT ARRAY
        if (body instanceof List) {
            return (List<Map<String, Object>>) body;
        }

        return new ArrayList<>();
    }

    // ------------------------------------------------------------------------
    // Get Full Document OCR (All Pages)
    // ------------------------------------------------------------------------
    @Override
    public String getFullDocumentOCR(Long documentId, Long fileId) {

        Long versionId = fileId;

        List<Map<String, Object>> pages = getDocumentFilePages(documentId, fileId);

        StringBuilder fullOcrText = new StringBuilder();

        for (int i = 0; i < pages.size(); i++) {

            Map<String, Object> page = pages.get(i);

            Long pageId = JsonUtils.getLong(page, "id");

            Integer pageNumber = null;
            Object pageNumberObj = page.get("page_number");

            if (pageNumberObj != null) {
                pageNumber = ((Number) pageNumberObj).intValue();
            }

            if (pageNumber == null) {
                pageNumber = i + 1;
            }

            try {
                Map<String, Object> ocrResponse =
                        getOCRText(documentId, versionId, pageId);

                String content = JsonUtils.getString(ocrResponse, "content");

                fullOcrText.append("----- Page ")
                        .append(pageNumber)
                        .append(" -----\n");

                if (content != null && !content.isEmpty()) {
                    fullOcrText.append(content);
                } else {
                    fullOcrText.append("[No OCR content]");
                }

                fullOcrText.append("\n\n");

            } catch (Exception e) {
                log.error("OCR failed for page {}", pageId, e);

                fullOcrText.append("----- Page ")
                        .append(pageNumber)
                        .append(" -----\n")
                        .append("[Error fetching OCR]\n\n");
            }
        }

        return fullOcrText.toString();
    }

    // ------------------------------------------------------------------------
    // get document by process instance id
    // ------------------------------------------------------------------------
    @Override
    public
    List<DocumentDto> getDocumentsByProcessInstanceId(String processInstanceId) {
        return mayanDocumentRepository.getDocumentsByProcessInstanceId(processInstanceId);
    }

    // ------------------------------------------------------------------------
    // get documents with files
    // ------------------------------------------------------------------------
    @Override
    public List<Map<String, Object>> getDocumentsWithFilesByProcessInstanceId(String processInstanceId) {

        List<DocumentDto> documents =
                mayanDocumentRepository.getDocumentsByProcessInstanceId(processInstanceId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (DocumentDto doc : documents) {

            Map<String, Object> combined = new HashMap<>();

            // ============================================================
            // 🔹 STEP 1: FETCH METADATA FROM MAYAN
            // ============================================================
            Map<String, String> metadataMap = new HashMap<>();

            try {

                Map<String, Object> metadataResponse = getMetadata(doc.getId());

                if (metadataResponse != null) {

                    Object body = metadataResponse;

                    List<Map<String, Object>> metadataResults;

                    if (body instanceof Map) {

                        Object resultsObj =
                                ((Map<?, ?>) body).get("results");

                        metadataResults = resultsObj instanceof List
                                ? (List<Map<String, Object>>) resultsObj
                                : new ArrayList<>();

                    } else if (body instanceof List) {

                        metadataResults =
                                (List<Map<String, Object>>) body;

                    } else {

                        metadataResults = new ArrayList<>();
                    }

                    for (Map<String, Object> item : metadataResults) {

                        Map<String, Object> type =
                                (Map<String, Object>) item.get("metadata_type");

                        if (type == null) {
                            continue;
                        }

                        String label = (String) type.get("label");

                        String value = item.get("value") != null
                                ? item.get("value").toString()
                                : null;

                        if (label != null) {
                            metadataMap.put(label, value);
                        }
                    }
                }

            } catch (Exception ex) {

                log.error("Failed to fetch metadata for documentId={}",
                        doc.getId(),
                        ex);
            }

            // ============================================================
            // 🔹 STEP 2: SET DOCUMENT METADATA
            // ============================================================
            doc.setMetadata(metadataMap);

            // ============================================================
            // 🔹 STEP 3: FETCH VERSIONS
            // ============================================================
            List<DocumentDto> documentDtos = new ArrayList<>();

            try {

                documentDtos = getDocumentVersions(doc.getId());

            } catch (Exception ex) {

                log.error("Failed to fetch versions for documentId={}",
                        doc.getId(),
                        ex);
            }

            // ============================================================
            // 🔹 STEP 4: MERGE VERSION DETAILS INTO MAIN DOC
            // ============================================================
            if (documentDtos != null && !documentDtos.isEmpty()) {

                DocumentDto versionDto = documentDtos.get(0);

                doc.setVersion(versionDto.getVersion());

                doc.setIsActive(versionDto.getIsActive());

                doc.setUploadedBy(
                        versionDto.getMetadata() != null
                                ? versionDto.getMetadata().get("uploadedBy")
                                : null
                );

                doc.setPageId(versionDto.getPageId());

                doc.setIsOcrPerformed(versionDto.getIsOcrPerformed());

                // ✅ preserve already fetched metadata if version metadata empty
                if (versionDto.getMetadata() != null &&
                        !versionDto.getMetadata().isEmpty()) {

                    doc.setMetadata(versionDto.getMetadata());
                }
            }

            // ============================================================
            // 🔹 STEP 5: BUILD RESPONSE
            // ============================================================
            combined.put("document", doc);

            combined.put("versions", documentDtos);

            result.add(combined);
        }

        return result;
    }

    private void throttle() {

        log.info("Sleeping for throttle");

        try {

            Thread.sleep(700);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }

    // ------------------------------------------------------------------------
    // Add Comment to Document
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> addDocumentComment(Long documentId, String text) {

        HttpHeaders headers = getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("text", text);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl + "/documents/" + documentId + "/comments/",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    // ------------------------------------------------------------------------
    // Get Document Comments
    // ------------------------------------------------------------------------
    @Override
    public List<Map<String, Object>> getDocumentComments(Long documentId,
                                                         int page,
                                                         int pageSize) {

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

        String url = baseUrl + "/documents/" + documentId +
                "/comments/?page=" + page +
                "&page_size=" + pageSize +
                "&ordering=-datetime_created";

        ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Object.class
        );

        Object body = response.getBody();

        // ✅ CASE 1: PAGINATED OBJECT
        if (body instanceof Map) {

            Object results = ((Map<?, ?>) body).get("results");

            if (results instanceof List) {
                return (List<Map<String, Object>>) results;
            }
        }

        // ✅ CASE 2: DIRECT ARRAY
        if (body instanceof List) {
            return (List<Map<String, Object>>) body;
        }

        return Collections.emptyList();
    }

    // ------------------------------------------------------------------------
    // Update Document Comment
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> updateDocumentComment(Long documentId, Long commentId, String text) {

        HttpHeaders headers = getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("text", text);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Object> response = restTemplate.exchange(
                baseUrl + "/documents/" + documentId + "/comments/" + commentId + "/",
                HttpMethod.PUT,
                request,
                Object.class
        );

        Object responseBody = response.getBody();

        if (responseBody instanceof Map) {
            return (Map<String, Object>) responseBody;
        }

        return Collections.emptyMap();
    }

    // ------------------------------------------------------------------------
    // Delete Document Comment
    // ------------------------------------------------------------------------
    @Override
    public void deleteDocumentComment(Long documentId, Long commentId) {

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

        restTemplate.exchange(
                baseUrl + "/documents/" + documentId + "/comments/" + commentId + "/",
                HttpMethod.DELETE,
                entity,
                Void.class
        );
    }

    private String getEmailFromProcess(String processInstanceId) {

        Map<String, Object> variables =
                runtimeService.getVariables(processInstanceId);

        if (variables == null || variables.isEmpty()) {
            throw new RuntimeException("No variables found");
        }

        Object email = variables.get("email_id");

        if (email == null) {
            throw new RuntimeException("email_id not found");
        }

        return email.toString();
    }

    // ------------------------------------------------------------------------
    // Send Document Mail (Dispatch)
    // ------------------------------------------------------------------------
    @Override
    public void sendDocumentByEmail(Long documentId, Long fileId) {

        ResponseEntity<byte[]> response = downloadDocumentFile(documentId, fileId);

        byte[] fileData = response.getBody();

        if (fileData == null || fileData.length == 0) {
            throw new RuntimeException("File is empty");
        }

        String fileName = "document_" + documentId; // fallback

        try {
            if (response.getHeaders().getContentDisposition() != null) {
                String originalFileName =
                        response.getHeaders().getContentDisposition().getFilename();

                if (originalFileName != null && !originalFileName.isEmpty()) {
                    fileName = originalFileName;
                }
            }
        } catch (Exception e) {
            log.warn("Could not extract filename, using fallback");
        }

        String processInstanceId =
                mayanDocumentRepository.getProcessInstanceIdByDocumentId(documentId);

        if (processInstanceId == null) {
            throw new RuntimeException("ProcessInstanceId not found");
        }

        String email = getEmailFromProcess(processInstanceId);

        log.info("Sending file {} to {}", fileName, email);

        String subject = "Your Requested Document - " + fileName;

        String body = "Dear Customer,\n\n"
                + "We hope you are doing well.\n\n"
                + "Please find the document attachment below.\n"
                + "Document Name: " + fileName + "\n\n"
                + "\n"
                + "Best Regards,\n"
                + "DIB BANK";

        mailNotificationUtil.sendEmailWithAttachment(
                email,
                subject,
                body,
                fileData,
                fileName
        );
    }

    // ------------------------------------------------------------------------
    // Send Reminder Mail (Signature Approval)
    // ------------------------------------------------------------------------
    @Override
    public void sendReminderEmail(Long documentId, Long fileId) {

        ResponseEntity<byte[]> response = downloadDocumentFile(documentId, fileId);

        byte[] fileData = response.getBody();

        if (fileData == null || fileData.length == 0) {
            throw new RuntimeException("File is empty");
        }

        String fileName = "document_" + documentId;

        try {
            if (response.getHeaders().getContentDisposition() != null) {
                String originalFileName =
                        response.getHeaders().getContentDisposition().getFilename();

                if (originalFileName != null && !originalFileName.isEmpty()) {
                    fileName = originalFileName;
                }
            }
        } catch (Exception e) {
            log.warn("Could not extract filename, using fallback");
        }

        // Get Process Instance
        String processInstanceId =
                mayanDocumentRepository.getProcessInstanceIdByDocumentId(documentId);

        if (processInstanceId == null) {
            throw new RuntimeException("ProcessInstanceId not found");
        }

        // Get Customer Email
        String email = getEmailFromProcess(processInstanceId);

        log.info("Sending REMINDER for file {} to {}", fileName, email);

        // ✅ Different Subject
        String subject = "Reminder: Document Pending for Signature Approval";

        // ✅ Different Email Body
        String body = "Dear Customer,\n\n"
                + "This is a gentle reminder that your document need to be signed.\n\n"
                + "Document Name: " + fileName + "\n\n"
                + "Please review and sign the document at your earliest convenience.\n\n"
                + "If you have already completed this, please ignore this email.\n\n"
                + "Best Regards,\n"
                + "DIB BANK";

        // Send Email
        mailNotificationUtil.sendEmailWithAttachment(
                email,
                subject,
                body,
                fileData,
                fileName
        );

        // ✅ Save Notification in DB
        AppNotification notification = new AppNotification();
        notification.setProcessInstanceId(processInstanceId);
        notification.setReferenceNo(documentId.toString());
        notification.setNotification("Reminder sent for signature approval");
        VariableInstanceDto cifNumberVarInstance = processInstanceVariablesService.getProcessInstanceVariable(processInstanceId, "cif_number");
        if(cifNumberVarInstance != null) {
            notification.setSentTo(cifNumberVarInstance.getValue().toString());
        }
        notification.setSentFrom(securityUtil.getCurrentUserFullName()); // or logged-in user
        notification.setRead(false);
        notification.setDateTime(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    // ------------------------------------------------------------------------
    // Get Document type id by Name
    // ------------------------------------------------------------------------
    @Override
    public Long getDocumentTypeIdByName(String documentTypeName) {

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());
        String url = baseUrl + "/document_types/";

        while (url != null) {

            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );

            Object body = response.getBody();

            if (body == null) return 0L;

            List<Map<String, Object>> results = Collections.emptyList();
            String nextUrl = null;

            // ✅ HANDLE BOTH CASES
            if (body instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) body;

                Object res = map.get("results");
                if (res instanceof List) {
                    results = (List<Map<String, Object>>) res;
                }

                Object next = map.get("next");
                if (next != null) {
                    nextUrl = next.toString();
                }

            } else if (body instanceof List) {
                results = (List<Map<String, Object>>) body;
            }

            // 🔍 FIND MATCH
            for (Map<String, Object> docType : results) {
                String label = (String) docType.get("label");

                if (label != null && label.equalsIgnoreCase(documentTypeName)) {
                    return ((Number) docType.get("id")).longValue();
                }
            }

            url = nextUrl;
        }

        return 0L;
    }

    // ------------------------------------------------------------------------
    // Get Metadata type id by name
    // ------------------------------------------------------------------------
    @Override
    public Long getMetadataTypeIdByName(String metadataTypeName) {

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());
        String url = baseUrl + "/metadata_types/";

        while (url != null) {

            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );

            Object body = response.getBody();

            if (body == null) return 0L;

            List<Map<String, Object>> results = Collections.emptyList();
            String nextUrl = null;

            // ✅ HANDLE BOTH Map & List
            if (body instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) body;

                Object res = map.get("results");
                if (res instanceof List) {
                    results = (List<Map<String, Object>>) res;
                }

                Object next = map.get("next");
                if (next != null) {
                    nextUrl = next.toString();
                }

            } else if (body instanceof List) {
                results = (List<Map<String, Object>>) body;
            }

            // 🔍 SEARCH
            for (Map<String, Object> metadata : results) {
                String label = (String) metadata.get("label");

                if (label != null && label.equalsIgnoreCase(metadataTypeName)) {
                    return ((Number) metadata.get("id")).longValue();
                }
            }

            url = nextUrl;
        }

        return 0L;
    }

    // ------------------------------------------------------------------------
    // Get All Metadata type
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Long> getAllMetadataTypes() {

        log.info("Fetching all metadata types from Mayan");

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

        String url = baseUrl + "/metadata_types/?page_size=100";

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to fetch metadata types");
            }

            Object body = response.getBody();

            List<Map<String, Object>> results;

            if (body instanceof Map) {
                Object res = ((Map<?, ?>) body).get("results");
                results = res instanceof List
                        ? (List<Map<String, Object>>) res
                        : new ArrayList<>();
            } else if (body instanceof List) {
                results = (List<Map<String, Object>>) body;
            } else {
                results = new ArrayList<>();
            }

            Map<String, Long> metadataMap = new HashMap<>();

            for (Map<String, Object> item : results) {
                String name = (String) item.get("label");
                Number id = (Number) item.get("id"); // ✅ FIXED

                metadataMap.put(name, id.longValue());
            }

            return metadataMap;

        } catch (Exception e) {
            log.error("Error fetching metadata types", e);
            throw new RuntimeException("Error fetching metadata types", e);
        }
    }

    // ------------------------------------------------------------------------
    // Get All Metadata type with key value mapping
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Long> getAllDocumentTypesMapping() {

        log.info("Fetching all document types from Mayan for mapping");

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

        String url = baseUrl + "/document_types/?page_size=100";

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to fetch document types");
            }

            Object body = response.getBody();

            List<Map<String, Object>> results;

            if (body instanceof Map) {
                Object res = ((Map<?, ?>) body).get("results");
                results = res instanceof List
                        ? (List<Map<String, Object>>) res
                        : new ArrayList<>();
            } else if (body instanceof List) {
                results = (List<Map<String, Object>>) body;
            } else {
                results = new ArrayList<>();
            }

            Map<String, Long> documentTypeMap = new HashMap<>();

            for (Map<String, Object> item : results) {
                String label = (String) item.get("label");
                Number id = (Number) item.get("id"); // ✅ fixed

                documentTypeMap.put(label, id.longValue());
            }

            return documentTypeMap;

        } catch (Exception e) {
            log.error("Error fetching document types", e);
            throw new RuntimeException("Error fetching document types", e);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sleep interrupted", e);
        }
    }

    // ------------------------------------------------------------------------
    // Add metadata by name
    // ------------------------------------------------------------------------
    @Override
    public Map<String, Object> addMetadataByName(Long documentId, String metadataName, String value) {

        log.info("Adding metadata using name: {}", metadataName);

        // Resolve metadata type ID
        Long metadataTypeId = getMetadataTypeIdByName(metadataName);

        if (metadataTypeId == null || metadataTypeId <= 0) {
            log.error("Metadata type not found for name: {}", metadataName);
            throw new IllegalArgumentException("Metadata type not found: " + metadataName);
        }

        log.debug("Resolved metadataTypeId={} for name={}", metadataTypeId, metadataName);

        // Delegate to existing method
        return addMetadata(documentId, metadataTypeId, value);
    }
}