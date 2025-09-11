package org.jono.medicalmodelsservice.repository.jdbc;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.DailyResourceCount;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentRelationship;
import org.jono.medicalmodelsservice.model.ModelRanking;
import org.jono.medicalmodelsservice.model.Tuple2;
import org.jono.medicalmodelsservice.model.dto.DocumentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class DocumentRepository {

    private final DocumentCrudRepository documentCrudRepository;
    private final DocumentRelationshipCrudRepository documentRelationshipCrudRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DocumentRepository(
            final DocumentCrudRepository documentCrudRepository,
            final DocumentRelationshipCrudRepository documentRelationshipCrudRepository,
            final JdbcTemplate jdbcTemplate
    ) {
        this.documentCrudRepository = documentCrudRepository;
        this.documentRelationshipCrudRepository = documentRelationshipCrudRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public long count() {
        return documentCrudRepository.count();
    }

    public Document create(final Document document) {
        return documentCrudRepository.save(document);
    }

    public Optional<Document> findById(final String id) {
        return documentCrudRepository.findById(id);
    }

    public Document updateById(final String id, final DocumentDto documentDto) {
        final Document existingDocument = documentCrudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document with id " + id + " not found"));

        if (documentDto.getTitle() != null) {
            existingDocument.setTitle(documentDto.getTitle());
        }
        if (documentDto.getBody() != null) {
            existingDocument.setBody(documentDto.getBody());
        }
        if (documentDto.getState() != null) {
            existingDocument.setState(documentDto.getState());
        }
        return documentCrudRepository.save(existingDocument);
    }

    public Tuple2<List<DocumentRelationship>, List<Document>> getDocsAndDocRelationships(final String companyId) {
        final List<DocumentRelationship> documentRelationships = ImmutableList.copyOf(
                documentRelationshipCrudRepository.findAll());
        final List<Document> documents = ImmutableList.copyOf(documentCrudRepository.findByCompanyId(companyId));
        return new Tuple2<>(documentRelationships, documents);
    }

    public List<DailyResourceCount> getDocumentGrowthData() {
        final String sql = """
                           WITH RECURSIVE dates AS (
                               SELECT CAST(MIN(created_date) AS DATE) as date
                               FROM document
                           
                               UNION ALL
                           
                               SELECT DATE_ADD(date, INTERVAL 1 DAY)
                               FROM dates
                               WHERE date < CURRENT_DATE()
                           ),
                           daily_counts AS (
                               SELECT 
                                   CAST(created_date AS DATE) as date,
                                   COUNT(*) as daily_count
                               FROM document
                               GROUP BY CAST(created_date AS DATE)
                           )
                           SELECT 
                               d.date,
                               COALESCE(dc.daily_count, 0) as new_documents,
                               SUM(COALESCE(dc.daily_count, 0)) OVER (ORDER BY d.date) as total_documents
                           FROM dates d
                           LEFT JOIN daily_counts dc ON d.date = dc.date
                           ORDER BY d.date
                           """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            log.debug("Mapping row: {} {} {}",
                      rs.getDate("date"),
                      rs.getLong("new_documents"),
                      rs.getLong("total_documents"));

            return new DailyResourceCount(
                    rs.getDate("date").toLocalDate(),
                    rs.getLong("new_documents"),
                    rs.getLong("total_documents")
            );
        });
    }

    public List<ModelRanking> getModelTypeFrequency() {
        final String sql = """
                           WITH RECURSIVE numbers AS (
                               SELECT 0 AS n
                               UNION ALL
                               SELECT n + 1 FROM numbers WHERE n < (SELECT MAX(JSON_LENGTH(body)) FROM document)
                           ),
                                          json_types AS (
                                              SELECT JSON_UNQUOTE(
                                                           JSON_EXTRACT(d.body, CONCAT('$[', n.n, '].type'))
                                                     ) as type
                                              FROM document d
                                                       CROSS JOIN numbers n
                                              WHERE JSON_EXTRACT(d.body, CONCAT('$[', n.n, '].type')) IS NOT NULL
                                          )
                           SELECT
                               type,
                               COUNT(*) as frequency
                           FROM json_types
                           GROUP BY type
                           ORDER BY frequency DESC;
                           """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            log.debug("Mapping row: {} {}",
                      rs.getString("type"),
                      rs.getLong("frequency"));

            return new ModelRanking(
                    rs.getString("type"),
                    rs.getLong("frequency")
            );
        });
    }
}
