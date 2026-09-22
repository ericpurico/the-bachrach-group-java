package com.client.custom.bullhorn.services;

import com.bullhornsdk.data.model.entity.core.standard.Candidate;
import com.bullhornsdk.data.model.entity.core.standard.Placement;

public interface BullhornService {
    Placement getPlacementById(Integer placementId);

    Candidate getCandidateById(Integer candidateId);

    void addIssue(Integer candidateId, Integer placementId, String message, Object jsonPayload);

}
