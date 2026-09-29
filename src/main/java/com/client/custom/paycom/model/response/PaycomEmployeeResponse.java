package com.client.custom.paycom.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response body from GET api/v1/employee/{employeeCode} - data is a single-element array (one
 * employee per code looked up).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaycomEmployeeResponse {

    private boolean result;
    private List<PaycomEmployeeDetail> data;
    private List<String> errors;
    private Integer errorCount;
    private Integer records;
}
