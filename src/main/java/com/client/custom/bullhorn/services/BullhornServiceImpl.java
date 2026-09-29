package com.client.custom.bullhorn.services;


import com.bullhornsdk.data.api.BullhornData;
import com.bullhornsdk.data.model.entity.core.standard.Candidate;
import com.bullhornsdk.data.model.entity.core.standard.Placement;
import com.bullhornsdk.data.model.parameter.SearchParams;
import com.bullhornsdk.data.model.parameter.standard.ParamFactory;
import com.client.custom.bullhorn.model.BullhornIssue;
import com.client.custom.bullhorn.model.BullhornIssueItem;
import com.client.custom.bullhorn.model.BullhornIssueReportRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
public class BullhornServiceImpl implements BullhornService{
    private final Set<String> placementFields = new HashSet<>(Arrays.asList("id","candidate(id,firstName,lastName,middleName,email,name,mobile,employeeType)","status","dateAdded", "dateBegin","dateEnd", "customText5","employeeType"));
    private final Set<String> candidateFields = new HashSet<>(Arrays.asList("id","firstName","lastName", "middleName","name","email","mobile","status","dateAdded","employeeType","customText20"));

    private final BullhornData bullhornData;
    private final RestTemplate restTemplate;

    private static final String ISSUE_ACTION = "Newbury Paycom Integration";
    private static final String ISSUE_ACTION_ENTITY = "Placement";
    private static final String ISSUE_EXTERNAL_SYSTEM_NAME = "Paycom";
    private static final String ISSUE_SOURCE_ENTITY = "Candidate";
    private static final String ISSUE_SEVERITY = "Error";
    private static final String ISSUE_ERROR_TYPE = "Error";

    public BullhornServiceImpl(BullhornData bullhornData, RestTemplate restTemplate){
        this.bullhornData = bullhornData;
        this.restTemplate = restTemplate;
    }

    @Override
    public Placement getPlacementById(Integer placementId) {
        return bullhornData.findEntity(Placement.class, placementId, placementFields);
    }

    @Override
    public Candidate getCandidateById(Integer candidateId) {
        return bullhornData.findEntity(Candidate.class, candidateId, candidateFields);
    }


    @Override
    public void addIssue(Integer candidateId, Integer placementId, String message, Object jsonPayload) {
        BullhornIssueItem issueItem = new BullhornIssueItem(ISSUE_SEVERITY, ISSUE_ERROR_TYPE, message, ISSUE_SOURCE_ENTITY, jsonPayload, candidateId);
        BullhornIssue issue = new BullhornIssue(ISSUE_ACTION, placementId != null ? ISSUE_ACTION_ENTITY : null, placementId, ISSUE_EXTERNAL_SYSTEM_NAME, List.of(issueItem));
        BullhornIssueReportRequest request = new BullhornIssueReportRequest(List.of(issue));

        try {
            String baseUrl = bullhornData.getRestUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            String token = UriUtils.encodeQueryParam(bullhornData.getBhRestToken(), StandardCharsets.UTF_8);
            String url = baseUrl + "/services/IssueReport?BhRestToken=" + token;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(request, headers), String.class);
        } catch (Exception e) {
            log.warn("addIssue: failed to report issue for placementId={}: {}", placementId, e.getMessage());
        }
    }

    @Override
    public void updateCandidate(Candidate candidate) {
        bullhornData.updateEntity(candidate);
    }

    @Override
    public List<Candidate> searchCandidate(String query) {
        SearchParams params = ParamFactory.searchParams();
        params.setCount(500);
        params.setSort("-id");
        params.setStart(0);
        return bullhornData.searchForList(Candidate.class, query, candidateFields, params);
    }


}
