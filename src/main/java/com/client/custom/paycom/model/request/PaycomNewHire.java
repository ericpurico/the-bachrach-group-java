package com.client.custom.paycom.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One element of the request body for POST api/v1/newhire/createnewhire - Paycom takes an array
 * of new hires, but we only ever submit one element per call (one new hire per request), so
 * PaycomAPIService wraps a single instance of this in a List before sending it.
 * <p>
 * Serialized with paycomObjectMapper (snake_case, nulls omitted), so an unset field is simply left
 * out of the request rather than sent as null.
 * <p>
 * ssn is deliberately not a field here - the new hire supplies it themselves in Paycom ESS, it is
 * never sent on create.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaycomNewHire {

    @Builder.Default
    private String countryCode = "US";

    /** CODED - confirmed as a numeric string, e.g. "1". Meaning of each code not yet confirmed with Paycom. */
    private String employeeType;

    /** CONFIG - valid values come from GET newhire/fieldoptions. */
    private Integer templateId;

    private String firstName;
    private String lastName;
    private String middleName;
    private String personalEmail;

    /** Digits only, no formatting (e.g. "2125550147"). */
    private String primaryPhone;

    @Builder.Default
    private String primaryPhoneIso2code = "US";

    /** 1 = start self-onboarding. */
    @Builder.Default
    private Integer sendInvite = 1;

    /** 0/1 - whether to default the new hire's address to their assigned location's address. */
    private Integer useEmployeeAddress;

    /** Paycom company location id the new hire is assigned to. */
    private Integer locationId;

    /** Optional. Paycom expects MM/DD/YYYY as a string here - NOT a Unix timestamp. */
    private String hireDate;
}
