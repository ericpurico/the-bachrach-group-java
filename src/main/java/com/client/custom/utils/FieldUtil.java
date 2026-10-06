package com.client.custom.utils;

import com.bullhornsdk.data.model.entity.core.standard.Candidate;
import com.bullhornsdk.data.model.entity.core.standard.Placement;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

@Log4j2
@Component
public class FieldUtil {
    public static void setCandidateFieldValue(Candidate candidate, String fieldName, String value){
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(candidate.getClass(), fieldName);
        if(propertyDescriptor == null || propertyDescriptor.getWriteMethod() == null){
            log.warn("No writable property '{}' found on {}", fieldName, candidate.getClass().getSimpleName());
            return;
        }
        try{
            propertyDescriptor.getWriteMethod().invoke(candidate, value);
        } catch (IllegalAccessException | InvocationTargetException e){
            log.error("Failed to write property '{}' on candidate {}", fieldName, candidate.getId(), e);
        }
    }

    public static String getCandidateFieldValue(Candidate candidate, String fieldName){
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(candidate.getClass(), fieldName);
        if(propertyDescriptor == null || propertyDescriptor.getReadMethod() == null){
            log.warn("No readable property '{}' found on {}", fieldName, candidate.getClass().getSimpleName());
            return null;
        }
        try{
            Object value = propertyDescriptor.getReadMethod().invoke(candidate);
            return value == null ? null : value.toString();
        } catch (IllegalAccessException | InvocationTargetException e){
            log.error("Failed to read property '{}' from candidate {}", fieldName, candidate.getId(), e);
            return null;
        }
    }

}
