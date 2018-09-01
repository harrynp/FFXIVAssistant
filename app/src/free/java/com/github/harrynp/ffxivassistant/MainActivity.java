package com.github.harrynp.ffxivassistant;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.harrynp.ffxivassistant.data.pojo.devtracker.Post;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Maintenance;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Notice;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Status;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Topic;
import com.github.harrynp.ffxivassistant.databinding.ActivityMainBinding;
import com.github.harrynp.ffxivassistant.network.chromecustomtab.CustomTabActivityHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TopicsFragment.OnTopicClickListener, NoticesFragment.OnNoticeClickListener,
        MaintenanceFragment.OnMaintenanceClickListener, StatusFragment.OnStatusClickListener,
        DevTrackerFragment.OnDevTrackerClickListener, CustomTabActivityHelper.ConnectionCallback {
    private CustomTabActivityHelper customTabActivityHelper;
    private ActivityMainBinding mBinding;
    private AdView mAdView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private final String MAIN_ACTIVITY_FRAGMENT = "MAIN_ACTIVITY_FRAGMENT";

    private void loadBannerAd(){
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(mBinding.toolbar);

        customTabActivityHelper = new CustomTabActivityHelper();
        customTabActivityHelper.setConnectionCallback(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mBinding.navView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null){
            NewsFragment newsFragment = NewsFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, newsFragment)
                    .commit();
            mBinding.navView.getMenu().getItem(0).setChecked(true);
        } else {
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, MAIN_ACTIVITY_FRAGMENT);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mBinding.drawerLayout, mBinding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mAdView = mBinding.adView;
        loadBannerAd();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        getSupportFragmentManager().putFragment(outState, MAIN_ACTIVITY_FRAGMENT, fragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customTabActivityHelper.setConnectionCallback(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        customTabActivityHelper.bindCustomTabsService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        customTabActivityHelper.unbindCustomTabsService(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();


        if (id == R.id.nav_news) {
            NewsFragment newsFragment = NewsFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, newsFragment)
                    .commit();
        } else if (id == R.id.nav_dev_tracker) {
            DevTrackerFragment devTrackerFragment = DevTrackerFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, devTrackerFragment)
                    .commit();
        } else if (id == R.id.nav_profile) {
            CharacterSearchFragment characterSearchFragment = CharacterSearchFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, characterSearchFragment)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCustomTabsConnected() {

    }

    @Override
    public void onCustomTabsDisconnected() {

    }

    private void openChromeCustomTab(String url){
        Uri uri = Uri.parse(url);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(customTabActivityHelper.getSession());
        builder.setToolbarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        Bitmap backButton = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_back_white_24dp);
        builder.setCloseButtonIcon(backButton);
        CustomTabsIntent customTabsIntent = builder.build();
        CustomTabActivityHelper.openCustomTab(this, customTabsIntent, uri,
                new CustomTabActivityHelper.CustomTabFallback() {
                    @Override
                    public void openUri(Activity activity, Uri uri) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        activity.startActivity(intent);
                    }
                });
    }

    @Override
    public void OnTopicClickListener(Topic topic, int adapterPosition) {
        if (topic.getUrl() != null && !topic.getUrl().isEmpty()) {
            openChromeCustomTab(topic.getUrl());
        }
    }

    @Override
    public void OnNoticeClickListener(Notice notice, int adapterPosition) {
        if (notice.getUrl() != null && !notice.getUrl().isEmpty()) {
            openChromeCustomTab(notice.getUrl());
        }
    }

    @Override
    public void OnMaintenanceClickListener(Maintenance maintenance, int adapterPosition) {
        if (maintenance.getUrl() != null && !maintenance.getUrl().isEmpty()) {
            openChromeCustomTab(maintenance.getUrl());
        }
    }

    @Override
    public void OnStatusClickListener(Status status, int adapterPosition) {
        if (status.getUrl() != null && !status.getUrl().isEmpty()) {
            openChromeCustomTab(status.getUrl());
        }
    }

    @Override
    public void OnDevTrackerClickListener(Post post, int adapterPosition) {
        String url = post.getThread().getUrl();
        if (url != null && !url.isEmpty()) {
            openChromeCustomTab(url);
        }
    }
}
