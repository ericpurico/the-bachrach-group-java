package com.client.custom.paycom.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response body from GET api/v1/employeedirectory. Paycom paginates this endpoint (206 Partial
 * Content when there are more results than pagesize) - errors/errorCount/records aren't shown in
 * every sample response but are included here for consistency with Paycom's other endpoints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaycomEmployeeDirectoryResponse {

    private boolean result;
    private List<PaycomEmployeeDirectoryEntry> data;
    private List<String> errors;
    private Integer errorCount;
    private Integer records;
}
