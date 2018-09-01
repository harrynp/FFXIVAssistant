package com.github.harrynp.ffxivassistant;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.harrynp.ffxivassistant.adapters.lodestone.MaintenanceAdapter;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Maintenance;
import com.github.harrynp.ffxivassistant.databinding.FragmentMaintenanceBinding;
import com.github.harrynp.ffxivassistant.utils.GridAutofitLayoutManager;
import com.github.harrynp.ffxivassistant.utils.UpdatableFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceFragment extends Fragment implements MaintenanceAdapter.MaintenanceAdapterOnClickHandler, UpdatableFragment {
    private FragmentMaintenanceBinding mBinding;
    private OnMaintenanceClickListener mListener;
    private MaintenanceAdapter maintenanceAdapter;
    public static String MAINTENANCE_EXTRA = "MAINTENANCE_EXTRA";
    private FirebaseAnalytics mFirebaseAnalytics;


    public MaintenanceFragment(){}

    public static MaintenanceFragment newInstance(List<Parcelable> maintenance){
        MaintenanceFragment fragment = new MaintenanceFragment();
        Bundle args = new Bundle();
        if (maintenance != null){
            args.putParcelableArrayList(MAINTENANCE_EXTRA, (ArrayList<? extends Parcelable>) maintenance);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_maintenance, container, false);
        maintenanceAdapter = new MaintenanceAdapter(getContext(), this);
        GridAutofitLayoutManager layoutManager = new GridAutofitLayoutManager(getContext(), 800);
        mBinding.rvMaintenance.setLayoutManager(layoutManager);
        mBinding.rvMaintenance.setAdapter(maintenanceAdapter);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        Bundle args = getArguments();
        if (args.containsKey(MAINTENANCE_EXTRA)){
            for (Parcelable parcelable : args.getParcelableArrayList(MAINTENANCE_EXTRA)){
                Maintenance maintenance = Parcels.unwrap(parcelable);
                maintenanceAdapter.addMaintenance(maintenance);
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
    public void onClick(Maintenance maintenance, int adapterPosition) {
        mListener.OnMaintenanceClickListener(maintenance, adapterPosition);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMaintenanceClickListener) {
            mListener = (OnMaintenanceClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMaintenanceClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void update(List<Parcelable> updateData) {
        maintenanceAdapter.clear();
        for (Parcelable parcelable : updateData){
            Maintenance maintenance = Parcels.unwrap(parcelable);
            maintenanceAdapter.addMaintenance(maintenance);
        }
    }

    public interface OnMaintenanceClickListener {
        void OnMaintenanceClickListener(Maintenance maintenance, int adapterPosition);
    }

}
