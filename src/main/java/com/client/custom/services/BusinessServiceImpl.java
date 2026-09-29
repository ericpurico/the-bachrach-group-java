package com.client.custom.services;

import com.bullhornsdk.data.model.entity.core.standard.Candidate;
import com.bullhornsdk.data.model.entity.core.standard.Placement;
import com.client.custom.bullhorn.services.BullhornService;
import com.client.custom.paycom.model.request.PaycomNewHire;
import com.client.custom.paycom.model.response.PaycomNewHireDetail;
import com.client.custom.paycom.model.response.PaycomNewHireResponse;
import com.client.custom.paycom.services.PaycomAPIService;
import com.client.custom.utils.DateUtil;
import com.client.custom.utils.TextUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class BusinessServiceImpl implements BusinessService{

    @Autowired
    private BullhornService bullhornService;

    @Autowired
    private PaycomAPIService paycomAPIService;

    //todo: use a default location?
    private static Integer DEFAULT_LOCATION_ID = 21185;

    private static Integer DEFAULT_TEMPLATE_ID = 0;

    private static String W2_EMPLOYEE_TYPE_ID = "1";
    private static String W1099_EMPLOYEE_TYPE_ID = "2";


    @Override
    public String createNewHire(Integer placementId) {

        Placement placement = bullhornService.getPlacementById(placementId);
        Candidate candidate = placement.getCandidate();


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
                return newHireDetail.getNewEmployeeCode();
            }
        }

        bullhornService.addIssue(candidate.getId(), placement.getId(),
                "create new hire error - new hire was created in Paycom but could not be matched by name/email among the recent new hire ids", null);
        return null;
    }
}
