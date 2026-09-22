package com.client.custom.bullhorn.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * One entry within BullhornIssueReportRequest.issues. actionEntity/actionEntityId are omitted
 * from the JSON entirely (not sent as null) when there's no specific entity the action relates to -
 * @JsonInclude(NON_NULL) below matches the JS source's conditional-spread behavior.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BullhornIssue(
        String action,
        String actionEntity,
        Integer actionEntityId,
        String externalSystemName,
        List<BullhornIssueItem> issueItems
) {
}
