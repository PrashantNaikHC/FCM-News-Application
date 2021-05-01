package com.hyperclock.instant.fcmnewsapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hyperclock.instant.fcmnewsapplication.R;
import com.hyperclock.instant.fcmnewsapplication.model.Article;
import com.hyperclock.instant.fcmnewsapplication.utils.DateUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<Article> articles;
    private Context context;
    private ClickListener listener;

    public NewsAdapter(List<Article> articles, Context context, ClickListener listener) {
        this.articles = articles;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsAdapter.NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.articles_item_layout, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.NewsViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.title.setText(article.getTitle());
        holder.description.setText(article.getDescription());
        if(article.getAuthor().equals("null")) {
            holder.author.setText("");
        } else {
            holder.author.setText(article.getAuthor());
        }
        holder.source.setText(article.getSource().getName());
        try {
            holder.publishDate.setText(DateUtils.getFormattedDate(article.getPublishedAt()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Glide.with(context)
                .load(article.getUrlToImage())
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.backgroundImage);

        holder.itemView.setOnClickListener(view -> listener.onClick(article.getUrl()));
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public TextView author;
        public TextView source;
        public TextView publishDate;
        public ImageView backgroundImage;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.news_title);
            this.description = itemView.findViewById(R.id.news_description);
            this.author = itemView.findViewById(R.id.news_author);
            this.source = itemView.findViewById(R.id.news_source);
            this.publishDate = itemView.findViewById(R.id.publish_date);
            this.backgroundImage = itemView.findViewById(R.id.news_background_image);
        }
    }

    public interface ClickListener {
        void onClick(String webUrl);
    }
}
