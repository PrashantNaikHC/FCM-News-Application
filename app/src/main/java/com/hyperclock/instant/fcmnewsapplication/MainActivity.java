package com.hyperclock.instant.fcmnewsapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hyperclock.instant.fcmnewsapplication.adapters.NewsAdapter;
import com.hyperclock.instant.fcmnewsapplication.model.Article;
import com.hyperclock.instant.fcmnewsapplication.utils.DateUtils;
import com.hyperclock.instant.fcmnewsapplication.utils.NewsUtility;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements NewsAdapter.ClickListener {

    private NewsAdapter adapter;
    private static final String LOG_TAG = "MainActivity";
    List<Article> articles;

    TextView noNetwork;
    RecyclerView recyclerView;
    SwipeRefreshLayout refresh_layout;
    SnapHelper snapHelper;

    ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initBackgroundService();
        getToken();
        getRemoteNewsData();

        refresh_layout.setOnRefreshListener(this::getRemoteNewsData);
        handleIntent();
    }

    // when user clicks on notification, intent data will be shown as a list item
    private void handleIntent() {
        if (getIntent().hasExtra("id")) {
            Article article = Article.getArticleFromIntent(this);
            articles.add(article);
            recyclerView.scrollToPosition(articles.size() - 1);
        }
    }

    // method to get the FCM token
    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(LOG_TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d(LOG_TAG, "FCM token = " + token);
                });
    }

    private void getRemoteNewsData() {
        if (isDeviceOnline(this)) {
            fetchNetworkResponse();
            noNetwork.setVisibility(View.GONE);
        } else {
            // network unavailable, show error
            noNetwork.setVisibility(View.VISIBLE);
            refresh_layout.setRefreshing(false);
        }
    }

    private void fetchNetworkResponse() {
        Future<List<Article>> future = executorService.submit(() -> NewsUtility.fetchNews(NewsUtility.NEWS_URL));
        try {
            refresh_layout.setRefreshing(false);
            articles = future.get();
            adapter = new NewsAdapter(articles, this, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initBackgroundService() {
        executorService = Executors.newSingleThreadExecutor();
    }

    private void initViews() {
        noNetwork = findViewById(R.id.network_error);
        recyclerView = findViewById(R.id.recycler_view);
        refresh_layout = findViewById(R.id.refresh_layout);
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        refresh_layout.setRefreshing(true);
    }

    @Override
    public void onClick(String webUrl) {
        Uri uri = Uri.parse(webUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.date_des_sort:
                if (articles != null) {
                    articles = DateUtils.sortedArticlesByDate(articles, false);
                    updateList();
                }
                return true;
            case R.id.date_asc_sort:
                if (articles != null) {
                    articles = DateUtils.sortedArticlesByDate(articles, true);
                    updateList();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Update the article list and move the scroll position to first index
     */
    private void updateList() {
        adapter.setArticles(articles);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);
    }

    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}