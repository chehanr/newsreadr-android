package com.chehanr.newsreadr.util;

import android.content.ActivityNotFoundException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public final class AppUtils {
    public static String getArticleIdHash(String title, String url, String media) {
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

    public static String handleArticleBody(String articleBody) {
//        TODO add regex stuff.
        String string;

        if (articleBody != null) {
            if (articleBody.endsWith("."))
                string = articleBody;
            else
                string = String.format(Locale.getDefault(), "%s...", articleBody);
            return string;
        }
        return null;
    }

    public static String handleArticleDetail(String articleType, String articleUrl, String articleMedia) {
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
}
