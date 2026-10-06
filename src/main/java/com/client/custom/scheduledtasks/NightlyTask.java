package com.client.custom.scheduledtasks;

import com.bullhornsdk.data.model.entity.core.standard.Candidate;
import com.client.custom.bullhorn.services.BullhornService;
import com.client.custom.paycom.model.response.PaycomEmployeeChangeEntry;
import com.client.custom.paycom.services.PaycomAPIService;
import com.client.custom.services.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.client.custom.model.IntegrationFields.*;


@Service
public class NightlyTask {


    @Autowired
    private BullhornService bullhornService;

    @Autowired
    private PaycomAPIService paycomAPIService;

    @Autowired
    private BusinessService businessService;

    private static final Duration EMPLOYEE_CHANGES_LOOKBACK = Duration.ofHours(25);
    private static final Duration EMPLOYEE_CHANGES_BATCH_SPLIT = Duration.ofHours(12);

    public void process(){
        this.processNewChanges();
        this.processNewHires();
    }

    private void processNewChanges(){
        //get the employee changes in the past 25 hours, as the Paycom endpoint only accepts max 24 hours, so we will call it in 2 batches, the first from 25 hours ago to 12 hours ago; the second from 12 hours to now;
        //the startdate enddate are unix timestamp in seconds (not milliseconds) such as 1790794891
        Instant now = Instant.now();
        long nowSeconds = now.getEpochSecond();
        long twelveHoursAgoSeconds = now.minus(EMPLOYEE_CHANGES_BATCH_SPLIT).getEpochSecond();
        long twentyFiveHoursAgoSeconds = now.minus(EMPLOYEE_CHANGES_LOOKBACK).getEpochSecond();

        List<PaycomEmployeeChangeEntry> firstBatch = paycomAPIService.getEmployeeChanges(twentyFiveHoursAgoSeconds, twelveHoursAgoSeconds);
        List<PaycomEmployeeChangeEntry> secondBatch = paycomAPIService.getEmployeeChanges(twelveHoursAgoSeconds, nowSeconds);

        List<String> recentlyUpdatedEmployeeIds = Stream.concat(firstBatch.stream(), secondBatch.stream())
                .map(PaycomEmployeeChangeEntry::getEecode)
                .distinct()
                .collect(Collectors.toList());

        //todo: for each id in recentlyUpdatedEmployeeIds, look up the matching Bullhorn candidate
        // (e.g. businessService.findCandidateByEeCode) and sync whatever Paycom fields changed


        
    }

    private void processNewHires(){
        //get new hires in the past 25 hours
        List<Integer> newlyHiredIds =  paycomAPIService.getRecentNewHireIds(Duration.ofHours(25));
        for(Integer newlyHiredId : newlyHiredIds){
            Candidate candidate = businessService.findCandidateByNewHireId(String.valueOf(newlyHiredId));
            if(candidate == null){
                //we didn't find the candidate in BH, we should raise an issue
                bullhornService.addIssue(null,null, "new hire not in BH: "+newlyHiredId,null);
            }
        }

    }

}
