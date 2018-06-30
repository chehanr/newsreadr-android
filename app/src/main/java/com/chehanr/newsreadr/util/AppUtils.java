package com.chehanr.newsreadr.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
}
