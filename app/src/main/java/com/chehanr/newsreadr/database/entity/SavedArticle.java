package com.chehanr.newsreadr.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.chehanr.newsreadr.model.Article;

/**
 * Created by chehanr on 9/22/2017.
 */

@Entity
public final class SavedArticle {

    @PrimaryKey()
    @ColumnInfo(name = "article_id")
    @NonNull
    public String articleId;
    @ColumnInfo(name = "article_index")
    public int articleIndex;
    @ColumnInfo(name = "article_title")
    public String articleTitle;
    @ColumnInfo(name = "article_body")
    public String articleBody;
    @ColumnInfo(name = "article_url")
    public String articleUrl;
    @ColumnInfo(name = "article_thumbnail_uri")
    public String articleThumbnailUri;
    @ColumnInfo(name = "article_type")
    public String articleType;
    @ColumnInfo(name = "article_media")
    public String articleMedia;

    public SavedArticle() {

    }

    public SavedArticle(@NonNull String articleId, Article article) {
        this.articleId = articleId;
        this.articleIndex = article.getArticleIndex();
        this.articleTitle = article.getArticleTitle();
        this.articleBody = article.getArticleBody();
        this.articleUrl = article.getArticleUrl();
        this.articleThumbnailUri = article.getArticleThumbnailUri();
        this.articleType = article.getArticleType();
        this.articleMedia = article.getArticleMedia();
    }

    public String getArticleId() {
        return articleId;
    }

    public Integer getArticleIndex() {
        return articleIndex;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public String getArticleThumbnailUri() {
        return articleThumbnailUri;
    }

    public String getArticleBody() {
        return articleBody;
    }

    public String getArticleType() {
        return articleType;
    }

    public String getArticleMedia() {
        return articleMedia;
    }

}
