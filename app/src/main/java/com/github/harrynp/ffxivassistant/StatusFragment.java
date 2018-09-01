package com.github.harrynp.ffxivassistant;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.harrynp.ffxivassistant.adapters.lodestone.StatusAdapter;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Status;
import com.github.harrynp.ffxivassistant.databinding.FragmentStatusBinding;
import com.github.harrynp.ffxivassistant.utils.GridAutofitLayoutManager;
import com.github.harrynp.ffxivassistant.utils.UpdatableFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class StatusFragment extends Fragment implements StatusAdapter.StatusAdapterOnClickHandler, UpdatableFragment {
    private FragmentStatusBinding mBinding;
    private OnStatusClickListener mListener;
    private StatusAdapter statusAdapter;
    public static String STATUS_EXTRA = "STATUS_EXTRA";
    private FirebaseAnalytics mFirebaseAnalytics;


    public StatusFragment(){}

    public static StatusFragment newInstance(List<Parcelable> statuses){
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        if (statuses != null){
            args.putParcelableArrayList(STATUS_EXTRA, (ArrayList<? extends Parcelable>) statuses);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_status, container, false);
        statusAdapter = new StatusAdapter(getContext(), this);
        GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(getContext(), 800);
        mBinding.rvStatus.setLayoutManager(layoutManager);
        mBinding.rvStatus.setAdapter(statusAdapter);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        Bundle args = getArguments();
        if (args.containsKey(STATUS_EXTRA)){
            for (Parcelable parcelable : args.getParcelableArrayList(STATUS_EXTRA)){
                Status status = Parcels.unwrap(parcelable);
                statusAdapter.addStatus(status);
            }
        }
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(getActivity(), this.getClass().getSimpleName(), this.getClass().getSimpleName());
    }

    @Override
    public void onClick(Status status, int adapterPosition) {
        mListener.OnStatusClickListener(status, adapterPosition);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStatusClickListener) {
            mListener = (OnStatusClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStatusClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void update(List<Parcelable> updateData) {
        statusAdapter.clear();
        for (Parcelable parcelable : updateData){
            Status status = Parcels.unwrap(parcelable);
            statusAdapter.addStatus(status);
        }
    }

    public interface OnStatusClickListener{
        void OnStatusClickListener(Status status, int adapterPosition);
    }
}
