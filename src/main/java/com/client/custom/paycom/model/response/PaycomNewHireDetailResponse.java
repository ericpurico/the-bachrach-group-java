package com.client.custom.paycom.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response body from GET api/v1/newhire/{id} - data is a single-element array (one new hire per
 * id looked up).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaycomNewHireDetailResponse {

    private boolean result;
    private List<PaycomNewHireDetail> data;
    private List<String> errors;
    private Integer errorCount;
    private Integer records;
}
