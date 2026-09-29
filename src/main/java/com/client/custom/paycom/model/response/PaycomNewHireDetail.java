package com.client.custom.paycom.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One entry from GET api/v1/newhire/{id} - only the fields this integration currently uses.
 * Paycom returns dozens more (address, supervisors, position, etc.) which paycomObjectMapper
 * ignores (FAIL_ON_UNKNOWN_PROPERTIES=false) - add fields here as they're needed.
 * <p>
 * Unlike the newhireids/createnewhire endpoints, this endpoint's JSON keys are plain camelCase
 * (firstName, newHireId, ...), not snake_case - every field is explicitly @JsonProperty-annotated
 * so paycomObjectMapper's SNAKE_CASE strategy (which would otherwise look for "first_name",
 * "new_hire_id", etc.) doesn't silently leave them null.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaycomNewHireDetail {

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("newHireId")
    private Integer newHireId;

    /** The Paycom employee code (eecode) assigned once the new hire is created. */
    @JsonProperty("newEmployeeCode")
    private String newEmployeeCode;

    @JsonProperty("personalEmail")
    private String personalEmail;
}
