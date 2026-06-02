package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.dto.DocumentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
public class MayanDocumentRepository {

    private final JdbcTemplate jdbcTemplate;

    public MayanDocumentRepository(@Qualifier("secondaryJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✅ Query constants
    private static final String BASE_DOCUMENT_QUERY = """
        SELECT id, label, description, datetime_created
        FROM documents_document
        """;

    private static final String DOCUMENT_BY_PROCESS_ID_QUERY = """
            SELECT d.id, d.file_latest_id as fileId, d.label as document_name, dt.label as document_type, d.description as description, d.datetime_created, dm.value FROM documents_document d
                                                                                     		 JOIN metadata_documentmetadata dm on d.id = dm.document_id
                                                       									     JOIN documents_documenttype dt on d.document_type_id = dt.id
                                                                                     		 where dm.value = ?

        """;

    // ✅ Fetch limited documents (with pagination support)
    public List<DocumentDto> getDocs(int limit, int offset) {
        String sql = BASE_DOCUMENT_QUERY + " ORDER BY datetime_created DESC LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, DOCUMENT_ROW_MAPPER, limit, offset);
        } catch (DataAccessException ex) {
            log.error("Error fetching documents", ex);
            return Collections.emptyList();
        }
    }

    // ✅ Fetch ocer performace is tru or false
    public Boolean isOcrPerformed(Long pageId) {
        String sql = """
        SELECT EXISTS (
            SELECT 1 
            FROM ocr_documentversionpageocrcontent 
            WHERE document_version_page_id = ?
        )
    """;

        try {
            return jdbcTemplate.queryForObject(sql, Boolean.class, pageId);
        } catch (DataAccessException ex) {
            log.error("Error checking OCR for pageId={}", pageId, ex);
            return false;
        }
    }

    // ✅ Fetch by processInstanceId
    public List<DocumentDto> getDocumentsByProcessInstanceId(String processInstanceId) {
        try {
            return jdbcTemplate.query(
                    DOCUMENT_BY_PROCESS_ID_QUERY,
                    DOCUMENT_ROW_MAPPER,
                    processInstanceId
            );
        } catch (DataAccessException ex) {
            log.error("Error fetching documents for processInstanceId={}", processInstanceId, ex);
            return Collections.emptyList();
        }
    }

    // ✅ RowMapper (Reusable & Clean)
    private static final RowMapper<DocumentDto> DOCUMENT_ROW_MAPPER = new RowMapper<>() {
        @Override
        public DocumentDto mapRow(ResultSet rs, int rowNum) throws SQLException {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            log.info("----- Row " + rowNum + " -----");

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i); // or getColumnName(i)
                Object value = rs.getObject(i);

                log.info(columnName + " = " + value);
            }

            DocumentDto documentDto = new DocumentDto();
            documentDto.setId(rs.getLong("id"));
            documentDto.setFileId(rs.getLong("fileId"));
            documentDto.setDocumentName(rs.getString("document_name"));
            documentDto.setDocumentType(rs.getString("document_type"));
            documentDto.setDescription(rs.getString("description"));
            documentDto.setCreatedAt(rs.getTimestamp("datetime_created").toLocalDateTime());

            return documentDto;
        }
    };

    public String getProcessInstanceIdByDocumentId(Long documentId) {
        String sql = """
        SELECT dm.value 
        FROM metadata_documentmetadata dm
        WHERE dm.document_id = ?
        AND dm.metadata_type_id = 1
        LIMIT 1
    """;

        try {
            return jdbcTemplate.queryForObject(sql, String.class, documentId);
        } catch (Exception e) {
            log.error("ProcessInstanceId not found for documentId={}", documentId);
            return null;
        }
    }
}