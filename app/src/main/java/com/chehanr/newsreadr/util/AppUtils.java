package com.chehanr.newsreadr.util;

import com.chehanr.newsreadr.model.Article;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class AppUtils {
    public static String getArticleIdHash(Article article) {
        String title = article.getArticleTitle();
        String url = article.getArticleUrl();
        String media = article.getArticleMedia();

        String concatString = title;
        if (url != null) {
            concatString += url;
        } else {
            concatString += media;
        }

        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(concatString.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest)
                hexString.append(Integer.toHexString(0xFF & aMessageDigest));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String checkNetworkIssues(Integer apiStatusCode, Integer remoteStatusCode) {
        String result;
        if (!NetworkUtils.isConnected()) {
            result = "NOT_CONNECTED";
        } else if (apiStatusCode != null && apiStatusCode != 200) {
            result = "API_DOWN";
        } else if (remoteStatusCode != null && remoteStatusCode != 200) {
            result = "PAGE_DOWN";
        } else {
            result = null;
        }
        return result;

    }
}
