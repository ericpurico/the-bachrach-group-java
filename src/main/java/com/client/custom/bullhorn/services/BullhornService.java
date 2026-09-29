package com.client.custom.bullhorn.services;

import com.bullhornsdk.data.model.entity.core.standard.Candidate;
import com.bullhornsdk.data.model.entity.core.standard.Placement;

import java.util.List;

public interface BullhornService {
    Placement getPlacementById(Integer placementId);

    Candidate getCandidateById(Integer candidateId);

    void addIssue(Integer candidateId, Integer placementId, String message, Object jsonPayload);

    void updateCandidate(Candidate candidate);

    List<Candidate> searchCandidate(String query);
}
