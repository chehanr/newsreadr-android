package com.chehanr.newsreadr.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Converters {
    @TypeConverter
    public static String arraylistToString(ArrayList<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}