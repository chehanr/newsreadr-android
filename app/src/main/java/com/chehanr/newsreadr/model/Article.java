package com.chehanr.newsreadr.model;

/**
 * Created by chehanr on 9/10/2017.
 */


import com.google.gson.annotations.SerializedName;

public class Article {
    @SerializedName("index")
    private Integer articleIndex;
    @SerializedName("title")
    private String articleTitle;
    @SerializedName("url")
    private String articleUrl;
    @SerializedName("thumbnail-uri")
    private String articleThumbnailUri;
    @SerializedName("body")
    private String articleBody;
    @SerializedName("type")
    private String articleType;
    @SerializedName("media")
    private String articleMedia;

    public Article(Integer articleIndex, String articleTitle, String articleUrl, String articleThumbnailUri, String articleBody, String articleType,
                   String articleMedia) {
        this.articleIndex = articleIndex;
        this.articleTitle = articleTitle;
        this.articleUrl = articleUrl;
        this.articleThumbnailUri = articleThumbnailUri;
        this.articleBody = articleBody;
        this.articleType = articleType;
        this.articleMedia = articleMedia;

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
