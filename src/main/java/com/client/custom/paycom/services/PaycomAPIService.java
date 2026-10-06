package com.client.custom.paycom.services;

import com.client.custom.paycom.exception.PaycomApiException;
import com.client.custom.paycom.model.request.PaycomNewHire;
import com.client.custom.paycom.model.response.PaycomEmployeeChangeEntry;
import com.client.custom.paycom.model.response.PaycomEmployeeDetail;
import com.client.custom.paycom.model.response.PaycomEmployeeDirectoryResponse;
import com.client.custom.paycom.model.response.PaycomNewHireDetail;
import com.client.custom.paycom.model.response.PaycomNewHireResponse;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.List;

public interface PaycomAPIService {

    /**
     * Calls the Paycom Employee Directory endpoint - GET api/v1/employeedirectory - to verify that
     * Basic Auth credentials (SID/Token) are valid and the connection is working. Throws
     * {@link PaycomApiException} on any failure (rejected call, auth failure, unparseable response).
     */
    PaycomEmployeeDirectoryResponse getEmployeeDirectory(Integer page, Integer pageSize);

    /**
     * Calls the Paycom New Hire Field Options endpoint - GET api/v1/newhire/fieldoptions - which
     * returns the valid dropdown/field values available for the New Hire form. Throws
     * {@link PaycomApiException} on any failure.
     */
    JsonNode getNewHireFieldOptions();

    JsonNode getNewHireIds();

    /**
     * Calls the Paycom Employee endpoint - GET api/v1/employee/{employeeCode} - and returns the
     * single employee it describes, or null if Paycom returned no data for that code.
     */
    PaycomEmployeeDetail getEmployeeById(String employeeCode);

    /**
     * Calls the Paycom New Hire endpoint - GET api/v1/newhire/{id} - and returns the single new
     * hire it describes, or null if Paycom returned no data for that id.
     */
    PaycomNewHireDetail getNewHireById(Integer newHireId);

    /**
     * Calls the Paycom New Hire IDs endpoint - GET api/v1/newhireids - and returns the ids of only
     * the entries whose addedOnDate falls within the given lookback window (e.g.
     * Duration.ofHours(2)), most recent first. addedOnDate carries no timezone marker in Paycom's
     * response; it is treated as America/New_York (see PaycomAPIServiceImpl for the assumption).
     * Multiple ids can come back if more than one new hire was created in the window - the caller
     * is expected to disambiguate (e.g. by fetching each via getNewHireById and comparing
     * name/email).
     */
    List<Integer> getRecentNewHireIds(Duration lookback);


    /**
     * Calls the Paycom New Hire Create endpoint - POST api/v1/newhire/createnewhire - to create
     * one new hire. Throws {@link PaycomApiException} on any failure, including a call Paycom
     * accepted (2xx) but flagged as unsuccessful in the response body (result=false or a non-zero
     * errorCount).
     */
    PaycomNewHireResponse createNewHire(PaycomNewHire newHire);

    /**
     * Calls the Paycom Employee Changes endpoint - GET api/v1/employeeids/employeechanges - and
     * returns, for the given date range, each employee (eecode) who had a field change along with
     * how many changes they had. startDate/endDate are Unix timestamps (seconds).
     */
    List<PaycomEmployeeChangeEntry> getEmployeeChanges(long startDate, long endDate);
}
