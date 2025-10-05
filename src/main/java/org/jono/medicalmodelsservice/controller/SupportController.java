package org.jono.medicalmodelsservice.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jono.medicalmodelsservice.model.CompanySupportSearchParams;
import org.jono.medicalmodelsservice.model.ModelRanking;
import org.jono.medicalmodelsservice.model.TotalResourceMetrics;
import org.jono.medicalmodelsservice.model.UserSupportSearchParams;
import org.jono.medicalmodelsservice.model.dto.CompanyDto;
import org.jono.medicalmodelsservice.model.dto.UserDto;
import org.jono.medicalmodelsservice.service.SupportService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/support")
public class SupportController {

    private final SupportService supportService;

    @GetMapping(path = "/companies/metrics",
            produces = "application/json")
    @ResponseBody
    public TotalResourceMetrics getTotalCompanyMetrics() {
        return supportService.getTotalCompanyMetrics();
    }

    @GetMapping(path = "/users/metrics",
            produces = "application/json")
    @ResponseBody
    public TotalResourceMetrics getTotalUserMetrics() {
        return supportService.getTotalUserMetrics();
    }

    @GetMapping(path = "/documents/metrics",
            produces = "application/json")
    @ResponseBody
    public TotalResourceMetrics getTotalDocumentMetrics() {
        return supportService.getTotalDocumentMetrics();
    }

    @GetMapping(path = "/comments/metrics",
            produces = "application/json")
    @ResponseBody
    public TotalResourceMetrics getTotalCommentMetrics() {
        return supportService.getTotalCommentMetrics();
    }

    @GetMapping(path = "/models/ranking",
            produces = "application/json")
    @ResponseBody
    public List<ModelRanking> getModelRankings() {
        return supportService.getModelTypeFrequency();
    }

    @PostMapping(path = "/companies/search",
            produces = "application/json")
    @ResponseBody
    public List<CompanyDto> searchCompanies(@RequestBody final CompanySupportSearchParams searchParams) {
        return supportService.searchCompaniesWithParams(searchParams);
    }

    @PostMapping(path = "/users/search",
            produces = "application/json")
    @ResponseBody
    public List<UserDto> searchUsers(@RequestBody final UserSupportSearchParams searchParams) {
        return supportService.searchUsersWithParams(searchParams);
    }
}
