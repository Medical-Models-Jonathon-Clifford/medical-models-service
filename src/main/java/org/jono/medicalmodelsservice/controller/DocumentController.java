package org.jono.medicalmodelsservice.controller;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentChild;
import org.jono.medicalmodelsservice.model.DocumentDto;
import org.jono.medicalmodelsservice.model.DocumentState;
import org.jono.medicalmodelsservice.model.NewDocNameDto;
import org.jono.medicalmodelsservice.model.NewDocument;
import org.jono.medicalmodelsservice.usecases.document.DocumentGraph;
import org.jono.medicalmodelsservice.usecases.document.DocumentNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final ConnectionFactory connectionFactory;

    @Autowired
    public DocumentController(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @PostMapping(produces = "application/json")
    @ResponseBody
    public Mono<Document> handleDocumentsPost(@RequestBody NewDocument newDocument) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
        Document document = new Document(newDocument);
        return template.insert(Document.class)
                .using(document);
    }

    @PostMapping(path = "/new",
            produces = "application/json")
    @ResponseBody
    public Mono<Document> handleDocumentsNewPost(@RequestParam Optional<String> parentId) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
        Document document = Document.draftDocument();
        Mono<Document> newDoc = template.insert(Document.class).using(document);
        return parentId
                .map(id ->
                        newDoc.flatMap(doc ->
                                template.insert(DocumentChild.class)
                                        .using(new DocumentChild(id, doc.getId()))
                                        .map(_ -> doc)))
                .orElse(newDoc);
    }

    @PutMapping(path = "/{id}",
            produces = "application/json")
    @ResponseBody
    public Mono<Document> handleDocumentsPut(@PathVariable String id,
                                             @RequestBody DocumentDto documentDto) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);

        if (documentDto.getState().equals(DocumentState.DRAFT)) {
            if (Objects.nonNull(documentDto.getTitle()) && Objects.nonNull(documentDto.getBody())) {
                documentDto.setState(DocumentState.ACTIVE);
            } else if (Objects.nonNull(documentDto.getTitle())) {
                documentDto.setState(DocumentState.NO_CONTENT);
            } else if (Objects.nonNull(documentDto.getBody())) {
                documentDto.setState(DocumentState.NO_TITLE);
            } else {
                throw new RuntimeException("Title and body are both null. It is unclear what was updated.");
            }
        }

        Map<SqlIdentifier, Object> updateMap = new LinkedHashMap<>();
        updateMap.put(SqlIdentifier.unquoted("title"), documentDto.getTitle());
        updateMap.put(SqlIdentifier.unquoted("body"), documentDto.getBody());
        updateMap.put(SqlIdentifier.unquoted("state"), documentDto.getState());

        return template.update(Document.class)
                .matching(query(where("id").is(documentDto.getId())))
                .apply(Update.from(updateMap))
                .then(
                        template.select(Document.class)
                                .matching(query(where("id").is(id)))
                                .one()
                );
    }

    @GetMapping(path = "/{id}",
            produces = "application/json")
    @ResponseBody
    public Mono<Document> handleDocumentsGet(@PathVariable String id) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
        return template.select(Document.class)
                .matching(query(where("id").is(id)))
                .one();
    }

    @GetMapping(path = "/all/navigation",
            produces = "application/json")
    @ResponseBody
    public Mono<List<DocumentNode>> handleDocumentsGetAllNavigation() {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);

        return template
                .select(DocumentChild.class)
                .all()
                .collectList()
                .zipWith(template.select(Document.class).all().collectList())
                .map(tuple -> new DocumentGraph(tuple.getT2(), tuple.getT1()).getTopLevelDocs());
    }
}
