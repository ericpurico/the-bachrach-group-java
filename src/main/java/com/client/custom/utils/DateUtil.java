package com.client.custom.utils;

import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Log4j2
public class DateUtil {

    private static final DateTimeZone DATE_TIME_ZONE = DateTimeZone.forID("America/New_York");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("MM/dd/yyyy");

    public static String formatDate(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.withZone(DATE_TIME_ZONE).toString(DATE_FORMATTER);
    }

}
