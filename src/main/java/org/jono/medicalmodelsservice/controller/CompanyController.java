package org.jono.medicalmodelsservice.controller;

import lombok.RequiredArgsConstructor;
import org.jono.medicalmodelsservice.model.dto.ViewCompanyDetailsDto;
import org.jono.medicalmodelsservice.service.CompanyService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping(path = "/{companyId}",
            produces = "application/json")
    @ResponseBody
    public ViewCompanyDetailsDto getCompanyDetails(@PathVariable final String companyId) {
        return companyService.getCompany(companyId);
    }
}
