package com.client.custom.paycom.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One entry from GET api/v1/employeeids/employeechanges - one employee (eecode) and how many
 * field changes they had in the requested date range, e.g. {"eecode": "A032", "changescount": 18}.
 * <p>
 * changescount is explicitly @JsonProperty-annotated since it's plain concatenated lowercase, not
 * snake_case - paycomObjectMapper's SNAKE_CASE strategy would otherwise look for "changes_count"
 * and leave it null.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaycomEmployeeChangeEntry {

    @JsonProperty("eecode")
    private String eecode;

    @JsonProperty("changescount")
    private Integer changesCount;
}
