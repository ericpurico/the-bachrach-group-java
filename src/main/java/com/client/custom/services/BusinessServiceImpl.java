package com.client.custom.services;

import com.bullhornsdk.data.model.entity.core.standard.Candidate;
import com.bullhornsdk.data.model.entity.core.standard.Placement;
import com.client.custom.bullhorn.services.BullhornService;
import com.client.custom.paycom.model.request.PaycomNewHire;
import com.client.custom.paycom.model.response.*;
import com.client.custom.paycom.services.PaycomAPIService;
import com.client.custom.utils.DateUtil;
import com.client.custom.utils.FieldUtil;
import com.client.custom.utils.TextUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static com.client.custom.model.IntegrationFields.*;

@Service
@Log4j2
public class BusinessServiceImpl implements BusinessService{

    @Autowired
    private BullhornService bullhornService;

    @Autowired
    private PaycomAPIService paycomAPIService;

    //@Autowired
    //private FieldUtil fieldUtil;

    //todo: use a default location?
    private static Integer DEFAULT_LOCATION_ID = 21185;

    private static Integer DEFAULT_TEMPLATE_ID = 0;

    private static String W2_EMPLOYEE_TYPE_ID = "1";
    private static String W1099_EMPLOYEE_TYPE_ID = "2";


    @Override
    public String createNewHire(Integer placementId) {

        Placement placement = bullhornService.getPlacementById(placementId);
        Candidate candidate = placement.getCandidate();

        //dedupe first maybe we should also check the current employee id

        String employeeCode = this.getExistingEmployeeCode(candidate);
        if(employeeCode!=null){
            return employeeCode;
        }


        PaycomNewHire newHire = new PaycomNewHire();
        newHire.setFirstName(candidate.getFirstName());
        newHire.setLastName(candidate.getLastName());
        newHire.setMiddleName(candidate.getMiddleName());
        newHire.setPersonalEmail(candidate.getEmail());
        //strip all none numericals
        newHire.setPrimaryPhone(TextUtil.sanitizePhone(candidate.getMobile()));
        //todo:  use_employee_address is required we need to know how to set it

        //todo: template id
        newHire.setTemplateId(DEFAULT_TEMPLATE_ID);

        if(StringUtils.equalsIgnoreCase(placement.getEmployeeType(), "W2")){
            newHire.setEmployeeType(W2_EMPLOYEE_TYPE_ID);
        }
        else {
            newHire.setEmployeeType(W1099_EMPLOYEE_TYPE_ID);
        }


        newHire.setHireDate(DateUtil.formatDate(placement.getDateBegin()));
        newHire.setLocationId(DEFAULT_LOCATION_ID);

        PaycomNewHireResponse response = paycomAPIService.createNewHire(newHire);

        if(response==null || response.getData()==null || response.getData().get("new_hire_0") == null){
            bullhornService.addIssue(candidate.getId(),placement.getId(),"create new hire error", null);
            return null;
        }
        if(!StringUtils.containsIgnoreCase(response.getData().get("new_hire_0"), "Successful")){
            bullhornService.addIssue(candidate.getId(),placement.getId(),"create new hire error", null);
            return null;
        }

        //creation success, lets lookup the new hire id
        List<Integer> recentNewHireIds = paycomAPIService.getRecentNewHireIds(Duration.ofHours(2));

        if (recentNewHireIds.isEmpty()) {
            bullhornService.addIssue(candidate.getId(), placement.getId(),
                    "create new hire error - new hire was created in Paycom but no matching new hire id was found in the last 2 hours", null);
            return null;
        }

        for (Integer recentNewHireId : recentNewHireIds) {
            PaycomNewHireDetail newHireDetail = paycomAPIService.getNewHireById(recentNewHireId);
            if (newHireDetail == null) {
                continue;
            }

            boolean isMatch = StringUtils.equalsIgnoreCase(newHireDetail.getFirstName(), candidate.getFirstName())
                    && StringUtils.equalsIgnoreCase(newHireDetail.getLastName(), candidate.getLastName())
                    && StringUtils.equalsIgnoreCase(newHireDetail.getPersonalEmail(), candidate.getEmail());

            if (isMatch) {
                String paycomEmployeeCode = newHireDetail.getNewEmployeeCode();
                this.writeEeCodeToBullhorn(candidate.getId(), paycomEmployeeCode);
                return paycomEmployeeCode;
            }
        }

        bullhornService.addIssue(candidate.getId(), placement.getId(),
                "create new hire error - new hire was created in Paycom but could not be matched by name/email among the recent new hire ids", null);
        return null;
    }

