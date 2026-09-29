package com.client.custom.paycom.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response body from GET api/v1/newhireids.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaycomNewHireIdsResponse {

    private boolean result;
    private List<PaycomNewHireIdEntry> data;
    private List<String> errors;
    private Integer errorCount;
    private Integer records;
}
