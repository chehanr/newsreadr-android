package com.chehanr.newsreadr.util;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class GlideUtils {
    public static void setUrl(Context context, RequestOptions requestOptions, String url, ImageView imageView) {
        Glide.with(context)
                .asBitmap()
                .apply(requestOptions)
                .load(url)
                .into(imageView);
    }

    public static void setDrawable(Context context, RequestOptions requestOptions, int drawable, ImageView imageView) {
        Glide.with(context)
                .asBitmap()
                .apply(requestOptions)
                .load(drawable)
                .into(imageView);
    }

    public static void clearView(Context context, View v) {
        Glide.with(context).clear(v);
    }
}
