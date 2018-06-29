package com.chehanr.newsreadr.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chehanr.newsreadr.R;
import com.chehanr.newsreadr.database.entity.SavedArticle;
import com.chehanr.newsreadr.model.Article;
import com.chehanr.newsreadr.util.NetworkUtils;
import com.chehanr.newsreadr.util.RegexUtils;

import java.util.ArrayList;
import java.util.List;

public class SavedArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static SavedArticlesAdapter.ItemClickListener itemClickListener;

    private List<SavedArticle> savedArticleList;
    private Context context;

    private boolean prefShowThumbnails;
    private String prefDownloadThumbnailsList;

    public SavedArticlesAdapter(Context context) {
        this.context = context;
        savedArticleList = new ArrayList<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        prefShowThumbnails = sharedPreferences.getBoolean("show_thumbnails_switch", true);
        prefDownloadThumbnailsList = sharedPreferences.getString("download_thumbnails_list", "1");
    }

    public List<SavedArticle> getSavedArticleList() {
        return savedArticleList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        viewHolder = getViewHolder(parent, inflater);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SavedArticle savedArticle = getSavedArticleList().get(position); // Movie
        final SavedArticleViewHolder savedArticleViewHolder = (SavedArticleViewHolder) holder;
        savedArticleViewHolder.articleTitleTextView.setText(savedArticle.getArticleTitle());
        savedArticleViewHolder.articleBodyTextView.setText(savedArticle.getArticleBody());
        savedArticleViewHolder.articleDetailTextView.setText(handleArticleDetail(savedArticle.getArticleType(), savedArticle.getArticleUrl(), savedArticle.getArticleMedia()));
        handleArticleThumbnail(savedArticleViewHolder, savedArticle.getArticleThumbnailUri());
    }

    @Override
    public int getItemCount() {
        return getSavedArticleList() == null ? 0 : getSavedArticleList().size();
    }

    public void add(SavedArticle savedArticle) {
        getSavedArticleList().add(savedArticle);
        notifyItemInserted(getSavedArticleList().size() - 1);
    }

    public void addAll(List<SavedArticle> savedArticleList) {
        for (SavedArticle savedArticle : savedArticleList) {
            add(savedArticle);
        }
    }

    public void remove(SavedArticle article) {
        int position = getSavedArticleList().indexOf(article);
        if (position > -1) {
            getSavedArticleList().remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View view = inflater.inflate(R.layout.list_item_article, parent, false);
        viewHolder = new SavedArticleViewHolder(view);
        return viewHolder;
    }

    public SavedArticle getItem(int position) {
        return getSavedArticleList().get(position);
    }

    private void handleArticleThumbnail(SavedArticlesAdapter.SavedArticleViewHolder savedArticleViewHolder, String thumbnailUri) {
//        TODO get url dynamically.
        String thumbnailUrl = "http://infolanka.com/news/" + thumbnailUri;
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .override(100, 100);
        if (prefShowThumbnails) {
            savedArticleViewHolder.horizontalLinearLayout.setVisibility(View.VISIBLE);
            if ((prefDownloadThumbnailsList.equals("1") || prefDownloadThumbnailsList.equals("2"))) {
                Glide.with(context)
                        .asBitmap()
                        .apply(requestOptions)
                        .load(thumbnailUrl)
                        .into(savedArticleViewHolder.articleThumbnailImageView);
                if (prefDownloadThumbnailsList.equals("2")) {
                    if (NetworkUtils.isWifiConnected()) {
                        Glide.with(context)
                                .asBitmap()
                                .apply(requestOptions)
                                .load(thumbnailUrl)
                                .into(savedArticleViewHolder.articleThumbnailImageView);
                    } else {
                        Glide.with(context)
                                .clear(savedArticleViewHolder.articleThumbnailImageView);
                    }
                }
            }
        } else {
            savedArticleViewHolder.horizontalLinearLayout.setVisibility(View.GONE);
        }
    }

    private String handleArticleDetail(String articleType, String articleUrl, String articleMedia) {
        try {
            if (articleType != null) {
                if (RegexUtils.isURL(articleUrl)) {
                    return String.format("%s (%s)", articleType, NetworkUtils.getHostAddress(articleUrl));
                } else if (RegexUtils.isURL(articleMedia)) {
                    return String.format("%s (%s)", articleType, NetworkUtils.getHostAddress(articleMedia));
                } else {
                    return null;
                }
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public void setOnItemClickListener(SavedArticlesAdapter.ItemClickListener itemClickListener) {
        SavedArticlesAdapter.itemClickListener = itemClickListener;
    }


    public interface ItemClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v);
    }

    protected class SavedArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ConstraintLayout articlesConstraintLayout;
        LinearLayout horizontalLinearLayout;
        TextView articleTitleTextView;
        TextView articleBodyTextView;
        TextView articleDetailTextView;
        ImageView articleThumbnailImageView;

        SavedArticleViewHolder(View view) {
            super(view);
            this.articlesConstraintLayout = view.findViewById(R.id.articles_layout_level_0);
            this.horizontalLinearLayout = view.findViewById(R.id.horizontalLinearLayout);
            this.articleTitleTextView = view.findViewById(R.id.title_textView);
            this.articleBodyTextView = view.findViewById(R.id.body_textView);
            this.articleDetailTextView = view.findViewById(R.id.detail_textView);
            this.articleThumbnailImageView = view.findViewById(R.id.thumbnail_imageView);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onItemLongClick(getAdapterPosition(), v);
            return true;
        }
    }
}
