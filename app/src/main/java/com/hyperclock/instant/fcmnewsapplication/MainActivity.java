package com.hyperclock.instant.fcmnewsapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.hyperclock.instant.fcmnewsapplication.adapters.NewsAdapter;
import com.hyperclock.instant.fcmnewsapplication.model.Article;
import com.hyperclock.instant.fcmnewsapplication.utils.NewsUtility;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements NewsAdapter.ClickListener {

    private NewsAdapter adapter;
    private static final String LOG_TAG  = "MainActivity";
    private static final String NEWS_URL="https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json";

    TextView noNetwork;
    ProgressBar loadingProgressbar;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        if(networkInfo!=null && networkInfo.isConnectedOrConnecting()){
            // network available
            Future<List<Article>> future = executorService.submit(new Callable<List<Article>>() {
                @Override
                public List<Article> call() {
                    return NewsUtility.fetchNews(NEWS_URL);
                }
            });

            try {
                loadingProgressbar.setVisibility(View.GONE);
                List<Article> articles = future.get();
                adapter = new NewsAdapter(articles, this, this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                SnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(recyclerView);

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            // network unavailable, show error
            noNetwork.setVisibility(View.VISIBLE);
            loadingProgressbar.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        noNetwork = findViewById(R.id.network_error);
        loadingProgressbar = findViewById(R.id.loading_progressbar);
        recyclerView = findViewById(R.id.recycler_view);
    }


    @Override
    public void onClick(String webUrl) {
        Uri uri  = Uri.parse(webUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}