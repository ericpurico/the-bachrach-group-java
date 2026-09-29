package com.client.custom.paycom.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One entry from GET api/v1/employeedirectory - only the core identity/status/contact fields.
 * Paycom also returns 40 more fields for labor allocation categories (cat1/cat1desc through
 * cat20/cat20desc) which are deliberately left unmapped for now - paycomObjectMapper ignores them
 * (FAIL_ON_UNKNOWN_PROPERTIES=false). Add them (or a collapsed List<PaycomLaborCategory>) if/when
 * this integration actually needs labor allocation data.
 * <p>
 * Every field is explicitly @JsonProperty-annotated since this endpoint's JSON keys don't follow
 * one consistent convention (eecode/firstname are plain concatenated lowercase, apt_suite_other/
 * preferred_first_name are snake_case) - paycomObjectMapper's SNAKE_CASE strategy would otherwise
 * mismatch the concatenated ones (e.g. expecting "first_name" instead of "firstname").
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaycomEmployeeDirectoryEntry {

    /** Paycom employee code - the id used elsewhere in this API (e.g. newEmployeeCode on create). */
    @JsonProperty("eecode")
    private String eecode;

    @JsonProperty("eename")
    private String employeeName;

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("streetaddr")
    private String streetAddress;

    @JsonProperty("apt_suite_other")
    private String aptSuiteOther;

    @JsonProperty("cityaddr")
    private String city;

    @JsonProperty("zipcode")
    private String zipCode;

    @JsonProperty("homestate")
    private String state;

    @JsonProperty("homephone")
    private String homePhone;

    @JsonProperty("homephone_country_code")
    private String homePhoneCountryCode;

    @JsonProperty("country_paid_in")
    private String countryPaidIn;

    /** Coded status, e.g. "A" (active), "T" (terminated). */
    @JsonProperty("eestatus")
    private String status;

    @JsonProperty("eebadge")
    private String badge;

    @JsonProperty("clockseq")
    private String clockSequence;

    @JsonProperty("deptcode")
    private String deptCode;

    @JsonProperty("deptdesc")
    private String deptDesc;

    @JsonProperty("preferred_first_name")
    private String preferredFirstName;

    @JsonProperty("preferred_last_name")
    private String preferredLastName;

    @JsonProperty("preferred_middle_name")
    private String preferredMiddleName;

    @JsonProperty("preferred_suffix")
    private String preferredSuffix;

    @JsonProperty("preferred_on_paystub")
    private Boolean preferredOnPaystub;
}
