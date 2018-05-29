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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chehanr.newsreadr.R;
import com.chehanr.newsreadr.model.Article;
import com.chehanr.newsreadr.util.NetworkUtils;
import com.chehanr.newsreadr.util.RegexUtils;

import java.util.ArrayList;
import java.util.List;


public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Article> articleList;
    private Context context;

    private boolean isLoadingAdded = false;

    private boolean prefShowThumbnails;
    private String prefDownloadThumbnailsList;

    public ArticlesAdapter(Context context) {
        this.context = context;
        articleList = new ArrayList<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        prefShowThumbnails = sharedPreferences.getBoolean("show_thumbnails_switch", true);
        prefDownloadThumbnailsList = sharedPreferences.getString("download_thumbnails_list", "1");
    }

    public List<Article> getArticleList() {
        return articleList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
//                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
//                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Article article = getArticleList().get(position); // Movie

        switch (getItemViewType(position)) {
            case ITEM:
                final ArticleItemViewHolder articleItemViewHolder = (ArticleItemViewHolder) holder;
                articleItemViewHolder.articleTitleTextView.setText(article.getArticleTitle());
                articleItemViewHolder.articleBodyTextView.setText(article.getArticleBody() + "...");
                articleItemViewHolder.articleDetailTextView.setText(handleArticleDetail(article.getArticleType(), article.getArticleUrl(), article.getArticleMedia()));
                handleArticleThumbnail(articleItemViewHolder, article.getArticleThumbnailUri());
                break;

            case LOADING:
//                Do nothing
                break;
        }
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.list_item_article, parent, false);
        viewHolder = new ArticleItemViewHolder(v1);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return getArticleList() == null ? 0 : getArticleList().size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == getArticleList().size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    public void add(Article article) {
        getArticleList().add(article);
        notifyItemInserted(getArticleList().size() - 1);
    }

    public void addAll(List<Article> articleList) {
        for (Article article : articleList) {
            add(article);
        }
    }

    public void remove(Article article) {
        int position = getArticleList().indexOf(article);
        if (position > -1) {
            getArticleList().remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeAll() {
        if (getArticleList().size() > 0) {
//            int endPosition = articleList.size();
            getArticleList().clear();
//            notifyItemRangeRemoved(0, endPosition);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


//    public void addLoadingFooter() {
//        isLoadingAdded = true;
//        add(new Article());
//    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = getArticleList().size() - 1;
        Article article = getItem(position);

        if (article != null) {
            getArticleList().remove(position);
            notifyItemRemoved(position);
        }
    }

    public Article getItem(int position) {
        return getArticleList().get(position);
    }

    private void handleArticleThumbnail(ArticlesAdapter.ArticleItemViewHolder articleItemViewHolder, String thumbnailUri) {
//        TODO get url dynamically.
        String thumbnailUrl = "http://infolanka.com/news/" + thumbnailUri;
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .override(100, 100);
        if (prefShowThumbnails) {
            articleItemViewHolder.horizontalLinearLayout.setVisibility(View.VISIBLE);
            if ((prefDownloadThumbnailsList.equals("1") || prefDownloadThumbnailsList.equals("2"))) {
                Glide.with(context)
                        .asBitmap()
                        .apply(requestOptions)
                        .load(thumbnailUrl)
                        .into(articleItemViewHolder.articleThumbnailImageView);
                if (prefDownloadThumbnailsList.equals("2")) {
                    if (NetworkUtils.isWifiConnected()) {
                        Glide.with(context)
                                .asBitmap()
                                .apply(requestOptions)
                                .load(thumbnailUrl)
                                .into(articleItemViewHolder.articleThumbnailImageView);
                    } else {
                        Glide.with(context)
                                .clear(articleItemViewHolder.articleThumbnailImageView);
                    }
                }
            }
        } else {
            articleItemViewHolder.horizontalLinearLayout.setVisibility(View.GONE);
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


    protected class ArticleItemViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout articlesConstraintLayout;
        LinearLayout horizontalLinearLayout;
        TextView articleTitleTextView;
        TextView articleBodyTextView;
        TextView articleDetailTextView;
        ImageView articleThumbnailImageView;

        public ArticleItemViewHolder(View view) {
            super(view);
            this.articlesConstraintLayout = view.findViewById(R.id.articles_layout_level_0);
            this.horizontalLinearLayout = view.findViewById(R.id.horizontalLinearLayout);
            this.articleTitleTextView = view.findViewById(R.id.title_textView);
            this.articleBodyTextView = view.findViewById(R.id.body_textView);
            this.articleDetailTextView = view.findViewById(R.id.detail_textView);
            this.articleThumbnailImageView = view.findViewById(R.id.thumbnail_imageView);
        }
    }


    protected class ArticleLoadingViewHolder extends RecyclerView.ViewHolder {
        TextView articleSectionSectionNumberTextView;

        public ArticleLoadingViewHolder(View view) {
            super(view);
            this.articleSectionSectionNumberTextView = view.findViewById(R.id.article_section);
        }
    }

}
