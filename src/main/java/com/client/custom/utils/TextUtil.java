package com.client.custom.utils;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class TextUtil {

    public static String sanitizePhone(String rawPhone){
        //strip all non numerics, if there is less than 9 digits add preceding zeros
        if(rawPhone == null){
            return null;
        }

        String digitsOnly = rawPhone.replaceAll("[^0-9]", "");
        return digitsOnly;
    }



}
