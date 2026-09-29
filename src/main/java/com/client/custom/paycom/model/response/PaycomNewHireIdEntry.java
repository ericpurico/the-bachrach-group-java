package com.client.custom.paycom.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One entry from GET api/v1/newhireids, e.g.:
 * <pre>
 * {
 *   "new_hire_id": 2538542,
 *   "hireDate": null,
 *   "addedOnDate": "2026-09-09 15:25:26",
 *   "eecode": null,
 *   "status": "Pending New Hire"
 * }
 * </pre>
 * Paycom mixes naming conventions on this endpoint (new_hire_id is snake_case, hireDate/addedOnDate
 * are camelCase) - hireDate and addedOnDate are annotated explicitly since paycomObjectMapper's
 * SNAKE_CASE strategy would otherwise look for "hire_date"/"added_on_date" and silently leave them
 * null.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaycomNewHireIdEntry {

    @JsonProperty("new_hire_id")
    private Integer newHireId;

    /** Unconfirmed source format - only ever observed as null so far. */
    @JsonProperty("hireDate")
    private String hireDate;

    /**
     * "yyyy-MM-dd HH:mm:ss", no timezone marker. Treated as America/New_York - see
     * PaycomAPIServiceImpl.getRecentNewHireIds.
     */
    @JsonProperty("addedOnDate")
    private String addedOnDate;

    private String eecode;
    private String status;
}
