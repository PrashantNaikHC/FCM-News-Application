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
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hyperclock.instant.fcmnewsapplication.adapters.NewsAdapter;
import com.hyperclock.instant.fcmnewsapplication.model.Article;
import com.hyperclock.instant.fcmnewsapplication.utils.DateUtils;
import com.hyperclock.instant.fcmnewsapplication.utils.NewsUtility;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements NewsAdapter.ClickListener {

    private NewsAdapter adapter;
    private static final String LOG_TAG = "MainActivity";
    private static final String NEWS_URL = "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json";
    List<Article> articles;

    TextView noNetwork;
    RecyclerView recyclerView;
    SwipeRefreshLayout refresh_layout;
    SnapHelper snapHelper;

    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initBackgroundService();
        getRemoteNewsData();
        refresh_layout.setOnRefreshListener(() -> getRemoteNewsData());
    }

    private void getRemoteNewsData() {
        if (isDeviceOnline(this)) {
            fetchNetworkResponse();
        } else {
            // network unavailable, show error
            noNetwork.setVisibility(View.VISIBLE);
            refresh_layout.setRefreshing(false);
        }
    }

    private void fetchNetworkResponse() {
        // network available
        Future<List<Article>> future = executorService.submit(new Callable<List<Article>>() {
            @Override
            public List<Article> call() {
                return NewsUtility.fetchNews(NEWS_URL);
            }
        });

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
        // Handle item selection
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