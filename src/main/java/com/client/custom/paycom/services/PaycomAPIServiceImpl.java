package com.client.custom.paycom.services;

import com.client.ApplicationSettings;
import com.client.custom.paycom.exception.PaycomApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
public class PaycomAPIServiceImpl implements PaycomAPIService {

    private static final String EMPLOYEE_DIRECTORY_ENDPOINT = "/api/v1/employeedirectory";
    private static final String NEW_HIRE_FIELD_OPTIONS_ENDPOINT = "/api/v1/newhire/fieldoptions";

    private final RestTemplate restTemplate;
    private final ApplicationSettings.Paycom paycomSettings;
    private final ObjectMapper paycomObjectMapper;

    @Autowired
    public PaycomAPIServiceImpl(RestTemplate restTemplate,
                                 ApplicationSettings applicationSettings,
                                 @Qualifier("paycomObjectMapper") ObjectMapper paycomObjectMapper) {
        this.restTemplate = restTemplate;
        this.paycomSettings = applicationSettings.paycom();
        this.paycomObjectMapper = paycomObjectMapper;
    }

    @Override
    public JsonNode getEmployeeDirectory() {
        return get(EMPLOYEE_DIRECTORY_ENDPOINT, "employee directory");
    }

    @Override
    public JsonNode getNewHireFieldOptions() {
        return get(NEW_HIRE_FIELD_OPTIONS_ENDPOINT, "new hire field options");
    }

    /**
     * GETs endpointPath and parses the response body as JSON. descriptor is only used for
     * logging/error messages.
     */
    private JsonNode get(String endpointPath, String descriptor) {
        String url = paycomSettings.baseUrl() + endpointPath;
        HttpEntity<Void> request = new HttpEntity<>(buildAuthHeaders());

        log.info("Calling Paycom {}: {}", descriptor, url);

        String responseBody;
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            responseBody = response.getBody();
            log.info("Paycom {} response status: {}", descriptor, response.getStatusCode());
        } catch (RestClientResponseException e) {
            log.error("Paycom rejected the request for {}: {} {}", descriptor, e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new PaycomApiException("Paycom rejected the request for " + descriptor,
                    HttpStatus.resolve(e.getRawStatusCode()), e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            log.error("Failed to call Paycom for {}: {}", descriptor, e.getMessage());
            throw new PaycomApiException("Failed to call Paycom for " + descriptor, e);
        }

        if (responseBody == null || responseBody.isBlank()) {
            throw new PaycomApiException("Paycom returned an empty response for " + descriptor);
        }

        try {
            return paycomObjectMapper.readTree(responseBody);
        } catch (Exception e) {
            throw new PaycomApiException("Could not parse Paycom's response for " + descriptor, null, responseBody, e);
        }
    }

    /**
     * Paycom authenticates via Basic Auth using the SID (username) and Token (password),
     * base64-encoded as "SID:Token" in the Authorization header.
     */
    private HttpHeaders buildAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(paycomSettings.sid(), paycomSettings.token());
        return headers;
    }
}
