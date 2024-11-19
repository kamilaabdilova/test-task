package org.example.testtaskmega.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Service
public class ExternalApiService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalApiService.class);
    private final RestTemplate restTemplate;

    public ExternalApiService() {
        this.restTemplate = new RestTemplate();
    }

    public void fetchAndLogExternalData() {
        String url = "https://api.restful-api.dev/objects";

        try {
            // Выполняем GET-запрос
            String response = restTemplate.getForObject(url, String.class);

            // Логируем ответ
            logger.info("Response from External API: {}", response);
        } catch (RestClientException e) {
            // Логируем ошибку
            logger.error("Failed to fetch data from External API", e);
        }
    }
}
