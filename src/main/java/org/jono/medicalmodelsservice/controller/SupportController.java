package org.jono.medicalmodelsservice.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.ModelRanking;
import org.jono.medicalmodelsservice.model.TotalResourceMetrics;
import org.jono.medicalmodelsservice.service.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/support")
public class SupportController {

    private final SupportService supportService;

    @Autowired
    public SupportController(final SupportService supportService) {
        this.supportService = supportService;
    }

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
}
