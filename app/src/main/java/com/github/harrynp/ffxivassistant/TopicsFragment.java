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

import com.github.harrynp.ffxivassistant.adapters.lodestone.TopicsAdapter;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Topic;
import com.github.harrynp.ffxivassistant.databinding.FragmentTopicsBinding;
import com.github.harrynp.ffxivassistant.utils.GridAutofitLayoutManager;
import com.github.harrynp.ffxivassistant.utils.UpdatableFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class TopicsFragment extends Fragment implements TopicsAdapter.TopicsAdapterOnClickHandler, UpdatableFragment {

    private FragmentTopicsBinding mBinding;
    private OnTopicClickListener mListener;
    private TopicsAdapter topicsAdapter;
    public static String TOPICS_EXTRA = "TOPICS_EXTRA";
    private FirebaseAnalytics mFirebaseAnalytics;


    public TopicsFragment(){}

    public static TopicsFragment newInstance(List<Parcelable> topics){
        TopicsFragment fragment = new TopicsFragment();
        Bundle args = new Bundle();
        if (topics != null){
            args.putParcelableArrayList(TOPICS_EXTRA, (ArrayList<? extends Parcelable>) topics);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_topics, container, false);
        topicsAdapter = new TopicsAdapter(getContext(), this);
        GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(getContext(), 800);
        mBinding.rvTopics.setLayoutManager(layoutManager);
        mBinding.rvTopics.setAdapter(topicsAdapter);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        Bundle args = getArguments();
        if (args.containsKey(TOPICS_EXTRA)){
            for (Parcelable parcelable : args.getParcelableArrayList(TOPICS_EXTRA)){
                Topic topic = Parcels.unwrap(parcelable);
                topicsAdapter.addTopic(topic);
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
    public void onClick(Topic topic, int adapterPosition) {
        mListener.OnTopicClickListener(topic, adapterPosition);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTopicClickListener) {
            mListener = (OnTopicClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTopicClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void update(List<Parcelable> updateData) {
        topicsAdapter.clear();
        for (Parcelable parcelable : updateData){
            Topic topic = Parcels.unwrap(parcelable);
            topicsAdapter.addTopic(topic);
        }
    }

    public interface OnTopicClickListener {
        void OnTopicClickListener(Topic topic, int adapterPosition);
    }
}
