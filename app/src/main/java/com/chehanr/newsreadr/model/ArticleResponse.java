package com.chehanr.newsreadr.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by chehanr on 9/10/2017.
 */

public class ArticleResponse {
    @SerializedName("page-url")
    private String pageUrl;
    @SerializedName("remote-status-code")
    private int remoteStatusCode;
    @SerializedName("page")
    private int page;
    @SerializedName("available-pages")
    private int availablePages;
    @SerializedName("articles")
    private List<Article> articles;

    public String getPageUrl() {
        return pageUrl;
    }

    public int getRemoteStatusCode() {
        return remoteStatusCode;
    }

    public int getPage() {
        return page;
    }

    public int getAvailablePages() {
        return availablePages;
    }

    public List<Article> getArticles() {
        return articles;
    }
}