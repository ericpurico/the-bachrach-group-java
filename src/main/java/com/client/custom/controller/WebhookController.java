package com.client.custom.controller;

import com.client.custom.services.WebhookService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/webhook")
@CrossOrigin("*")
public class WebhookController {

    @Autowired
    private WebhookService webhookService;

    @GetMapping("/employeeChange")
    public ResponseEntity<String> employeeChange(@RequestBody String object){

        return ResponseEntity.ok("ok");
    }
}
