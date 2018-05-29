package com.chehanr.newsreadr.rest;

import com.chehanr.newsreadr.model.ArticleResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by chehanr on 9/10/2017.
 */

public interface ApiInterface {
    @GET("articles")
    Call<ArticleResponse> getArticles(@Query("page") Integer articlesPage);

    @GET("archives")
    Call<ArticleResponse> getArchives(@Query("page") Integer archivesPage, @Query("year") String archivesYear, @Query("month") Integer archivesMonth);

//    @GET("movie/{id}")
//    Call<ArticleResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);
}

