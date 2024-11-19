package org.example.testtaskmega.controller;

import org.example.testtaskmega.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExternalApiController {

    @Autowired
    private ExternalApiService externalApiService;

    @GetMapping("/api/fetch-external")
    public String fetchExternalData() {
        externalApiService.fetchAndLogExternalData();
        return "Data fetched and logged. Check the logs!";
    }
}
