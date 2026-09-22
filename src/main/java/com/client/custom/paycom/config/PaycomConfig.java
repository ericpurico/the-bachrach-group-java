package com.client.custom.paycom.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Paycom request/response payloads (e.g. newhire/createnewhire) are snake_case - matched here via
 * PropertyNamingStrategies.SNAKE_CASE so typed model classes can use normal camelCase Java fields
 * without needing a @JsonProperty on every one. Single-word field names elsewhere (eecode,
 * changedesc, etc., read only as JsonNode today) are unaffected, since snake_case is a no-op on a
 * name with no word boundaries. Null fields are omitted on serialize, since optional fields like
 * hire_date should be left out of the request entirely rather than sent as null.
 */
@Configuration
public class PaycomConfig {

    @Bean
    public ObjectMapper paycomObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
