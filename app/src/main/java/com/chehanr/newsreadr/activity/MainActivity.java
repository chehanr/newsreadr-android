package com.chehanr.newsreadr.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chehanr.newsreadr.R;
import com.chehanr.newsreadr.adapter.ArticlesAdapter;
import com.chehanr.newsreadr.database.AppDatabase;
import com.chehanr.newsreadr.database.entity.SavedArticle;
import com.chehanr.newsreadr.listener.EndlessRecyclerViewScrollListener;
import com.chehanr.newsreadr.model.Article;
import com.chehanr.newsreadr.model.ArticleResponse;
import com.chehanr.newsreadr.rest.ApiClient;
import com.chehanr.newsreadr.rest.ApiInterface;
import com.chehanr.newsreadr.util.AppUtils;
import com.chehanr.newsreadr.util.NetworkUtils;
import com.chehanr.newsreadr.util.RegexUtils;
import com.chehanr.newsreadr.util.SnackbarUtils;
import com.chehanr.newsreadr.util.Utils;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static String LIST_STATE_KEY = "recycler_list_state";
    public final static Integer INIT_PAGE = 1;
    private final static String TAG = MainActivity.class.getSimpleName();

    private Context context;

    private LinearLayoutManager linearLayoutManager;
    private View rootView;
    private ArticlesAdapter articlesAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private ProgressBar progressBar;

    private ApiInterface apiInterface;
    private SharedPreferences sharedPreferences;
    private Parcelable state;

    private AppDatabase appDatabase;

    private String pageUrl;
    private int currentPage, availablePages;

    private Boolean prefListAnimation, prefUseInAppBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.init(getApplication());

        context = Utils.getApp().getBaseContext();

        rootView = findViewById(android.R.id.content);
        linearLayoutManager = new LinearLayoutManager(context);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        articlesAdapter = new ArticlesAdapter(context);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                page += 1;
                loadDataFromApi(page);
            }
        };

        RecyclerView articlesRecyclerView = findViewById(R.id.articles_recycler_view);
        articlesRecyclerView.setLayoutManager(linearLayoutManager);
        articlesRecyclerView.setAdapter(articlesAdapter);
        articlesRecyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);

        articlesAdapter.setOnItemClickListener(new ArticlesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                handleUrlOpening(articlesAdapter.getItem(position));
            }

            @Override
            public void onItemLongClick(int position, View v) {
                handleModalBottomSheetDialogFragment(articlesAdapter.getItem(position));
            }
        });

        progressBar = findViewById(R.id.articles_progressBar);

        appDatabase = AppDatabase.getDatabase(context);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        handleSharedPreferences();

        loadDataFromApi(INIT_PAGE);
    }

    private void loadDataFromApi(int page) {
        showProgressBar(true);

        Call<ArticleResponse> call = apiInterface.getArticles(page);
        call.enqueue(new Callback<ArticleResponse>() {
            int apiStatusCode, remoteStatusCode = 999;

            @Override
            public void onResponse(@NonNull Call<ArticleResponse> call, @NonNull Response<ArticleResponse> response) {
                showProgressBar(false);

                ArticleResponse articleResponse = response.body();
                apiStatusCode = response.code();

                if (!response.isSuccessful()) {
                    handleNetworkIssues(apiStatusCode, remoteStatusCode);
                    return;
                }

                if (articleResponse != null) {
                    if (!NetworkUtils.isConnected())
                        handleOffline(false);

                    List<Article> articles = articleResponse.getArticles();
                    pageUrl = articleResponse.getPageUrl();
                    remoteStatusCode = articleResponse.getRemoteStatusCode();
                    currentPage = articleResponse.getPage();
                    availablePages = articleResponse.getAvailablePages();

                    if (articles != null) {
                        Log.d(TAG, String.format("loaded page: %d/%d", currentPage, availablePages));
                        articlesAdapter.addAll(articles);
                        articlesAdapter.notifyDataSetChanged();
                    } else if (availablePages <= currentPage - 1) {
                        SnackbarUtils.with(rootView)
                                .setMessage("You browsed all available articles")
                                .show();
                    } else {
                        handleNetworkIssues(apiStatusCode, remoteStatusCode);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArticleResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                showProgressBar(false);
                handleNetworkIssues(apiStatusCode, remoteStatusCode);
            }
        });
    }


    private void handleSharedPreferences() {
        // Handle list animation pref.
        prefListAnimation = sharedPreferences.getBoolean("list_animation_switch", true);

        if (articlesAdapter != null) {
            handleAnimation();
        }
        // Handle browser pref.
        prefUseInAppBrowser = sharedPreferences.getBoolean("use_in_app_browser_switch", true);
    }


    private void handleAnimation() {
//        TODO make changes.
        if (prefListAnimation) {
//            articlesAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        }
//        articlesRecyclerView.notify(articlesAdapter);
    }


    private void handleOffline(boolean mode) {
        String msg;

        if (mode)
            msg = "Device offline";
        else
//            TODO add last response dateTime.
            msg = "Browsing offline";

        SnackbarUtils.with(rootView)
                .setMessage(msg)
                .setDuration(SnackbarUtils.LENGTH_LONG)
                .show();
    }


    private void handleNetworkIssues(int apiStatusCode, int remoteStatusCode) {
        String msg;

        if (!NetworkUtils.isConnected()) {
            handleOffline(true);
        } else if (apiStatusCode != 999 && apiStatusCode != 200) {
            msg = String.format(Locale.getDefault(), "API unresponsive (Code: %d)", apiStatusCode);
            SnackbarUtils.with(rootView)
                    .setMessage(msg)
                    .setDuration(SnackbarUtils.LENGTH_LONG)
                    .show();
        } else if (remoteStatusCode != 999 && remoteStatusCode != 200) {
            msg = String.format(Locale.getDefault(), "Website unreachable (Code: %d)", remoteStatusCode);
            SnackbarUtils.with(rootView)
                    .setMessage(msg)
                    .setDuration(SnackbarUtils.LENGTH_LONG)
                    .show();
        }
    }


    private void handleUrlOpening(@NonNull Article article) {
        String url = article.getArticleUrl();

        if (url == null)
            url = article.getArticleMedia();

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

    private void handleModalBottomSheetDialogFragment(Article article) {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog_main, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        LinearLayout share = view.findViewById(R.id.share_bottom_sheet_dialog_main);
        LinearLayout save = view.findViewById(R.id.save_bottom_sheet_dialog_main);

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

        save.setOnClickListener(v -> {
            saveArticle(article);
            bottomSheetDialog.dismiss();
        });
    }

    private void handleRefresh() {
        articlesAdapter.removeAll();
        endlessRecyclerViewScrollListener.resetState();
        loadDataFromApi(INIT_PAGE);
    }

    private void saveArticle(Article article) {
        String articleId = AppUtils.getArticleIdHash(article.getArticleTitle(), article.getArticleUrl(), article.getArticleMedia());

        if (appDatabase.savedArticlesDao().checkIfSavedArticleExists(articleId)) {
            Toast.makeText(context, "Article already saved", Toast.LENGTH_SHORT).show();
        } else {
            try {
                if (articleId != null) {
                    appDatabase.savedArticlesDao().addSaveArticle(new SavedArticle(articleId, article));
                }
            } catch (Exception e) {
                Toast.makeText(context, "Article failed to save", Toast.LENGTH_SHORT).show();
            } finally {
                Toast.makeText(context, "Article saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showProgressBar(Boolean visibility) {
        if (visibility)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save list state
        state = linearLayoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, state);
    }

    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        // Retrieve list state and list/item positions
        if (inState != null)
            state = inState.getParcelable(LIST_STATE_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleSharedPreferences();
        if (state != null) {
            linearLayoutManager.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Integer id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            handleRefresh();
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Integer id = item.getItemId();

        if (id == R.id.nav_archives) {

        } else if (id == R.id.nav_saved) {
            Intent intent = new Intent(MainActivity.this, SavedArticlesActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
