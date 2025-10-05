package org.jono.medicalmodelsservice.service;

import static org.jono.medicalmodelsservice.utils.DtoAdapters.companyToViewDto;

import lombok.RequiredArgsConstructor;
import org.jono.medicalmodelsservice.model.dto.ViewCompanyDetailsDto;
import org.jono.medicalmodelsservice.repository.jdbc.CompanyRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public ViewCompanyDetailsDto getCompany(final String companyId) {
        return companyToViewDto(companyRepository.findById(companyId));
    }
}
