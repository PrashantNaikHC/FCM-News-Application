package com.hyperclock.instant.fcmnewsapplication.model;

import android.app.Activity;

import com.google.firebase.messaging.RemoteMessage;

public class Article {
    private Source source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String content;

    public Article(Source source, String author, String title, String description, String url, String urlToImage, String publishedAt, String content) {
        this.source = source;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Article{" +
                "source=" + source +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", urlToImage='" + urlToImage + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public Source getSource() {
        return source;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public static Article getArticleFromNotification(RemoteMessage remoteMessage) {
        Article article = null;
        try {
            article = new Article(
                    new Source(remoteMessage.getData().get("id")
                            , remoteMessage.getData().get("name"))
                    , remoteMessage.getData().get("author")
                    , remoteMessage.getData().get("title")
                    , remoteMessage.getData().get("description")
                    , remoteMessage.getData().get("url")
                    , remoteMessage.getData().get("urlToImage")
                    , remoteMessage.getData().get("publishedAt")
                    , remoteMessage.getData().get("content")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return article;
    }

    public static Article getArticleFromIntent(Activity activity) {
        Article article = null;
        try {
            article = new Article(
                    new Source(activity.getIntent().getStringExtra("id")
                            , activity.getIntent().getStringExtra("name"))
                    , activity.getIntent().getStringExtra("author")
                    , activity.getIntent().getStringExtra("title")
                    , activity.getIntent().getStringExtra("description")
                    , activity.getIntent().getStringExtra("url")
                    , activity.getIntent().getStringExtra("urlToImage")
                    , activity.getIntent().getStringExtra("publishedAt")
                    , activity.getIntent().getStringExtra("content")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return article;
    }
}
