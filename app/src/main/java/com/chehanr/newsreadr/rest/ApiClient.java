package com.chehanr.newsreadr.rest;

import com.chehanr.newsreadr.util.NetworkUtils;
import com.chehanr.newsreadr.util.Utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chehanr on 9/10/2017.
 */

public class ApiClient {
    private static final String BASE_URL = "https://newsreadr.herokuapp.com/api/v1/";
    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;

    private static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .cache(new Cache(Utils.getApp().getCacheDir(), 10 * 1024 * 1024)) // 10 MB
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            if (NetworkUtils.isConnected()) {
                                request = request.newBuilder().header("Cache-Control", "public, max-age=" + 60).build();
                            } else {
                                request = request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build();
                            }
                            return chain.proceed(request);
                        }
                    })
                    .readTimeout(10, TimeUnit.SECONDS)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build();
        }
        return okHttpClient;
    }


    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(new NullOnEmptyConverterFactory()) //this should come first
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

