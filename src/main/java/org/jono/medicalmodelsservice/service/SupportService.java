package org.jono.medicalmodelsservice.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.ModelRanking;
import org.jono.medicalmodelsservice.model.TotalResourceMetrics;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRepository;
import org.jono.medicalmodelsservice.repository.jdbc.CompanyRepository;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentRepository;
import org.jono.medicalmodelsservice.repository.jdbc.UserRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupportService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final CommentRepository commentRepository;

    public TotalResourceMetrics getTotalCompanyMetrics() {
        return new TotalResourceMetrics(companyRepository.count(), companyRepository.getCompanyGrowthData());
    }

    public TotalResourceMetrics getTotalUserMetrics() {
        return new TotalResourceMetrics(userRepository.count(), userRepository.getUserGrowthData());
    }

    public TotalResourceMetrics getTotalDocumentMetrics() {
        return new TotalResourceMetrics(documentRepository.count(), documentRepository.getDocumentGrowthData());
    }

    public TotalResourceMetrics getTotalCommentMetrics() {
        return new TotalResourceMetrics(commentRepository.count(), commentRepository.getCommentGrowthData());
    }

    public List<ModelRanking> getModelTypeFrequency() {
        return documentRepository.getModelTypeFrequency();
    }
}
