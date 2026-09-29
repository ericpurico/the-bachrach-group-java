package com.client.custom.paycom.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response body from POST api/v1/newhire/createnewhire, e.g.:
 * <pre>
 * {
 *   "result": true,
 *   "data": { "new_hire_0": "Successfully Created New Hire." },
 *   "records": 1,
 *   "errors": [],
 *   "errorCount": 0
 * }
 * </pre>
 * data's keys are positional ("new_hire_0", "new_hire_1", ...) matching the index of each element
 * in the request array - since we only ever submit one new hire per call, data will only ever have
 * a "new_hire_0" entry.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaycomNewHireResponse {

    private boolean result;
    private Map<String, String> data;
    private Integer records;
    private List<String> errors;
    private Integer errorCount;
}
