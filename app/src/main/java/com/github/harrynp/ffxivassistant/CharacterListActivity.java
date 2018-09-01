package com.github.harrynp.ffxivassistant;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.harrynp.ffxivassistant.data.pojo.charactersearch.CharacterResult;
import com.github.harrynp.ffxivassistant.databinding.ActivityCharacterListBinding;
import com.github.harrynp.ffxivassistant.network.chromecustomtab.CustomTabActivityHelper;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.github.harrynp.ffxivassistant.CharacterListFragment.CHARACTER_RESULTS;
import static com.github.harrynp.ffxivassistant.CharacterListFragment.FAVORITES;
import static com.github.harrynp.ffxivassistant.CharacterViewerFragment.CHARACTER_RESULT;


public class CharacterListActivity extends AppCompatActivity implements CharacterListFragment.OnCharacterSearchResultClickListener,
        CustomTabActivityHelper.ConnectionCallback{
    private ActivityCharacterListBinding mBinding;
    public List<Parcelable> mCharacterResults;
    private CustomTabActivityHelper customTabActivityHelper;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_character_list);
        Toolbar toolbar = mBinding.characterResultsToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        customTabActivityHelper = new CustomTabActivityHelper();
        customTabActivityHelper.setConnectionCallback(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CHARACTER_RESULTS)) {
                mCharacterResults = savedInstanceState.getParcelableArrayList(CHARACTER_RESULTS);
            }
        }
        CharacterListFragment characterListFragment;
        if(getIntent().hasExtra(FAVORITES)) {
            getSupportActionBar().setTitle(getString(R.string.favorited_characters_title));
            characterListFragment = CharacterListFragment.newInstance(null, true);
        } else {
            if(getIntent().hasExtra(CHARACTER_RESULTS)) {
                mCharacterResults = getIntent().getParcelableArrayListExtra(CHARACTER_RESULTS);
            }
            characterListFragment = CharacterListFragment.newInstance(mCharacterResults, false);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fl_character_results_container, characterListFragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(CHARACTER_RESULTS, (ArrayList<? extends Parcelable>) mCharacterResults);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                } else {
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    public void onCustomTabsConnected() {

    }

    @Override
    public void onCustomTabsDisconnected() {

    }

    @Override
    public void OnCharacterSearchResultClick(CharacterResult characterResult, int adapterPosition) {
        Intent intent = new Intent(this, CharacterViewerActivity.class);
        intent.putExtra(CHARACTER_RESULT, Parcels.wrap(characterResult));
        startActivity(intent);
//        String id = characterResult.getUrlApi().substring(32);
//        if (id != null && !id.isEmpty()) {
//            String url = "https://na.finalfantasyxiv.com/lodestone/character/" + id;
//            openChromeCustomTab(url);
//        }
    }
}
