package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.DocumentDto;
import com.rutusoft.flowable.dto.MetadataRequestDTO;
import com.rutusoft.flowable.repository.MayanDocumentRepository;
import com.rutusoft.flowable.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Document APIs", description = "APIs for managing documents via Mayan EDMS")
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    private MayanDocumentRepository mayanDocumentRepository;
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }


    // ------------------------------------------------------------------------
    // Create Document Type
    // ------------------------------------------------------------------------
    @Operation(summary = "Create document type")
    @PostMapping("/types")
    public ResponseEntity<Map<String, Object>> createDocumentType(
            @RequestParam String label) {

        return ResponseEntity.ok(
                documentService.createDocumentType(label)
        );
    }

    // ------------------------------------------------------------------------
    // Get Document Types
    // ------------------------------------------------------------------------
    @Operation(summary = "Get all document types")
    @GetMapping("/types")
    public ResponseEntity<Map<String, Object>> getDocumentTypes() {
        return ResponseEntity.ok(documentService.getDocumentTypes());
    }

    // ------------------------------------------------------------------------
    // Create Metadata Type
    // ------------------------------------------------------------------------
    @Operation(summary = "Create metadata type")
    @PostMapping("/metadata-types")
    public ResponseEntity<Map<String, Object>> createMetadataType(
            @RequestParam String label,
            @RequestParam String name) {

        return ResponseEntity.ok(
                documentService.createMetadataType(label, name)
        );
    }

    // ------------------------------------------------------------------------
    // add metadata type to document type
    // ------------------------------------------------------------------------
    @Operation(summary = "Add metadata type to document type")
    @PostMapping("/types/{documentTypeId}/metadata-types")
    public ResponseEntity<Map<String, Object>> addMetadataTypeToDocumentType(
            @PathVariable Long documentTypeId,
            @RequestParam Long metadataTypeId,
            @RequestParam boolean required) {

        return ResponseEntity.ok(
                documentService.addMetadataTypeToDocumentType(
                        documentTypeId, metadataTypeId, required
                )
        );
    }

    // ------------------------------------------------------------------------
    // Upload Document
    // ------------------------------------------------------------------------
    @Operation(summary = "Upload document", description = "Uploads a document to Mayan EDMS")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(
            @RequestParam Long documentTypeId,
            @RequestParam String label,
            @RequestParam(required = false) String description,
            @RequestParam MultipartFile file) {

        return ResponseEntity.ok(
                documentService.uploadDocument(documentTypeId, label, description, file)
        );
    }

    // ------------------------------------------------------------------------
    // Get All Documents
    // ------------------------------------------------------------------------
    @Operation(summary = "Get all documents")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDocuments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(documentService.getAllDocuments(page, size));
    }

    // ------------------------------------------------------------------------
    // Get Document By ID
    // ------------------------------------------------------------------------
    @Operation(summary = "Get document by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    // ------------------------------------------------------------------------
    // Add Metadata to Document
    // ------------------------------------------------------------------------
    @Operation(summary = "Add metadata to document")
    @PostMapping("/{id}/metadata")
    public ResponseEntity<Map<String, Object>> addMetadata(
            @PathVariable Long id,
            @RequestParam Long metadataTypeId,
            @RequestParam String value) {

        return ResponseEntity.ok(
                documentService.addMetadata(id, metadataTypeId, value)
        );
    }

    // ------------------------------------------------------------------------
    // Add Multiple Metadata to Document
    // ------------------------------------------------------------------------
    @Operation(summary = "Add Multiple Metadata to Document")
    @PostMapping("/documents/{documentId}/metadata/bulk")
    public ResponseEntity<String> addBulkMetadata(
            @PathVariable Long documentId,
            @RequestBody MetadataRequestDTO request) {

        Map<String, String> metadataMap = new HashMap<>();
        metadataMap.put("processInstanceId", request.getProcessInstanceId());
        metadataMap.put("status", request.getStatus());
        metadataMap.put("uploadedBy", request.getUploadedBy());

        documentService.addMultipleMetadata(documentId, metadataMap);

        return ResponseEntity.ok("Metadata added successfully");
    }

    // ------------------------------------------------------------------------
    // Update Metadata (Partial)
    // ------------------------------------------------------------------------
    @Operation(summary = "Update metadata (PUT)")
    @PutMapping("/{documentId}/metadata/{metadataId}")
    public ResponseEntity<Map<String, Object>> updateMetadata(
            @PathVariable Long documentId,
            @PathVariable Long metadataId,
            @RequestParam String value
    ) {
        return ResponseEntity.ok(
                documentService.updateMetadata(documentId, metadataId, value)
        );
    }

    // ------------------------------------------------------------------------
    // Update Multiple Metadata
    // ------------------------------------------------------------------------
    @Operation(summary = "Update Multiple Metadata")
    @PutMapping("/{documentId}/metadata/bulk-update")
    public ResponseEntity<String> updateBulkMetadata(
            @PathVariable Long documentId,
            @RequestBody MetadataRequestDTO request) {

        Map<String, String> metadataMap = new HashMap<>();
        metadataMap.put("processInstanceId", request.getProcessInstanceId());
        metadataMap.put("status", request.getStatus());
        metadataMap.put("uploadedBy", request.getUploadedBy());

        documentService.updateMultipleMetadata(documentId, metadataMap);

        return ResponseEntity.ok("Metadata updated successfully");
    }

    // ------------------------------------------------------------------------
    // Get Document with Metadata
    // ------------------------------------------------------------------------
    @Operation(summary = "Get document with metadata")
    @GetMapping("/{id}/metadata")
    public ResponseEntity<Map<String, Object>> getMetadata(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getMetadata(id));
    }

    // ------------------------------------------------------------------------
    // Delete Document
    // ------------------------------------------------------------------------
    @Operation(summary = "Delete document")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok("Document deleted successfully");
    }

    // ------------------------------------------------------------------------
    // Create Cabinet (Folder)
    // ------------------------------------------------------------------------
    @Operation(summary = "Create cabinet")
    @PostMapping("/cabinets")
    public ResponseEntity<Map<String, Object>> createCabinet(
            @RequestParam String label,
            @RequestParam(required = false) Long parentId) {

        return ResponseEntity.ok(
                documentService.createCabinet(label, parentId)
        );
    }

    // ------------------------------------------------------------------------
    // Add Document to Cabinet
    // ------------------------------------------------------------------------
    @Operation(summary = "Add document to cabinet")
    @PostMapping("/cabinets/{cabinetId}/add-document")
    public ResponseEntity<String> addDocumentToCabinet(
            @PathVariable Long cabinetId,
            @RequestParam Long documentId) {

        documentService.addDocumentToCabinet(cabinetId, documentId);
        return ResponseEntity.ok("Document added to cabinet");
    }

    // ------------------------------------------------------------------------
    // Upload New Version File to Document
    // ------------------------------------------------------------------------
    @Operation(summary = "Upload New Version File to Document")
    @PostMapping("/{id}/files")
    public ResponseEntity<Map<String, Object>> uploadDocumentFile(
            @PathVariable Long id,
            @RequestParam MultipartFile file,
            @RequestParam(defaultValue = "replace") String actionName,
            @RequestParam(required = false) String comment) {

        return ResponseEntity.ok(
                documentService.uploadNewVersionDocumentFile(id, file, comment, actionName)
        );
    }

    // ------------------------------------------------------------------------
    // Get All Versions of a Document
    // ------------------------------------------------------------------------
    @Operation(summary = "Get all versions of a document")
    @GetMapping("/{id}/versions")
    public ResponseEntity<List<DocumentDto>> getDocumentVersions(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                documentService.getDocumentVersions(id)
        );
    }

    // ------------------------------------------------------------------------
    // Get Files
    // ------------------------------------------------------------------------
    @Operation(summary = "Get All Files")
    @GetMapping("/{id}/files")
    public ResponseEntity<Map<String, Object>> getFiles(@PathVariable Long id) {
        return ResponseEntity.ok(
                documentService.getDocumentFiles(id)
        );
    }

    /// ------------------------------------------------------------------------
    // Download File
    // ------------------------------------------------------------------------
    @Operation(summary = "Download Document")
    @GetMapping(
            value = "/{id}/files/{fileId}/download",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable Long id,
            @PathVariable Long fileId) {

        ResponseEntity<byte[]> response = documentService.downloadDocumentFile(id, fileId);

        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }

    // ------------------------------------------------------------------------
    // OCR Submit
    // ------------------------------------------------------------------------
    @Operation(summary = "Submit document for OCR")
    @PostMapping("/{id}/ocr")
    public ResponseEntity<String> submitOCR(@PathVariable Long id) {

        documentService.submitOCR(id);
        return ResponseEntity.ok("OCR submitted");
    }

    // ------------------------------------------------------------------------
    // OCR Content
    // ------------------------------------------------------------------------
    @Operation(summary = "Get OCR content")
    @GetMapping("/{id}/versions/{versionId}/pages/{pageId}/ocr")
    public ResponseEntity<Map<String, Object>> getOCR(
            @PathVariable Long id,
            @PathVariable Long versionId,
            @PathVariable Long pageId) {

        return ResponseEntity.ok(
                documentService.getOCRText(id, versionId, pageId)
        );
    }

    // ------------------------------------------------------------------------
    // Get Document File Pages
    // ------------------------------------------------------------------------
    @Operation(summary = "Get document file pages")
    @GetMapping("/{id}/files/{fileId}/pages")
    public ResponseEntity<List<Map<String, Object>>> getDocumentFilePages(
            @PathVariable Long id,
            @PathVariable Long fileId) {

        List<Map<String, Object>> response = documentService.getDocumentFilePages(id, fileId);

        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------------------------
    // Get Full Document OCR (Clean)
    // ------------------------------------------------------------------------
    @Operation(summary = "Get full document OCR")
    @GetMapping("/{id}/files/{fileId}/ocr-full")
    public ResponseEntity<String> getFullOCR(
            @PathVariable Long id,
            @PathVariable Long fileId) {

        String result = documentService.getFullDocumentOCR(id, fileId);

        return ResponseEntity.ok(result);
    }

    // ------------------------------------------------------------------------
    // Get all document by process instance id
    // ------------------------------------------------------------------------
    @Operation(summary = "Get All documents by process instance ID")
    @GetMapping("/by-process/{processInstanceId}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByProcessId(
            @PathVariable String processInstanceId) {

        return ResponseEntity.ok(
                documentService.getDocumentsByProcessInstanceId(processInstanceId)
        );
    }

    // ------------------------------------------------------------------------
    // get all documents with files
    // ------------------------------------------------------------------------
    @Operation(summary = "get all documents with files")
    @GetMapping("/by-process/{processInstanceId}/with-files")
    public ResponseEntity<List<Map<String, Object>>> getDocumentsWithFiles(
            @PathVariable String processInstanceId) {

        return ResponseEntity.ok(
                documentService.getDocumentsWithFilesByProcessInstanceId(processInstanceId)
        );
    }

    // ------------------------------------------------------------------------
    // Add Comment
    // ------------------------------------------------------------------------
    @Operation(summary = "Add comment to document")
    @PostMapping("/{id}/comments")
    public ResponseEntity<Map<String, Object>> addComment(
            @PathVariable Long id,
            @RequestParam String text) {

        return ResponseEntity.ok(
                documentService.addDocumentComment(id, text)
        );
    }

    // ------------------------------------------------------------------------
    // Get Comments
    // ------------------------------------------------------------------------
    @Operation(summary = "Get document comments")
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Map<String, Object>>> getDocumentComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        return ResponseEntity.ok(
                documentService.getDocumentComments(id, page, pageSize)
        );
    }

    // ------------------------------------------------------------------------
    // Update Comment
    // ------------------------------------------------------------------------
    @Operation(summary = "Update document comment")
    @PutMapping("/{id}/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> updateComment(
            @PathVariable Long id,
            @PathVariable Long commentId,
            @RequestParam String text) {

        return ResponseEntity.ok(
                documentService.updateDocumentComment(id, commentId, text)
        );
    }

    // ------------------------------------------------------------------------
    // Delete Comment
    // ------------------------------------------------------------------------
    @Operation(summary = "Delete document comment")
    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long id,
            @PathVariable Long commentId) {

        documentService.deleteDocumentComment(id, commentId);
        return ResponseEntity.ok("Comment deleted successfully");
    }

    // ------------------------------------------------------------------------
    // send document mail (dispatch)
    // ------------------------------------------------------------------------
    @Operation(summary = "send document mail")
    @PostMapping("/{id}/files/{fileId}/send-email")
    public ResponseEntity<String> sendDocumentEmail(
            @PathVariable Long id,
            @PathVariable Long fileId) {

        documentService.sendDocumentByEmail(id, fileId);
        return ResponseEntity.ok("Email sent successfully");
    }

    // ------------------------------------------------------------------------
    // Send Reminder Mail
    // ------------------------------------------------------------------------
    @Operation(summary = "Send reminder email for document signature")
    @PostMapping("/{id}/files/{fileId}/send-reminder")
    public ResponseEntity<String> sendReminderEmail(
            @PathVariable Long id,
            @PathVariable Long fileId) {

        documentService.sendReminderEmail(id, fileId);
        return ResponseEntity.ok("Reminder email sent successfully");
    }

    // ------------------------------------------------------------------------
    // Get Document type id by name
    // ------------------------------------------------------------------------
    @Operation(summary = "Get document type ID by name")
    @GetMapping("/document-type/id")
    public ResponseEntity<Long> getDocumentTypeId(
            @RequestParam String name) {

        Long documentTypeId = documentService.getDocumentTypeIdByName(name);
        return ResponseEntity.ok(documentTypeId);
    }

    // ------------------------------------------------------------------------
    // Get metadata type ID by name
    // ------------------------------------------------------------------------
    @Operation(summary = "Get metadata type ID by name")
    @GetMapping("/metadata-type/id")
    public ResponseEntity<Long> getMetadataTypeId(
            @RequestParam String name) {

        Long metadataTypeId = documentService.getMetadataTypeIdByName(name);
        return ResponseEntity.ok(metadataTypeId);
    }

    // ------------------------------------------------------------------------
    // Get all metadata types (ID + Name)
    // ------------------------------------------------------------------------
    @Operation(summary = "Get all metadata types as key-value")
    @GetMapping("/metadata-types")
    public ResponseEntity<Map<String, Long>> getAllMetadataTypes() {

        Map<String, Long> metadataTypes = documentService.getAllMetadataTypes();
        return ResponseEntity.ok(metadataTypes);
    }

    // ------------------------------------------------------------------------
    // Get all document types as key-value Mapping (label -> id)
    // ------------------------------------------------------------------------
    @Operation(summary = "Get all document types with mapping")
    @GetMapping("/document-types/mapping")
    public ResponseEntity<Map<String, Long>> getAllDocumentTypesMapping() {

        Map<String, Long> documentTypes = documentService.getAllDocumentTypesMapping();
        return ResponseEntity.ok(documentTypes);
    }

    // ------------------------------------------------------------------------
    // Add Metadata using name (no ID required)
    // ------------------------------------------------------------------------
    @Operation(summary = "Add metadata using metadata name")
    @PostMapping("/metadata/by-name")
    public ResponseEntity<Map<String, Object>> addMetadataByName(
            @RequestParam Long documentId,
            @RequestParam String metadataName,
            @RequestParam String value
    ) {
        return ResponseEntity.ok(
                documentService.addMetadataByName(documentId, metadataName, value)
        );
    }

    // ------------------------------------------------------------------------
    // Add Digital signature to the document
    // ------------------------------------------------------------------------
    @Operation(summary = "Add digital signature to document")
    @PostMapping("/{documentId}/sign/{fileId}")
    public ResponseEntity<?> signDocument(
            @PathVariable Long documentId,
            @PathVariable Long fileId) {

        documentService.digitallySignDocument(
                documentId,
                fileId
        );

        return ResponseEntity.ok(
                "Document signed successfully"
        );
    }
}