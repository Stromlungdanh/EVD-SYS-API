package com.lotte.evdsys.report;

import com.lotte.evdsys.document.DocumentStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DocumentReportJdbcRepository {

    private static final String COUNT_BY_STATUS_SQL = """
            SELECT status, COUNT(*) AS total
            FROM documents
            GROUP BY status
            ORDER BY status
            """;

    private final JdbcTemplate jdbcTemplate;

    public DocumentReportJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DocumentStatusCountResponse> countByStatus() {
        return jdbcTemplate.query(COUNT_BY_STATUS_SQL, (resultSet, rowNumber) ->
                new DocumentStatusCountResponse(
                        DocumentStatus.valueOf(resultSet.getString("status")),
                        resultSet.getLong("total")
                )
        );
    }
}
