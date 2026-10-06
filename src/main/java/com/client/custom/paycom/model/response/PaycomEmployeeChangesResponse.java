package com.client.custom.paycom.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response body from GET api/v1/employeeids/employeechanges - one entry per employee who had a
 * change in the requested date range.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaycomEmployeeChangesResponse {

    private boolean result;
    private List<PaycomEmployeeChangeEntry> data;
    private List<String> errors;
    private Integer errorCount;
    private Integer records;
}
