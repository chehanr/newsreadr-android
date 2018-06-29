package com.chehanr.newsreadr.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.chehanr.newsreadr.database.entity.SavedArticle;

import java.util.List;

/**
 * Created by chehanr on 9/22/2017.
 */

@Dao
public interface SavedArticlesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addSaveArticle(SavedArticle SavedArticle);

    @Query("SELECT * FROM SavedArticle")
    public List<SavedArticle> getAllSavedArticles();

    @Query("SELECT * FROM SavedArticle WHERE article_id = :articleId")
    public List<SavedArticle> getSavedArticle(String articleId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSavedArticle(SavedArticle SavedArticle);

    @Query("DELETE FROM SavedArticle WHERE article_id = :articleId")
    public void removeSavedArticle(String articleId);

    @Query("DELETE FROM SavedArticle")
    void removeAllSavedArticles();

    @Query("SELECT article_id FROM SavedArticle WHERE EXISTS (SELECT article_id FROM SavedArticle WHERE article_id = :articleId)")
    public boolean checkIfSavedArticleExists(String articleId);
}
