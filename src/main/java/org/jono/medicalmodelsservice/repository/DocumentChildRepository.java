package org.jono.medicalmodelsservice.repository;

import io.r2dbc.spi.ConnectionFactory;
import org.jono.medicalmodelsservice.model.DocumentChild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class DocumentChildRepository {

    private final R2dbcEntityTemplate template;

    @Autowired
    public DocumentChildRepository(ConnectionFactory connectionFactory) {
        this.template = new R2dbcEntityTemplate(connectionFactory);
    }

    public Mono<DocumentChild> create(String parentId, String docId) {
        return template.insert(DocumentChild.class)
                .using(new DocumentChild(parentId, docId));
    }
}
