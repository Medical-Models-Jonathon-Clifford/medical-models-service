package org.jono.medicalmodelsservice.controller;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.dto.UpdateDocumentDto;
import org.jono.medicalmodelsservice.model.dto.ViewDocumentDto;
import org.jono.medicalmodelsservice.service.document.DocumentService;
import org.jono.medicalmodelsservice.service.document.DocumentTree;
import org.jono.medicalmodelsservice.utils.AuthenticationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final AuthenticationUtils authUtils;

    @PostMapping(path = "/new",
            produces = "application/json")
    @ResponseBody
    public Document handleDocumentsNewPost(@RequestParam final Optional<String> parentId,
            final JwtAuthenticationToken authentication) {
        final String companyId = authUtils.extractCompanyId(authentication, "query for document tree");
        final String userId = authUtils.extractUserId(authentication, "query for document tree");
        return documentService.createDocument(parentId, companyId, userId);
    }

    @PutMapping(path = "/{id}",
            produces = "application/json")
    @ResponseBody
    public Document handleDocumentsPut(@PathVariable final String id,
            @RequestBody final UpdateDocumentDto updateDocumentDto) {
        return documentService.updateDocument(id, updateDocumentDto);
    }

    @GetMapping(path = "/{id}",
            produces = "application/json")
    @ResponseBody
    public ResponseEntity<ViewDocumentDto> handleDocumentsGet(@PathVariable final String id) {
        return documentService.readDocument(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping(path = "/all/navigation",
            produces = "application/json")
    @ResponseBody
    public List<DocumentTree> handleDocumentsGetAllNavigation(final JwtAuthenticationToken authentication) {
        final String companyId = authUtils.extractCompanyId(authentication, "query for document tree");
        return documentService.getAllNavigation(companyId);
    }
}
