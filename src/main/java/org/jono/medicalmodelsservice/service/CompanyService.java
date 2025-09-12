package org.jono.medicalmodelsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Company;
import org.jono.medicalmodelsservice.model.dto.ViewCompanyDetailsDto;
import org.jono.medicalmodelsservice.repository.jdbc.CompanyRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public ViewCompanyDetailsDto getCompany(final String companyId) {
        return companyToViewDto(companyRepository.findById(companyId));
    }

    private ViewCompanyDetailsDto companyToViewDto(final Company company) {
        return new ViewCompanyDetailsDto(company.getId(),
                                         company.getName(),
                                         company.getLocationState(),
                                         company.getLogoFilename());
    }
}
