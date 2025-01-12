package org.jono.medicalmodelsservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.dto.DocumentDto;
import org.jono.medicalmodelsservice.service.document.DocumentService;
import org.jono.medicalmodelsservice.service.document.DocumentNode;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(path = "/new",
            produces = "application/json")
    @ResponseBody
    public Mono<Document> handleDocumentsNewPost(@RequestParam Optional<String> parentId) {
        return documentService.createDocument(parentId);
    }

    @PutMapping(path = "/{id}",
            produces = "application/json")
    @ResponseBody
    public Mono<Document> handleDocumentsPut(@PathVariable String id,
                                             @RequestBody DocumentDto documentDto) {
        return documentService.updateDocument(id, documentDto);
    }

    @GetMapping(path = "/{id}",
            produces = "application/json")
    @ResponseBody
    public Mono<Document> handleDocumentsGet(@PathVariable String id) {
        return documentService.getDocumentById(id);
    }

    @GetMapping(path = "/all/navigation",
            produces = "application/json")
    @ResponseBody
    public Mono<List<DocumentNode>> handleDocumentsGetAllNavigation() {
        return documentService.getAllNavigation();
    }
}
