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

import com.github.harrynp.ffxivassistant.adapters.lodestone.NoticesAdapter;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Notice;
import com.github.harrynp.ffxivassistant.databinding.FragmentNoticesBinding;
import com.github.harrynp.ffxivassistant.utils.GridAutofitLayoutManager;
import com.github.harrynp.ffxivassistant.utils.UpdatableFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class NoticesFragment extends Fragment implements NoticesAdapter.NoticesAdapterOnClickHandler, UpdatableFragment {
    private FragmentNoticesBinding mBinding;
    private OnNoticeClickListener mListener;
    private NoticesAdapter noticesAdapter;
    public static String NOTICES_EXTRA = "NOTICES_EXTRA";
    private FirebaseAnalytics mFirebaseAnalytics;

    public NoticesFragment(){}

    public static NoticesFragment newInstance(List<Parcelable> notices){
        NoticesFragment fragment = new NoticesFragment();
        Bundle args = new Bundle();
        if (notices != null){
            args.putParcelableArrayList(NOTICES_EXTRA, (ArrayList<? extends Parcelable>) notices);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notices, container, false);
        noticesAdapter = new NoticesAdapter(getContext(), this);
        GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(getContext(), 800);
        mBinding.rvNotices.setLayoutManager(layoutManager);
        mBinding.rvNotices.setAdapter(noticesAdapter);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        Bundle args = getArguments();
        if (args.containsKey(NOTICES_EXTRA)){
            for (Parcelable parcelable : args.getParcelableArrayList(NOTICES_EXTRA)){
                Notice notice = Parcels.unwrap(parcelable);
                noticesAdapter.addNotice(notice);
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
    public void onClick(Notice notice, int adapterPosition) {
        mListener.OnNoticeClickListener(notice, adapterPosition);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNoticeClickListener) {
            mListener = (OnNoticeClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNoticeClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void update(List<Parcelable> updateData) {
        noticesAdapter.clear();
        for (Parcelable parcelable : updateData){
            Notice notice = Parcels.unwrap(parcelable);
            noticesAdapter.addNotice(notice);
        }
    }

    public interface OnNoticeClickListener {
        void OnNoticeClickListener(Notice notice, int adapterPosition);
    }
}
