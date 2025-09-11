package org.jono.medicalmodelsservice.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.ModelRanking;
import org.jono.medicalmodelsservice.model.NamedUserRanking;
import org.jono.medicalmodelsservice.model.TotalResourceMetrics;
import org.jono.medicalmodelsservice.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(final AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(path = "/users/documents/ranking",
            produces = "application/json")
    @ResponseBody
    public List<NamedUserRanking> getUserDocumentRankings(final JwtAuthenticationToken authentication) {
        if (authentication.getToken().getClaims().get("companyId") instanceof String companyId) {
            return adminService.getUserDocumentCreatorRankings(companyId);
        }
        throw new IllegalArgumentException("companyId is required to query for user document creation rankings.");
    }

    @GetMapping(path = "/users/comments/ranking",
            produces = "application/json")
    @ResponseBody
    public List<NamedUserRanking> getUserCommentRankings(final JwtAuthenticationToken authentication) {
        if (authentication.getToken().getClaims().get("companyId") instanceof String companyId) {
            return adminService.getUserCommentCreatorRankings(companyId);
        }
        throw new IllegalArgumentException("companyId is required to query for user comment creation rankings.");
    }

    @GetMapping(path = "/company/models/ranking",
            produces = "application/json")
    @ResponseBody
    public List<ModelRanking> getModelRankings(final JwtAuthenticationToken authentication) {
        if (authentication.getToken().getClaims().get("companyId") instanceof String companyId) {
            return adminService.getModelTypeFrequency(companyId);
        }
        throw new IllegalArgumentException("companyId is required to query for company model rankings.");
    }

    @GetMapping(path = "/company/documents/metrics",
            produces = "application/json")
    @ResponseBody
    public TotalResourceMetrics getCompanyDocumentMetrics(final JwtAuthenticationToken authentication) {
        if (authentication.getToken().getClaims().get("companyId") instanceof String companyId) {
            return adminService.getCompanyDocumentMetrics(companyId);
        }
        throw new IllegalArgumentException("companyId is required to query for company document metrics.");
    }

    @GetMapping(path = "/company/comments/metrics",
            produces = "application/json")
    @ResponseBody
    public TotalResourceMetrics getCompanyCommentMetrics(final JwtAuthenticationToken authentication) {
        if (authentication.getToken().getClaims().get("companyId") instanceof String companyId) {
            return adminService.getCompanyCommentMetrics(companyId);
        }
        throw new IllegalArgumentException("companyId is required to query for company comment metrics.");
    }
}
