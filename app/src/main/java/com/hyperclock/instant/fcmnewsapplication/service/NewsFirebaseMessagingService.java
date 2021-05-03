package com.hyperclock.instant.fcmnewsapplication.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hyperclock.instant.fcmnewsapplication.model.Article;

public class NewsFirebaseMessagingService extends FirebaseMessagingService {
    private static final String LOG_TAG = "MessagingService";

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(LOG_TAG, "Refreshed Messaging token = " + token);
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(LOG_TAG, "Message data payload" + remoteMessage.getData());

            // article object retrieved from the FCM, when the app is in the Foreground
            // This can be passed as values to trigger a notification. on clicking of which,
            // will open that article
            Article article = Article.getArticleFromNotification(remoteMessage);

            super.onMessageReceived(remoteMessage);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(LOG_TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}

