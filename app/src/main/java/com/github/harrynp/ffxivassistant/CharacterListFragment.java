package com.github.harrynp.ffxivassistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.harrynp.ffxivassistant.adapters.charactersearch.CharacterSearchResultsAdapter;
import com.github.harrynp.ffxivassistant.data.database.CharactersContract;
import com.github.harrynp.ffxivassistant.data.pojo.charactersearch.CharacterResult;
import com.github.harrynp.ffxivassistant.databinding.FragmentCharacterListBinding;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class CharacterListFragment extends Fragment implements CharacterSearchResultsAdapter.CharacterSearchResultsAdapterOnClickHandler {
    private FragmentCharacterListBinding mBinding;
    private CharacterSearchResultsAdapter characterSearchResultsAdapter;
    private OnCharacterSearchResultClickListener mListener;
    public static String CHARACTER_RESULTS = "CHARACTER_RESULTS";
    public static String FAVORITES = "FAVORITES";
    private static final int FETCH_CHARACTERS_LOADER = 23;


    public CharacterListFragment() {
    }

    public static CharacterListFragment newInstance(List<Parcelable> characterResults, boolean favorites){
        CharacterListFragment fragment = new CharacterListFragment();
        Bundle args = new Bundle();
        if (characterResults != null){
            args.putParcelableArrayList(CHARACTER_RESULTS, (ArrayList<? extends Parcelable>) characterResults);
        }
        args.putBoolean(FAVORITES, favorites);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_character_list, container, false);
        characterSearchResultsAdapter = new CharacterSearchResultsAdapter(getContext(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBinding.rvCharacterResults.setLayoutManager(layoutManager);
        mBinding.rvCharacterResults.setAdapter(characterSearchResultsAdapter);
        Bundle args = getArguments();
        if (args.containsKey(CHARACTER_RESULTS)){
            mBinding.pbCharacterResultsLoadingIndicator.setVisibility(View.VISIBLE);
            mBinding.rvCharacterResults.setVisibility(View.INVISIBLE);
            for (Parcelable parcelable : args.getParcelableArrayList(CHARACTER_RESULTS)){
                CharacterResult characterResult = Parcels.unwrap(parcelable);
                characterSearchResultsAdapter.addCharacterResult(characterResult);
            }
            mBinding.pbCharacterResultsLoadingIndicator.setVisibility(View.INVISIBLE);
            mBinding.rvCharacterResults.setVisibility(View.VISIBLE);
            if (characterSearchResultsAdapter.getItemCount() == 0){
                mBinding.tvNoResults.setVisibility(View.VISIBLE);
            }
        } else if (args.containsKey(FAVORITES)){
            getActivity().getSupportLoaderManager().restartLoader(FETCH_CHARACTERS_LOADER, null, charactersDbListener);
        }

        return mBinding.getRoot();
    }

    @Override
    public void onClick(CharacterResult characterResult, int adapterPosition) {
        mListener.OnCharacterSearchResultClick(characterResult, adapterPosition);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCharacterSearchResultClickListener) {
            mListener = (OnCharacterSearchResultClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCharacterSearchResultClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private LoaderManager.LoaderCallbacks<CharacterResult[]> charactersDbListener = new LoaderManager.LoaderCallbacks<CharacterResult[]>() {
        @SuppressLint("StaticFieldLeak")
        @NonNull
        @Override
        public Loader<CharacterResult[]> onCreateLoader(int id, @Nullable Bundle args) {
            switch (id){
                case FETCH_CHARACTERS_LOADER:

                    return new AsyncTaskLoader<CharacterResult[]>(getContext()) {

                        @Override
                        protected void onStartLoading() {
                            super.onStartLoading();
                            mBinding.rvCharacterResults.setVisibility(View.INVISIBLE);
                            mBinding.pbCharacterResultsLoadingIndicator.setVisibility(View.VISIBLE);
                            forceLoad();
                        }

                        @Nullable
                        @Override
                        public CharacterResult[] loadInBackground() {
                            Cursor cursor = getActivity().getContentResolver().query(
                                    CharactersContract.CharactersEntry.CONTENT_URI,
                                    null,
                                    null,
                                    null,
                                    CharactersContract.CharactersEntry.COLUMN_NAME);
                            CharacterResult[] characterResults = new CharacterResult[cursor.getCount()];
                            int position = 0;
                            while (cursor.moveToNext()){
                                characterResults[position] = new CharacterResult(
                                        cursor.getString(cursor.getColumnIndex(CharactersContract.CharactersEntry.COLUMN_NAME)),
                                        cursor.getString(cursor.getColumnIndex(CharactersContract.CharactersEntry.COLUMN_SERVER)),
                                        cursor.getString(cursor.getColumnIndex(CharactersContract.CharactersEntry.COLUMN_ICON_PATH)),
                                        cursor.getInt(cursor.getColumnIndex(CharactersContract.CharactersEntry.COLUMN_CHARACTER_ID)),
                                        cursor.getString(cursor.getColumnIndex(CharactersContract.CharactersEntry.COLUMN_URL_API)),
                                        cursor.getString(cursor.getColumnIndex(CharactersContract.CharactersEntry.COLUMN_URL_XIVDB))
                                );
                                ++position;
                            }
                            return characterResults;
                        }
                    };
                default:
                    throw new RuntimeException("Loader Not Implemented: " + id);
            }
        }

        @Override
        public void onLoadFinished(@NonNull Loader<CharacterResult[]> loader, CharacterResult[] data) {
            if (data.length == 0){
                mBinding.tvNoResults.setText(getString(R.string.no_characters_saved_error));
                mBinding.tvNoResults.setVisibility(View.VISIBLE);
            } else {
                characterSearchResultsAdapter.clear();
                for (CharacterResult characterResult : data){
                    characterSearchResultsAdapter.addCharacterResult(characterResult);
                }
                mBinding.rvCharacterResults.setVisibility(View.VISIBLE);
            }
            mBinding.pbCharacterResultsLoadingIndicator.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<CharacterResult[]> loader) {

        }
    };

    public interface OnCharacterSearchResultClickListener {
        void OnCharacterSearchResultClick(CharacterResult characterResult, int adapterPosition);
    }
}
