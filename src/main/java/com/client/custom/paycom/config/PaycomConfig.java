package com.client.custom.paycom.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Paycom's API returns its own field naming (eecode, changedesc, detailcode, etc.) and the schema
 * for endpoints we haven't fully mapped yet isn't confirmed - so this gets its own mapper, tolerant
 * of unknown fields, rather than reusing the app-wide CustomJsonObjectMapper.
 */
@Configuration
public class PaycomConfig {

    @Bean
    public ObjectMapper paycomObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
