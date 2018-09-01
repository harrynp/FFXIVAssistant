package com.github.harrynp.ffxivassistant;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.github.harrynp.ffxivassistant.data.pojo.charactersearch.CharacterResult;
import com.github.harrynp.ffxivassistant.data.pojo.charactersearch.CharacterResults;
import com.github.harrynp.ffxivassistant.databinding.FragmentCharacterSearchBinding;
import com.github.harrynp.ffxivassistant.network.CharactersResultsListener;
import com.github.harrynp.ffxivassistant.network.XIVDBApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.github.harrynp.ffxivassistant.CharacterListFragment.CHARACTER_RESULTS;
import static com.github.harrynp.ffxivassistant.CharacterListFragment.FAVORITES;


public class CharacterSearchFragment extends Fragment implements CharactersResultsListener{
    private FragmentCharacterSearchBinding mBinding;
    private TextInputLayout mCharacterNameInputLayout;
    private TextInputEditText mCharacterNameEditText;
    private Spinner mServerSpinner;
    private XIVDBApiClient xivdbApiClient;
    private FirebaseAnalytics mFirebaseAnalytics;


    public CharacterSearchFragment(){}

    public static CharacterSearchFragment newInstance() {
        CharacterSearchFragment fragment = new CharacterSearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_character_search, container, false);
        xivdbApiClient = new XIVDBApiClient();
        mCharacterNameInputLayout = mBinding.ilSearchName;
        mCharacterNameEditText = mBinding.etSearchName;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.servers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mServerSpinner = mBinding.spinnerServers;
        mServerSpinner.setAdapter(adapter);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        final Button mSearchButton = mBinding.buttonSearch;
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCharacters();
            }
        });
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_character_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_favorite:
                Intent intent = new Intent(getActivity(), CharacterListActivity.class);
                intent.putExtra(FAVORITES, true);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(getActivity(), this.getClass().getSimpleName(), this.getClass().getSimpleName());
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void searchCharacters(){
        if (isOnline()) {
            String characterName = mCharacterNameEditText.getText().toString().trim();
            if (characterName != null && !characterName.isEmpty()) {
                String server = mServerSpinner.getSelectedItem().toString();
                if(server.equalsIgnoreCase("all")){
                    server = "";
                }
                mBinding.pbCharacterSearchLoadingIndicator.setVisibility(View.VISIBLE);
                xivdbApiClient.getCharacterResults(this, characterName, server);
            } else if (characterName.isEmpty()) {
                mCharacterNameInputLayout.setError(getString(R.string.name_empty_error));
            }
        } else {
            mBinding.tvCharacterSearchErrorMessageDisplay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSuccess(CharacterResults result) {
        mBinding.pbCharacterSearchLoadingIndicator.setVisibility(View.INVISIBLE);
        List<CharacterResult> characterResults = result.getCharacters().getResults();
        Intent intent = new Intent(getActivity(), CharacterListActivity.class);
        ArrayList<Parcelable> parcelableCharacterResults = new ArrayList<>();
        if (characterResults != null){
            for (CharacterResult characterResult : characterResults){
                parcelableCharacterResults.add(Parcels.wrap(characterResult));
            }
        }
        intent.putParcelableArrayListExtra(CHARACTER_RESULTS, parcelableCharacterResults);
        startActivity(intent);
    }

    @Override
    public void onFailure(Throwable throwable) {
        mBinding.pbCharacterSearchLoadingIndicator.setVisibility(View.INVISIBLE);
    }
}
