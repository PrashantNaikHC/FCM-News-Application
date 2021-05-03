package com.hyperclock.instant.fcmnewsapplication.utils;

import android.text.TextUtils;
import android.util.Log;

import com.hyperclock.instant.fcmnewsapplication.model.Article;
import com.hyperclock.instant.fcmnewsapplication.model.Source;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NewsUtility {

    private static final String LOG_TAG = "NewsUtility";
    public static final String NEWS_URL = "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json";

    public NewsUtility() {
    }

    public static List<Article> fetchNews(String requestUrl) {
        URL url = createURL(requestUrl);
        String JsonResponse = null;
        try {
            JsonResponse = makeHTTPrequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "IO  Exception occured", e);
        }
        return extractFeatureFromJson(JsonResponse);
    }

    private static List<Article> extractFeatureFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        List<Article> NewsArticleArrayList = new ArrayList<>();
        try {
            JSONObject baseJsonObject = new JSONObject(jsonResponse);
            JSONArray jsonArray = baseJsonObject.getJSONArray("articles");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject currentJsonObject = jsonArray.getJSONObject(i);
                JSONObject source = currentJsonObject.getJSONObject("source");
                String id = source.getString("id");
                String name = source.getString("name");

                String author = currentJsonObject.getString("author");
                String title = currentJsonObject.getString("title");
                String description = currentJsonObject.getString("description");
                String url = currentJsonObject.getString("url");
                String urlToImage = currentJsonObject.getString("urlToImage");
                String publishedAt = currentJsonObject.getString("publishedAt");
                String content = currentJsonObject.getString("content");

                Article article = new Article(new Source(id, name), author, title, description, url, urlToImage, publishedAt, content);
                NewsArticleArrayList.add(article);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Json exception occured", e);
        }
        return NewsArticleArrayList;
    }

    private static String makeHTTPrequest(URL url) throws IOException {
        String JsonResponse = "";
        if (url == null) {
            return JsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                JsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Response code is" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the response", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return JsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static URL createURL(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Malformed URL exception", e);
        }
        return url;
    }
}
