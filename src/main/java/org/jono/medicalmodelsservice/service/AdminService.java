package org.jono.medicalmodelsservice.service;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.ModelRanking;
import org.jono.medicalmodelsservice.model.NamedUserRanking;
import org.jono.medicalmodelsservice.model.TotalResourceMetrics;
import org.jono.medicalmodelsservice.model.User;
import org.jono.medicalmodelsservice.model.UserIdRanking;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRepository;
import org.jono.medicalmodelsservice.repository.jdbc.CompanyRepository;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentRepository;
import org.jono.medicalmodelsservice.repository.jdbc.UserRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final CommentRepository commentRepository;

    public List<NamedUserRanking> getUserDocumentCreatorRankings(final String companyId) {
        final List<UserIdRanking> creatorFrequencyForDocument = documentRepository.getCreatorFrequencyForDocument(
                companyId);
        final List<String> userIds = creatorFrequencyForDocument.stream().map(UserIdRanking::userId).toList();
        final List<User> users = userRepository.findUsersForIds(userIds);
        final Map<String, User> idToUserMap = createIdToUserMap(users);
        return creatorFrequencyForDocument.stream()
                .map(userIdRanking ->
                             new NamedUserRanking(idToUserMap.get(userIdRanking.userId()).getName(),
                                                  userIdRanking.frequency()))
                .toList();
    }

    public List<NamedUserRanking> getUserCommentCreatorRankings(final String companyId) {
        final List<UserIdRanking> creatorFrequencyForComments = commentRepository.rankUsersByCommentsCreated(companyId);
        final List<String> userIds = creatorFrequencyForComments.stream().map(UserIdRanking::userId).toList();
        final List<User> users = userRepository.findUsersForIds(userIds);
        final Map<String, User> idToUserMap = createIdToUserMap(users);
        return creatorFrequencyForComments.stream()
                .map(userIdRanking ->
                             new NamedUserRanking(idToUserMap.get(userIdRanking.userId()).getName(),
                                                  userIdRanking.frequency()))
                .toList();
    }

    private Map<String, User> createIdToUserMap(final List<User> users) {
        return users.stream()
                .map(user -> Map.entry(user.getId(), user))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public List<ModelRanking> getModelTypeFrequency(final String companyId) {
        return documentRepository.getModelTypeFrequencyForCompany(companyId);
    }

    public TotalResourceMetrics getCompanyDocumentMetrics(final String companyId) {
        return new TotalResourceMetrics(documentRepository.countByCompany(companyId),
                                        documentRepository.getDocumentGrowthDataByCompany(companyId));
    }

    public TotalResourceMetrics getCompanyCommentMetrics(final String companyId) {
        return new TotalResourceMetrics(commentRepository.countByCompany(companyId),
                                        commentRepository.getCommentGrowthDataByCompany(companyId));
    }
}
