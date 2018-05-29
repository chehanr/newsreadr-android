package com.chehanr.newsreadr.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.chehanr.newsreadr.R;
import com.chehanr.newsreadr.adapter.SavedArticlesAdapter;
import com.chehanr.newsreadr.database.AppDatabase;
import com.chehanr.newsreadr.database.entity.SavedArticle;
import com.chehanr.newsreadr.util.Utils;

import java.util.List;

public class SavedArticlesActivity extends AppCompatActivity {
    private static final String TAG = SavedArticlesActivity.class.getSimpleName();

    private Context context;

    private LinearLayoutManager linearLayoutManager;
    private SavedArticlesAdapter savedArticlesAdapter;
    private RecyclerView savedArticlesRecyclerView;
    private AppDatabase appDatabase;

    private List<SavedArticle> savedArticleList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_articles);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = Utils.getApp().getBaseContext();

        linearLayoutManager = new LinearLayoutManager(context);
        savedArticlesAdapter = new SavedArticlesAdapter(context);

        savedArticlesRecyclerView = findViewById(R.id.saved_articles_recycler_view);
        savedArticlesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        savedArticlesRecyclerView.setAdapter(savedArticlesAdapter);
        savedArticlesRecyclerView.setNestedScrollingEnabled(false);

        appDatabase = AppDatabase.getDatabase(getApplicationContext());

        savedArticleList = appDatabase.savedArticlesDao().getAllSavedArticles();

        savedArticlesAdapter.addAll(savedArticleList);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_saved_articles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
