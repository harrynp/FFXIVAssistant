package com.github.harrynp.ffxivassistant;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.harrynp.ffxivassistant.data.pojo.charactersearch.CharacterResult;
import com.github.harrynp.ffxivassistant.databinding.ActivityCharacterViewerBinding;

import org.parceler.Parcels;

import static com.github.harrynp.ffxivassistant.CharacterViewerFragment.CHARACTER_RESULT;


public class CharacterViewerActivity extends AppCompatActivity {
    private ActivityCharacterViewerBinding mBinding;
    private CharacterResult mCharacterResult;
    private final String CHARACTER_VIEWER_FRAGMENT = "CHARACTER_VIEWER_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_character_viewer);
        setSupportActionBar(mBinding.characterViewerToolbar);

        CharacterViewerFragment characterViewerFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null){
            if(getIntent().hasExtra(CHARACTER_RESULT)) {
                mCharacterResult = Parcels.unwrap(getIntent().getParcelableExtra(CHARACTER_RESULT));
            }
            characterViewerFragment = CharacterViewerFragment.newInstance(mCharacterResult);
            fragmentManager.beginTransaction()
                    .replace(R.id.fl_character_viewer_container, characterViewerFragment)
                    .commit();

        } else {
            if (savedInstanceState.containsKey(CHARACTER_RESULT)) {
                mCharacterResult = Parcels.unwrap(savedInstanceState.getParcelable(CHARACTER_RESULT));
            }
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, CHARACTER_VIEWER_FRAGMENT);
            fragmentManager.beginTransaction()
                    .replace(R.id.fl_character_viewer_container, fragment)
                    .commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(CHARACTER_RESULT, Parcels.wrap(mCharacterResult));
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_character_viewer_container);
        getSupportFragmentManager().putFragment(outState, CHARACTER_VIEWER_FRAGMENT, fragment);
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
}