    private void writeEeCodeToBullhorn(Integer candidateId, String eeCode){
        //todo: field to be confirmed
        Candidate candidate = new Candidate(candidateId);
        candidate.setCustomText20(eeCode);
        bullhornService.updateCandidate(candidate);
        log.info("Candidate eecode updated: {}-{}", candidateId, eeCode);
    }

    public Candidate findCandidateByEeCode(String eeCode){
        //todo: field to be confirmed
        String query = "isDeleted:false AND NOT status:Archive AND "+EE_CODE_FIELD+":("+eeCode+")";
        List<Candidate> candidates = bullhornService.searchCandidate(query);
        List<Candidate> filteredCandidates = candidates.stream().filter(s->StringUtils.equalsIgnoreCase(FieldUtil.getCandidateFieldValue(s, EE_CODE_FIELD),eeCode)).collect(Collectors.toList());
        if(filteredCandidates.size()>0){
            return filteredCandidates.get(0);
        }
        return null;
    }

    public Candidate findCandidateByNewHireId(String newHireId){
        //todo: field to be confirmed
        String query = "isDeleted:false AND NOT status:Archive AND "+NEW_HIRE_ID_FIELD+":("+newHireId+")";
        List<Candidate> candidates = bullhornService.searchCandidate(query);
        List<Candidate> filteredCandidates = candidates.stream().filter(s->StringUtils.equalsIgnoreCase(FieldUtil.getCandidateFieldValue(s, NEW_HIRE_ID_FIELD),newHireId)).collect(Collectors.toList());
        if(filteredCandidates.size()>0){
            return filteredCandidates.get(0);
        }
        return null;
    }




    private String getExistingEmployeeCode(Candidate candidate){
        List<PaycomEmployeeDirectoryEntry> allEmployees = new ArrayList<>();

        Integer page = 1;
        Integer pageSize = 500;

        while(true){
            PaycomEmployeeDirectoryResponse response = paycomAPIService.getEmployeeDirectory(page, pageSize);
            if(response==null || response.getData()==null || response.getData().isEmpty()){
                break;
            }
            allEmployees.addAll(response.getData());
            page = page + 1;
        }

        List<PaycomEmployeeDirectoryEntry> filteredEmployees = allEmployees.stream().filter(s->StringUtils.equalsIgnoreCase(candidate.getLastName(), s.getLastName()) && StringUtils.equalsIgnoreCase(StringUtils.substring(candidate.getFirstName(),2), StringUtils.substring(s.getLastName(),2))).toList();
        if(filteredEmployees.isEmpty()){
            return null;
        }

        for(PaycomEmployeeDirectoryEntry employee: filteredEmployees){
            //get the employee entity so we can get the email to compare
            PaycomEmployeeDetail employeeDetail = paycomAPIService.getEmployeeById(employee.getEecode());
            if(employeeDetail == null){
                continue;
            }
            boolean isMatch = StringUtils.equalsIgnoreCase(employeeDetail.getFirstName(), candidate.getFirstName())
                    && StringUtils.equalsIgnoreCase(employeeDetail.getLastName(), candidate.getLastName())
                    && StringUtils.equalsIgnoreCase(employeeDetail.getPersonalEmail(), candidate.getEmail());

            if (isMatch) {
                return employeeDetail.getEmployeeCode();
            }

        }

        return null;
    }
}
