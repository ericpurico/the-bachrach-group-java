package com.client.custom;


import com.client.custom.paycom.services.PaycomAPIService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/test")
@CrossOrigin("*")
public class TestController {

    @Autowired
    private PaycomAPIService paycomAPIService;

    @GetMapping("/testGetFieldOptions")
    public ResponseEntity<JsonNode> testGetFieldOptions(HttpServletRequest request){
        JsonNode json = paycomAPIService.getNewHireFieldOptions();
        System.out.println(json);
        return ResponseEntity.ok(json);
    }
}
