package org.jono.medicalmodelsservice.service;

import static org.jono.medicalmodelsservice.utils.DtoAdapters.userToDto;
import static org.jono.medicalmodelsservice.utils.SearchParamUtils.isSet;
import static org.jono.medicalmodelsservice.utils.SearchParamUtils.notSet;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jono.medicalmodelsservice.model.Company;
import org.jono.medicalmodelsservice.model.CompanySupportSearchParams;
import org.jono.medicalmodelsservice.model.ModelRanking;
import org.jono.medicalmodelsservice.model.TotalResourceMetrics;
import org.jono.medicalmodelsservice.model.User;
import org.jono.medicalmodelsservice.model.UserSupportSearchParams;
import org.jono.medicalmodelsservice.model.dto.CompanyDto;
import org.jono.medicalmodelsservice.model.dto.UserDto;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRepository;
import org.jono.medicalmodelsservice.repository.jdbc.CompanyRepository;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentRepository;
import org.jono.medicalmodelsservice.repository.jdbc.UserRepository;
import org.jono.medicalmodelsservice.utils.DtoAdapters;
import org.springframework.stereotype.Service;

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

    public List<CompanyDto> searchCompaniesWithParams(final CompanySupportSearchParams searchParams) {
        return DtoAdapters.companyToDto(searchCompanies(searchParams));
    }

    private List<Company> searchCompanies(final CompanySupportSearchParams searchParams) {
        if (notSet(searchParams.nameSearchTerm()) && notSet(searchParams.locationStateFilter())) {
            return companyRepository.findAll();
        } else if (isSet(searchParams.nameSearchTerm()) && isSet(searchParams.locationStateFilter())) {
            return companyRepository.findByNameAndState(searchParams.nameSearchTerm(),
                                                        searchParams.locationStateFilter());
        } else if (isSet(searchParams.nameSearchTerm())) {
            return companyRepository.findByName(searchParams.nameSearchTerm());
        } else {
            return companyRepository.findByState(searchParams.locationStateFilter());
        }
    }

    public List<UserDto> searchUsersWithParams(final UserSupportSearchParams searchParams) {
        return userToDto(searchUsers(searchParams));
    }

    private List<User> searchUsers(final UserSupportSearchParams searchParams) {
        if (isSet(searchParams.nameSearchTerm())) {
            return userRepository.findByName(searchParams.nameSearchTerm());
        }
        return userRepository.findAll();
    }

}
