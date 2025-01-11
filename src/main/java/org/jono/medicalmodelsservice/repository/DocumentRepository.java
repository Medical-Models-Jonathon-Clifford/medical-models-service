package org.jono.medicalmodelsservice.repository;

import io.r2dbc.spi.ConnectionFactory;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentChild;
import org.jono.medicalmodelsservice.model.dto.DocumentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Map;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Repository
public class DocumentRepository {

    private final R2dbcEntityTemplate template;

    @Autowired
    public DocumentRepository(ConnectionFactory connectionFactory) {
        this.template = new R2dbcEntityTemplate(connectionFactory);
    }

    public Mono<Document> create(Document document) {
        return template.insert(Document.class).using(document);
    }

    public Mono<Document> findById(String id) {
        return template.select(Document.class)
                .matching(query(where("id").is(id)))
                .one();
    }

    public Mono<Document> updateById(String id, DocumentDto documentDto, Map<SqlIdentifier, Object> updateMap) {
        return template.update(Document.class)
                .matching(query(where("id").is(documentDto.getId())))
                .apply(Update.from(updateMap))
                .then(
                        template.select(Document.class)
                                .matching(query(where("id").is(id)))
                                .one()
                );
    }

    public Mono<Tuple2<List<DocumentChild>, List<Document>>> getDocsAndDocChildren() {
        return template
                .select(DocumentChild.class)
                .all()
                .collectList()
                .zipWith(template.select(Document.class).all().collectList());
    }
}
