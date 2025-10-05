
package org.jono.medicalmodelsservice.controller;

import static org.jono.medicalmodelsservice.utils.AuthenticationUtils.extractCompanyId;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jono.medicalmodelsservice.model.ModelRanking;
import org.jono.medicalmodelsservice.model.NamedUserRanking;
import org.jono.medicalmodelsservice.model.TotalResourceMetrics;
import org.jono.medicalmodelsservice.model.UserSupportSearchParams;
import org.jono.medicalmodelsservice.model.dto.UserDto;
import org.jono.medicalmodelsservice.model.dto.ViewCompanyDetailsDto;
import org.jono.medicalmodelsservice.service.AdminService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping(path = "/users/documents/ranking",
            produces = "application/json")
    @ResponseBody
    public List<NamedUserRanking> getUserDocumentRankings(final JwtAuthenticationToken authentication) {
        final String companyId = extractCompanyId(authentication,
                                                                      "query for user document creation rankings");
        return adminService.getUserDocumentCreatorRankings(companyId);
    }

    @GetMapping(path = "/users/comments/ranking",
            produces = "application/json")
    @ResponseBody
    public List<NamedUserRanking> getUserCommentRankings(final JwtAuthenticationToken authentication) {
        final String companyId = extractCompanyId(authentication,
                                                                      "query for user comment creation rankings");
        return adminService.getUserCommentCreatorRankings(companyId);
    }

    @GetMapping(path = "/company/models/ranking",
            produces = "application/json")
    @ResponseBody
    public List<ModelRanking> getModelRankings(final JwtAuthenticationToken authentication) {
        final String companyId = extractCompanyId(authentication,
                                                                      "query for company model rankings");
        return adminService.getModelTypeFrequency(companyId);
    }

    @GetMapping(path = "/company/documents/metrics",
            produces = "application/json")
    @ResponseBody
    public TotalResourceMetrics getCompanyDocumentMetrics(final JwtAuthenticationToken authentication) {
        final String companyId = extractCompanyId(authentication,
                                                                      "query for company document metrics");
        return adminService.getCompanyDocumentMetrics(companyId);
    }

    @GetMapping(path = "/company/comments/metrics",
            produces = "application/json")
    @ResponseBody
    public TotalResourceMetrics getCompanyCommentMetrics(final JwtAuthenticationToken authentication) {
        final String companyId = extractCompanyId(authentication,
                                                                      "query for company comment metrics");
        return adminService.getCompanyCommentMetrics(companyId);
    }

    @PostMapping(path = "/companies/users/search",
            produces = "application/json")
    @ResponseBody
    public List<UserDto> searchUsers(@RequestBody final UserSupportSearchParams searchParams,
            final JwtAuthenticationToken authentication) {
        final String companyId = extractCompanyId(authentication, "search for users");
        return adminService.searchUsersWithCompanyIdAndName(companyId, searchParams);
    }

    @GetMapping(path = "/company/details",
            produces = "application/json")
    @ResponseBody
    public ViewCompanyDetailsDto getCompanyDetails(final JwtAuthenticationToken authentication) {
        final String companyId = extractCompanyId(authentication, "retrieve company details");
        return adminService.getCompany(companyId);
    }
}
