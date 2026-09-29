package com.client.custom.paycom.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One entry from GET api/v1/employee/{employeeCode} - Paycom returns well over 100 fields here
 * (emergency contacts, COBRA, 401k, EEOC/VETS reporting, cat1-20 labor allocation, secondary/
 * tertiary/quaternary supervisors, schedule/terminal group, etc.); only the identity, contact,
 * employment-status, and primary position/supervisor fields are mapped here. paycomObjectMapper
 * ignores the rest (FAIL_ON_UNKNOWN_PROPERTIES=false) - add more as they're actually needed.
 * <p>
 * Every field is explicitly @JsonProperty-annotated since this endpoint mixes naming conventions
 * (employee_code/hire_date are snake_case, but firstname/lastname/zipcode are concatenated and
 * companyLocationId/companyEstablishmentId are camelCase) - paycomObjectMapper's SNAKE_CASE
 * strategy alone would mismatch several of these.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaycomEmployeeDetail {

    @JsonProperty("employee_code")
    private String employeeCode;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("middlename")
    private String middleName;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("gender")
    private String gender;

    /** ISO-8601 with offset, e.g. "1918-05-04T00:00:00-05:00". */
    @JsonProperty("birth_date")
    private String birthDate;

    /** Coded status, e.g. "A" (active), "T" (terminated). */
    @JsonProperty("employee_status")
    private String employeeStatus;

    @JsonProperty("personal_email")
    private String personalEmail;

    @JsonProperty("work_email")
    private String workEmail;

    @JsonProperty("primary_phone")
    private String primaryPhone;

    @JsonProperty("primary_phone_country_code")
    private String primaryPhoneCountryCode;

    @JsonProperty("street")
    private String street;

    @JsonProperty("apt_suite_other")
    private String aptSuiteOther;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("zipcode")
    private String zipCode;

    @JsonProperty("country_paid_in")
    private String countryPaidIn;

    @JsonProperty("hire_date")
    private String hireDate;

    @JsonProperty("rehire_date")
    private String rehireDate;

    @JsonProperty("termination_date")
    private String terminationDate;

    @JsonProperty("employee_added")
    private String employeeAdded;

    @JsonProperty("new_hire")
    private Boolean newHire;

    @JsonProperty("department_code")
    private String departmentCode;

    @JsonProperty("department_description")
    private String departmentDescription;

    @JsonProperty("business_title")
    private String businessTitle;

    @JsonProperty("position_title")
    private String positionTitle;

    @JsonProperty("position_code")
    private String positionCode;

    @JsonProperty("location")
    private String location;

    @JsonProperty("pay_class")
    private String payClass;

    @JsonProperty("pay_frequency")
    private String payFrequency;

    @JsonProperty("supervisor_primary")
    private String supervisorPrimary;

    @JsonProperty("supervisor_primary_code")
    private String supervisorPrimaryCode;

    @JsonProperty("employee_badge")
    private String employeeBadge;

    /** Human-readable summary of the labor allocation categories, e.g. "[202020] - Beaverton - ... - 1". */
    @JsonProperty("labor_allocation_details")
    private String laborAllocationDetails;
}
