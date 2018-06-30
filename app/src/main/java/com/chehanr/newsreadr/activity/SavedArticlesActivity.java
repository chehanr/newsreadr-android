package com.chehanr.newsreadr.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chehanr.newsreadr.R;
import com.chehanr.newsreadr.adapter.SavedArticlesAdapter;
import com.chehanr.newsreadr.database.AppDatabase;
import com.chehanr.newsreadr.database.entity.SavedArticle;
import com.chehanr.newsreadr.util.AppUtils;
import com.chehanr.newsreadr.util.RegexUtils;
import com.chehanr.newsreadr.util.Utils;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.List;

public class SavedArticlesActivity extends AppCompatActivity {
    private static final String TAG = SavedArticlesActivity.class.getSimpleName();

    private Context context;

    private LinearLayoutManager linearLayoutManager;
    private SavedArticlesAdapter savedArticlesAdapter;
    private RecyclerView savedArticlesRecyclerView;
    private AppDatabase appDatabase;

    private SharedPreferences sharedPreferences;

    private boolean prefListAnimation, prefUseInAppBrowser;

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

        List<SavedArticle> articles = appDatabase.savedArticlesDao().getAllSavedArticles();

        savedArticlesAdapter.addAll(articles);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        savedArticlesAdapter.setOnItemClickListener(new SavedArticlesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                handleUrlOpening(savedArticlesAdapter.getItem(position));
            }

            @Override
            public void onItemLongClick(int position, View v) {
                handleModalBottomSheetDialogFragment(savedArticlesAdapter.getItem(position));
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        handleSharedPreferences();
    }

    private void handleSharedPreferences() {
        // Handle list animation pref.
        prefListAnimation = sharedPreferences.getBoolean("list_animation_switch", true);

        if (savedArticlesAdapter != null) {
            handleAnimation();
        }
        // Handle browser pref.
        prefUseInAppBrowser = sharedPreferences.getBoolean("use_in_app_browser_switch", true);
    }


    public void handleAnimation() {
//        TODO make changes.
        if (prefListAnimation) {
//            articlesAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        }
//        articlesRecyclerView.notify(articlesAdapter);
    }

    private void handleUrlOpening(@NonNull SavedArticle article) {
        String url = article.articleUrl;

        if (url == null)
            url = article.articleMedia;

        if (RegexUtils.isURL(url)) {
            try {
                Uri uri = Uri.parse(url);
                if (prefUseInAppBrowser) {
                    new FinestWebView.Builder(context).show(uri.toString());
                } else {
//                    TODO open youtube links separately.
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    Utils.getApp().startActivity(intent);
                }
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, "No external browser found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Cannot open url", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleModalBottomSheetDialogFragment(SavedArticle article) {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog_saved_articles, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        LinearLayout share = view.findViewById(R.id.share_bottom_sheet_dialog_main);
        LinearLayout delete = view.findViewById(R.id.delete_bottom_sheet_dialog_main);

        share.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

            String url = article.getArticleUrl();

            if (url == null)
                url = article.getArticleMedia();

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, article.getArticleTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, url);
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.dialog_share_to)));
        });

        delete.setOnClickListener(v -> {
            removeArticle(article);
            bottomSheetDialog.dismiss();
        });
    }

    private void removeArticle(SavedArticle article) {
        String articleId = AppUtils.getArticleIdHash(article.getArticleTitle(), article.getArticleUrl(), article.getArticleMedia());

        if (appDatabase.savedArticlesDao().checkIfSavedArticleExists(articleId)) {
            try {
                if (articleId != null) {
                    appDatabase.savedArticlesDao().removeSavedArticle(articleId);
                    savedArticlesAdapter.remove(article);
                }
            } catch (Exception e) {
                Toast.makeText(context, "Article failed to delete", Toast.LENGTH_SHORT).show();
            } finally {
                Toast.makeText(context, "Article deleted", Toast.LENGTH_SHORT).show();
            }
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
