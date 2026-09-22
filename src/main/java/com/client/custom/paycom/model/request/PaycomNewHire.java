package com.client.custom.paycom.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for POST api/v1/newhire/createnewhire. Serialized with paycomObjectMapper
 * (snake_case, nulls omitted), so an unset field is simply left out of the request rather than
 * sent as null/0/"".
 * <p>
 * ssn is deliberately not a field here - the new hire supplies it themselves in Paycom ESS, it is
 * never sent on create.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaycomNewHire {

    private String countryCode = "US";

    /** CODED - meaning not yet confirmed with Paycom. */
    private Integer employeeType;

    /** CONFIG - valid values come from GET newhire/fieldoptions. */
    private Integer templateId;

    /** CONFIG - valid values come from GET newhire/fieldoptions. */
    private String payrollProfile;

    private String firstName;
    private String lastName;
    private String personalEmail;

    /** Digits only, no formatting (e.g. "2125550147"). */
    private String primaryPhone;

    private String primaryPhoneIso2code="US";

    /** 1 = start self-onboarding. */
    private Integer sendInvite = 1;

    /** Optional. Paycom expects MM/DD/YYYY as a string here - NOT a Unix timestamp. */
    private String hireDate;
}
