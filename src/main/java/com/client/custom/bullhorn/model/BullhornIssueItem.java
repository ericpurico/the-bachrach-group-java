package com.client.custom.bullhorn.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * One item within a BullhornIssue - see BullhornIssueReportRequest.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BullhornIssueItem(
        String severity,
        String errorType,
        String description,
        String sourceEntity,
        Object rawIssueItemPayload,
        Integer sourceEntityId
) {
}
