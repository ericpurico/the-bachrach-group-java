package com.client.custom.services;

import com.bullhornsdk.data.model.entity.core.standard.Candidate;
import com.bullhornsdk.data.model.entity.core.standard.Placement;

public interface BusinessService {
    String createNewHire(Integer placementId);
    Candidate findCandidateByEeCode(String eeCode);

    Candidate findCandidateByNewHireId(String newHireId);
}
