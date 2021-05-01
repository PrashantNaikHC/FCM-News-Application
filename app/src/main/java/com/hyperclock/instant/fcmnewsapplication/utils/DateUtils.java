package com.hyperclock.instant.fcmnewsapplication.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String getFormattedDate(String dateString) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date date = inputFormat.parse(dateString);
        return outputFormat.format(date);
    }

}
