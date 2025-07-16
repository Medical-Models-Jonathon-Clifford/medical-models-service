package org.jono.medicalmodelsservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/search")
public class SearchController {

  @GetMapping(produces = "application/json")
  @ResponseBody
  public String getCommentsForDocumentId() {
    return "{ \"key\": \"value\" }";
  }
}
