package com.client.custom.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
