package com.client.custom.bullhorn.model;

import java.util.List;

/**
 * Body for Bullhorn's services/IssueReport PUT endpoint - not exposed by the sdk-rest SDK, so
 * BullhornServiceImpl.addIssue calls it directly against the session's own REST URL/token
 * (BullhornData.getRestUrl()/getBhRestToken()). Ported from a working JS implementation.
 */
public record BullhornIssueReportRequest(
        List<BullhornIssue> issues
) {
}
