package com.hyperclock.instant.fcmnewsapplication.utils;


import com.hyperclock.instant.fcmnewsapplication.model.Article;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtils {

    public static String getFormattedDate(String dateString) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);
        Date date = inputFormat.parse(dateString);
        return outputFormat.format(date);
    }

    public static List<Article> sortedArticlesByDate(List<Article> articles, boolean reverseOrder){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMyyyyHHmm", Locale.US);
        Collections.sort(articles, new Comparator<Article>() {
            @Override
            public int compare(Article a1, Article a2) {
                int result = 0;
                try {
                    String time1 = outputFormat.format(inputFormat.parse(a1.getPublishedAt()));
                    String time2 = outputFormat.format(inputFormat.parse(a2.getPublishedAt()));
                    result = time1.compareTo(time2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return result;
            }
        });
        if(reverseOrder) Collections.reverse(articles);
        return articles;
    }

}
