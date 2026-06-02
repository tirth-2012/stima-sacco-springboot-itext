package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.DocumentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface DocumentService {

    Map<String, Object> createDocumentType(String label);

    Map<String, Object> getDocumentTypes();

    Map<String, Object> createMetadataType(String label, String name);

    Map<String, Object> addMetadataTypeToDocumentType(Long documentTypeId,
                                                      Long metadataTypeId,
                                                      boolean required);

    String uploadDocument(Long documentTypeId,
                          String label,
                          String description,
                          MultipartFile file);

    Map<String, Object> getAllDocuments(int page, int size);

    Map<String, Object> getDocumentById(Long id);

    Map<String, Object> addMetadata(Long documentId,
                                    Long metadataTypeId,
                                    String value);

    Map<String, Object> getMetadata(Long documentId);

    Map<String, Object> createCabinet(String label, Long parentId);

    void addDocumentToCabinet(Long cabinetId, Long documentId);

    void deleteDocument(Long id);

    Map<String, Object> uploadNewVersionDocumentFile(Long documentId,
                                           MultipartFile file,
                                           String comment,
                                           String actionName);

    List<DocumentDto> getDocumentVersions(Long documentId);

    Map<String, Object> getDocumentFiles(Long documentId);

    ResponseEntity<byte[]> downloadDocumentFile(Long documentId, Long fileId);

    void submitOCR(Long documentId);

    Map<String, Object> getOCRText(Long documentId,
                                   Long versionId,
                                   Long pageId);

    List<DocumentDto> getDocumentsByProcessInstanceId(String processInstanceId);

    List<Map<String, Object>> getDocumentsWithFilesByProcessInstanceId(String processInstanceId);

    List<Map<String, Object>> getDocumentFilePages(Long documentId, Long fileId);

    String getFullDocumentOCR(Long documentId, Long fileId);

    Map<String, Object> addDocumentComment(Long documentId, String text);

    List<Map<String, Object>> getDocumentComments(Long documentId, int page, int pageSize);

    Map<String, Object> updateDocumentComment(Long documentId, Long commentId, String text);

    void deleteDocumentComment(Long documentId, Long commentId);

    void sendDocumentByEmail(Long documentId, Long fileId);

    void sendReminderEmail(Long documentId, Long fileId);

    void addMultipleMetadata(Long documentId, Map<String, String> metadataMap);

    Long getDocumentTypeIdByName(String documentTypeName);

    Long getMetadataTypeIdByName(String metadataTypeName);

    Map<String, Long> getAllMetadataTypes();

    Map<String, Long> getAllDocumentTypesMapping();

    Map<String, Object> addMetadataByName(Long documentId, String metadataName, String value);

    Map<String, Object> updateMetadata(Long documentId,
                                       Long metadataId,
                                       String value);

    void updateMultipleMetadata(Long documentId,
                                Map<String, String> metadataMap);

}