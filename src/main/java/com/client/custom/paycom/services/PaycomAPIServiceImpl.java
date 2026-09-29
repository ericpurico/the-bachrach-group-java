package com.client.custom.paycom.services;

import com.client.ApplicationSettings;
import com.client.custom.paycom.exception.PaycomApiException;
import com.client.custom.paycom.model.request.PaycomNewHire;
import com.client.custom.paycom.model.response.PaycomEmployeeDetail;
import com.client.custom.paycom.model.response.PaycomEmployeeDirectoryResponse;
import com.client.custom.paycom.model.response.PaycomEmployeeResponse;
import com.client.custom.paycom.model.response.PaycomNewHireDetail;
import com.client.custom.paycom.model.response.PaycomNewHireDetailResponse;
import com.client.custom.paycom.model.response.PaycomNewHireIdEntry;
import com.client.custom.paycom.model.response.PaycomNewHireIdsResponse;
import com.client.custom.paycom.model.response.PaycomNewHireResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class PaycomAPIServiceImpl implements PaycomAPIService {

    private static final String EMPLOYEE_DIRECTORY_ENDPOINT = "/api/v1/employeedirectory";
    private static final String NEW_HIRE_FIELD_OPTIONS_ENDPOINT = "/api/v1/newhire/fieldoptions";
    private static final String NEW_HIRE_CREATE_ENDPOINT = "/api/v1/newhire/createnewhire";
    private static final String EMPLOYEE_NEW_HIRE_ID_ENDPOINT = "/api/v1/newhireids";
    private static final String EMPLOYEE_NEW_HIRE_ENDPOINT = "/api/v1/newhire";

    private static final String EMPLOYEE_ENDPOINT = "/api/v1/employee";
    private static final Duration NEW_HIRE_FIELD_OPTIONS_CACHE_TTL = Duration.ofDays(1);

    // addedOnDate on the newhireids endpoint carries no timezone marker - treated as
    // America/New_York, matching the zone DateUtil already assumes for Paycom hire dates.
    private static final ZoneId PAYCOM_ZONE = ZoneId.of("America/New_York");
    private static final DateTimeFormatter ADDED_ON_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;
    private final ApplicationSettings.Paycom paycomSettings;
    private final ObjectMapper paycomObjectMapper;

    // New Hire field options change rarely, so they're cached in memory and refreshed once a day
    // rather than calling Paycom on every request.
    private JsonNode cachedNewHireFieldOptions;
    private Instant newHireFieldOptionsCachedAt;

    @Autowired
    public PaycomAPIServiceImpl(RestTemplate restTemplate,
                                 ApplicationSettings applicationSettings,
                                 @Qualifier("paycomObjectMapper") ObjectMapper paycomObjectMapper) {
        this.restTemplate = restTemplate;
        this.paycomSettings = applicationSettings.paycom();
        this.paycomObjectMapper = paycomObjectMapper;
    }



    @Override
    public PaycomEmployeeDirectoryResponse getEmployeeDirectory(Integer page, Integer pageSize) {
        if(page==null){
            page=1;
        }
        if(pageSize==null){
            pageSize=500;
        }
        String descriptor = "employee directory";
        JsonNode responseJson = get(EMPLOYEE_DIRECTORY_ENDPOINT+"?page="+page+"&pagesize="+pageSize, descriptor);

        try {
            return paycomObjectMapper.treeToValue(responseJson, PaycomEmployeeDirectoryResponse.class);
        } catch (Exception e) {
            throw new PaycomApiException("Could not parse Paycom's response for " + descriptor, null, responseJson.toString(), e);
        }
    }



    @Override
    public synchronized JsonNode getNewHireFieldOptions() {
        if (cachedNewHireFieldOptions == null || Instant.now().isAfter(newHireFieldOptionsCachedAt.plus(NEW_HIRE_FIELD_OPTIONS_CACHE_TTL))) {
            cachedNewHireFieldOptions = get(NEW_HIRE_FIELD_OPTIONS_ENDPOINT, "new hire field options");
            newHireFieldOptionsCachedAt = Instant.now();
        }
        return cachedNewHireFieldOptions;
    }

    @Override
    public JsonNode getNewHireIds() {
        return get(EMPLOYEE_NEW_HIRE_ID_ENDPOINT, "new hire id");
    }

    @Override
    public PaycomEmployeeDetail getEmployeeById(String employeeCode) {
        String descriptor = "employee code=" + employeeCode;
        JsonNode responseJson = get(EMPLOYEE_ENDPOINT + "/" + employeeCode, descriptor);

        PaycomEmployeeResponse employeeResponse;
        try {
            employeeResponse = paycomObjectMapper.treeToValue(responseJson, PaycomEmployeeResponse.class);
        } catch (Exception e) {
            throw new PaycomApiException("Could not parse Paycom's response for " + descriptor, null, responseJson.toString(), e);
        }

        if (employeeResponse.getData() == null || employeeResponse.getData().isEmpty()) {
            return null;
        }
        return employeeResponse.getData().get(0);
    }

    @Override
    public PaycomNewHireDetail getNewHireById(Integer newHireId) {
        String descriptor = "new hire id=" + newHireId;
        JsonNode responseJson = get(EMPLOYEE_NEW_HIRE_ENDPOINT + "/" + newHireId, descriptor);

        PaycomNewHireDetailResponse newHireDetailResponse;
        try {
            newHireDetailResponse = paycomObjectMapper.treeToValue(responseJson, PaycomNewHireDetailResponse.class);
        } catch (Exception e) {
            throw new PaycomApiException("Could not parse Paycom's response for " + descriptor, null, responseJson.toString(), e);
        }

        if (newHireDetailResponse.getData() == null || newHireDetailResponse.getData().isEmpty()) {
            return null;
        }
        return newHireDetailResponse.getData().get(0);
    }

    @Override
    public List<Integer> getRecentNewHireIds(Duration lookback) {
        JsonNode responseJson = get(EMPLOYEE_NEW_HIRE_ID_ENDPOINT, "new hire ids");

        PaycomNewHireIdsResponse newHireIdsResponse;
        try {
            newHireIdsResponse = paycomObjectMapper.treeToValue(responseJson, PaycomNewHireIdsResponse.class);
        } catch (Exception e) {
            throw new PaycomApiException("Could not parse Paycom's response for new hire ids", null, responseJson.toString(), e);
        }

        if (newHireIdsResponse.getData() == null) {
            return List.of();
        }

        LocalDateTime cutoff = LocalDateTime.now(PAYCOM_ZONE).minus(lookback);

        // addedOnDate's "yyyy-MM-dd HH:mm:ss" format sorts identically whether compared as a
        // String or parsed into a LocalDateTime, so sorting is done on the raw String rather than
        // parsing it a second time.
        return newHireIdsResponse.getData().stream()
                .filter(entry -> isAddedAfter(entry.getAddedOnDate(), cutoff))
                .sorted(Comparator.comparing(PaycomNewHireIdEntry::getAddedOnDate).reversed())
                .map(PaycomNewHireIdEntry::getNewHireId)
                .collect(Collectors.toList());
    }

    private boolean isAddedAfter(String addedOnDate, LocalDateTime cutoff) {
        if (addedOnDate == null || addedOnDate.isBlank()) {
            return false;
        }
        return LocalDateTime.parse(addedOnDate, ADDED_ON_DATE_FORMATTER).isAfter(cutoff);
    }

    @Override
    public PaycomNewHireResponse createNewHire(PaycomNewHire newHire) {
        String url = paycomSettings.baseUrl() + NEW_HIRE_CREATE_ENDPOINT;

        // Paycom's create endpoint takes an array of new hires - we only ever submit one per call.
        String requestJson;
        try {
            requestJson = paycomObjectMapper.writeValueAsString(List.of(newHire));
        } catch (JsonProcessingException e) {
            throw new PaycomApiException("Could not serialize the new hire request", e);
        }

        String descriptor = "new hire create for " + newHire.getFirstName() + " " + newHire.getLastName();
        log.info("Calling Paycom {}: {}", descriptor, requestJson);

        HttpHeaders headers = buildAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        String responseBody;
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
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

        PaycomNewHireResponse newHireResponse;
        try {
            newHireResponse = paycomObjectMapper.readValue(responseBody, PaycomNewHireResponse.class);
        } catch (Exception e) {
            throw new PaycomApiException("Could not parse Paycom's response for " + descriptor, null, responseBody, e);
        }

        // Paycom can accept the call (2xx) but still report the new hire itself as rejected in the
        // body - result=false or a populated errors[]/errorCount is that case, distinct from the
        // rejected-HTTP-status case already handled above via RestClientResponseException.
        boolean hasErrors = newHireResponse.getErrorCount() != null && newHireResponse.getErrorCount() > 0;
        if (!newHireResponse.isResult() || hasErrors) {
            log.error("Paycom reported errors for {}: {}", descriptor, responseBody);
            throw new PaycomApiException("Paycom reported errors for " + descriptor, null, responseBody);
        }

        return newHireResponse;
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
