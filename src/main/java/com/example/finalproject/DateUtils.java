package com.example.finalproject;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

// utility class for date conversions
public class DateUtils {

    // converts a java.util.Date to java.time.LocalDate using the system default time zone.
    public static LocalDate toLocal(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
