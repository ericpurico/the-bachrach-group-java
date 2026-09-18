package com.client.custom.paycom.services;

import com.client.custom.paycom.exception.PaycomApiException;
import com.fasterxml.jackson.databind.JsonNode;

public interface PaycomAPIService {

    /**
     * Calls the Paycom Employee Directory endpoint - GET api/v1/employeedirectory - to verify that
     * Basic Auth credentials (SID/Token) are valid and the connection is working. Throws
     * {@link PaycomApiException} on any failure (rejected call, auth failure, unparseable response).
     */
    JsonNode getEmployeeDirectory();

    /**
     * Calls the Paycom New Hire Field Options endpoint - GET api/v1/newhire/fieldoptions - which
     * returns the valid dropdown/field values available for the New Hire form. Throws
     * {@link PaycomApiException} on any failure.
     */
    JsonNode getNewHireFieldOptions();
}
