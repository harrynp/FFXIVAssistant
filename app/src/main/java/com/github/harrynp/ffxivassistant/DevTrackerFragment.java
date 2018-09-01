package com.github.harrynp.ffxivassistant;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.harrynp.ffxivassistant.adapters.devtracker.DevTrackerAdapter;
import com.github.harrynp.ffxivassistant.data.pojo.devtracker.Post;
import com.github.harrynp.ffxivassistant.databinding.FragmentDevTrackerBinding;
import com.github.harrynp.ffxivassistant.network.DevTrackerListener;
import com.github.harrynp.ffxivassistant.network.XIVDBApiClient;
import com.github.harrynp.ffxivassistant.utils.GridAutofitLayoutManager;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.parceler.Parcels;

import java.util.ArrayList;

public class DevTrackerFragment extends Fragment implements DevTrackerListener<Post>, DevTrackerAdapter.DevTrackerAdapterOnClickHandler {
    private FragmentDevTrackerBinding mBinding;
    private XIVDBApiClient xivdbApiClient;
    private OnDevTrackerClickListener mListener;
    private DevTrackerAdapter devTrackerAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private final String POST_LIST_SAVE_STATE = "POST_LIST_SAVE_STATE";


    public DevTrackerFragment(){}

    public static DevTrackerFragment newInstance() {
        DevTrackerFragment fragment = new DevTrackerFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dev_tracker, container, false);
        xivdbApiClient = new XIVDBApiClient();
        devTrackerAdapter = new DevTrackerAdapter(getContext(), this);
        GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(getContext(), 800);
        mBinding.rvDevtracker.setLayoutManager(layoutManager);
        mBinding.rvDevtracker.setAdapter(devTrackerAdapter);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        mBinding.devtrackerSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateDevTracker();
            }
        });
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null){
            updateDevTracker();
        } else {
            if (savedInstanceState.containsKey(POST_LIST_SAVE_STATE)){
                for (Parcelable parcelable : savedInstanceState.getParcelableArrayList(POST_LIST_SAVE_STATE)){
                    devTrackerAdapter.addPost((Post) Parcels.unwrap(parcelable));
                }
            }
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        ArrayList<Parcelable> parcelables = new ArrayList<>();
        for (Post post : devTrackerAdapter.getPostList()){
            parcelables.add(Parcels.wrap(post));
        }
        if (!parcelables.isEmpty()){
            outState.putParcelableArrayList(POST_LIST_SAVE_STATE, parcelables);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(getActivity(), this.getClass().getSimpleName(), this.getClass().getSimpleName());
    }

    private void showDevTracker(){
        mBinding.tvDevtrackerErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mBinding.flDevtracker.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mBinding.flDevtracker.setVisibility(View.INVISIBLE);
        mBinding.tvDevtrackerErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void updateDevTracker(){
        if (isOnline()) {
            mBinding.pbDevtrackerLoadingIndicator.setVisibility(View.VISIBLE);
            xivdbApiClient.getDevTracker(this);
        } else {
            mBinding.devtrackerSwipeRefresh.setRefreshing(false);
            showErrorMessage();
        }
    }

    @Override
    public void onSuccess(ArrayList<Post> result) {
        mBinding.devtrackerSwipeRefresh.setRefreshing(false);
        devTrackerAdapter.clear();
        for (Post post : result){
            devTrackerAdapter.addPost(post);
        }
        mBinding.pbDevtrackerLoadingIndicator.setVisibility(View.INVISIBLE);
        showDevTracker();
    }

    @Override
    public void onFailure(Throwable throwable) {

    }

    @Override
    public void onClick(Post post, int adapterPosition) {
        mListener.OnDevTrackerClickListener(post, adapterPosition);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDevTrackerClickListener) {
            mListener = (OnDevTrackerClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDevTrackerClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnDevTrackerClickListener {
        void OnDevTrackerClickListener(Post post, int adapterPosition);
    }
}
