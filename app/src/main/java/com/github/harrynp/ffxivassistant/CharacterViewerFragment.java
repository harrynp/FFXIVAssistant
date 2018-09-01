package com.github.harrynp.ffxivassistant;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.github.harrynp.ffxivassistant.data.database.CharactersContract;
import com.github.harrynp.ffxivassistant.data.pojo.charactersearch.CharacterResult;
import com.github.harrynp.ffxivassistant.databinding.FragmentCharacterViewerBinding;
import com.github.harrynp.ffxivassistant.utils.CheckableFloatingActionButton;
import com.github.harrynp.ffxivassistant.utils.NestedWebView;

import org.parceler.Parcels;


/**
 * A placeholder fragment containing a simple view.
 */
public class CharacterViewerFragment extends Fragment {
    private FragmentCharacterViewerBinding mBinding;
    private CharacterResult mCharacterResult;
    private NestedWebView mWebView;
    private CheckableFloatingActionButton mFab;
    private boolean favorited;
    public static String CHARACTER_RESULT = "CHARACTER_RESULTS";
    private static final int ADD_CHARACTER_LOADER = 25;
    private static final int REMOVE_CHARACTER_LOADER = 26;

    public static CharacterViewerFragment newInstance(CharacterResult characterResult) {
        CharacterViewerFragment fragment = new CharacterViewerFragment();
        Bundle args = new Bundle();
        args.putParcelable(CHARACTER_RESULT, Parcels.wrap(characterResult));
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_character_viewer, container, false);

        if (savedInstanceState == null){
            Bundle args = getArguments();
            if (args.containsKey(CHARACTER_RESULT)) {
                mCharacterResult = Parcels.unwrap(args.getParcelable(CHARACTER_RESULT));
            }
        } else {
            if (savedInstanceState.containsKey(CHARACTER_RESULT)){
                mCharacterResult = Parcels.unwrap(savedInstanceState.getParcelable(CHARACTER_RESULT));
            }
        }

        mFab = mBinding.fabCharacterViewer;
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return isFavorited();
            }

            @Override
            protected void onPostExecute(Boolean isFavorited) {
                favorited = isFavorited;
                mFab.setChecked(favorited);
            }
        }.execute();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!favorited){
                    getActivity().getSupportLoaderManager().restartLoader(ADD_CHARACTER_LOADER, null, charactersDbListener).forceLoad();
                } else {
                    getActivity().getSupportLoaderManager().restartLoader(REMOVE_CHARACTER_LOADER, null, charactersDbListener).forceLoad();
                }
            }
        });

        mWebView = mBinding.wvCharacterViewer;
        mWebView.setOnScrollChangedCallback(new NestedWebView.OnScrollChangedCallback() {
            @Override
            public void onScrollChange(WebView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY && scrollY > 0) {
                    mFab.hide();
                }
                if (scrollY < oldScrollY) {
                    mFab.show();
                }
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mBinding.pbCharacterViewerLoadingIndicator.setProgress(newProgress);
                if (newProgress >= 100) {
                    mBinding.pbCharacterViewerLoadingIndicator.setVisibility(View.INVISIBLE);
                }
            }
        });


        if (mCharacterResult != null) {
            loadUrl();
        }


        return mBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(CHARACTER_RESULT, Parcels.wrap(mCharacterResult));
        super.onSaveInstanceState(outState);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void loadUrl() {
        if (isOnline()) {
            String url = "https://na.finalfantasyxiv.com/lodestone/character/" + mCharacterResult.getId();
            mBinding.tvCharacterViewerErrorMessageDisplay.setVisibility(View.INVISIBLE);
            mBinding.pbCharacterViewerLoadingIndicator.setVisibility(View.VISIBLE);
            mWebView.loadUrl(url);
        } else {
            mBinding.tvCharacterViewerErrorMessageDisplay.setVisibility(View.VISIBLE);
            Snackbar snackbar = Snackbar.make(mBinding.flCharacterViewer, "Internet connection not available.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadUrl();
                        }
                    });
            snackbar.show();
        }
    }

    private boolean isFavorited() {
        Cursor cursor = getContext().getContentResolver().query(
                CharactersContract.CharactersEntry.CONTENT_URI,
                null,
                CharactersContract.CharactersEntry.COLUMN_CHARACTER_ID + " = ?",
                new String[]{Integer.toString(mCharacterResult.getId())},
                null
        );
        return cursor.getCount() == 1;
    }

    private LoaderManager.LoaderCallbacks<Boolean> charactersDbListener = new LoaderManager.LoaderCallbacks<Boolean>() {

        @SuppressLint("StaticFieldLeak")
        @NonNull
        @Override
        public Loader<Boolean> onCreateLoader(int id, @Nullable Bundle args) {
            switch (id) {
                case ADD_CHARACTER_LOADER: {
                    return new AsyncTaskLoader<Boolean>(getContext()) {
                        @Override
                        public Boolean loadInBackground() {
                            if (mCharacterResult != null) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(CharactersContract.CharactersEntry.COLUMN_CHARACTER_ID, mCharacterResult.getId());
                                contentValues.put(CharactersContract.CharactersEntry.COLUMN_NAME, mCharacterResult.getName());
                                contentValues.put(CharactersContract.CharactersEntry.COLUMN_SERVER, mCharacterResult.getServer());
                                contentValues.put(CharactersContract.CharactersEntry.COLUMN_ICON_PATH, mCharacterResult.getIcon());
                                contentValues.put(CharactersContract.CharactersEntry.COLUMN_URL_API, mCharacterResult.getUrlApi());
                                contentValues.put(CharactersContract.CharactersEntry.COLUMN_URL_XIVDB, mCharacterResult.getUrlXivdb());
                                getContext().getContentResolver().insert(CharactersContract.CharactersEntry.CONTENT_URI, contentValues);
                                return true;
                            }
                            return false;
                        }
                    };

                }
                case REMOVE_CHARACTER_LOADER: {
                    return new AsyncTaskLoader<Boolean>(getContext()) {
                        @Override
                        public Boolean loadInBackground() {
                            getContext().getContentResolver().delete(
                                    CharactersContract.CharactersEntry.CONTENT_URI,
                                    CharactersContract.CharactersEntry.COLUMN_CHARACTER_ID + "=?",
                                    new String[]{Integer.toString(mCharacterResult.getId())});
                            return false;
                        }
                    };
                }
                default:
                    throw new RuntimeException("Loader Not Implemented: " + id);
            }
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean data) {
            favorited = data;
            mFab.setChecked(favorited);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Boolean> loader) {

        }
    };
}
