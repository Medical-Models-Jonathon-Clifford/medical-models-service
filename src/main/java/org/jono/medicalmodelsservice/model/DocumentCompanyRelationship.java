package org.jono.medicalmodelsservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("document_company_relationship")
public class DocumentCompanyRelationship {
    @Id
    private String id;
    private String documentId;
    private String companyId;

    public DocumentCompanyRelationship(final String documentId, final String companyId) {
        this.documentId = documentId;
        this.companyId = companyId;
    }
}
