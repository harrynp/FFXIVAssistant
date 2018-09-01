package com.github.harrynp.ffxivassistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Lodestone;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Maintenance;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Notice;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Status;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Topic;
import com.github.harrynp.ffxivassistant.databinding.FragmentNewsBinding;
import com.github.harrynp.ffxivassistant.network.LodestoneListener;
import com.github.harrynp.ffxivassistant.network.XIVDBApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import timber.log.Timber;

public class NewsFragment extends Fragment implements LodestoneListener, LoaderManager.LoaderCallbacks<Lodestone> {
    private FragmentNewsBinding mBinding;
    private XIVDBApiClient xivdbApiClient;
    ArrayList<Parcelable> mTopicsList;
    ArrayList<Parcelable> mNoticesList;
    ArrayList<Parcelable> mMaintenanceList;
    ArrayList<Parcelable> mStatusList;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final int FETCH_LODESTONE_LOADER = 23;
    private final String XIVDB_BASE_URL = "https://xivdb.com/assets/";
    private final String LODESTONE_PATH = "lodestone.json";
    private final String TOPICS_LIST_SAVED_STATE = "TOPICS_LIST_SAVED_STATE";
    private final String NOTICES_LIST_SAVED_STATE = "NOTICES_LIST_SAVED_STATE";
    private final String MAINTENANCE_LIST_SAVED_STATE = "MAINTENANCE_LIST_SAVED_STATE";
    private final String STATUS_LIST_SAVED_STATE = "STATUS_LIST_SAVED_STATE";



    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_news, container, false);
        xivdbApiClient = new XIVDBApiClient();
        mTopicsList = new ArrayList<>();
        mNoticesList = new ArrayList<>();
        mMaintenanceList = new ArrayList<>();
        mStatusList = new ArrayList<>();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager = mBinding.newsViewpager;
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = mBinding.newsTabs;
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        mBinding.newsSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                updateNews();
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null){
            updateNews();
        } else {
            if (savedInstanceState.containsKey(TOPICS_LIST_SAVED_STATE)) {
                mTopicsList = savedInstanceState.getParcelableArrayList(TOPICS_LIST_SAVED_STATE);
            }
            if (savedInstanceState.containsKey(NOTICES_LIST_SAVED_STATE)) {
                mNoticesList = savedInstanceState.getParcelableArrayList(NOTICES_LIST_SAVED_STATE);
            }
            if (savedInstanceState.containsKey(MAINTENANCE_LIST_SAVED_STATE)) {
                mMaintenanceList = savedInstanceState.getParcelableArrayList(MAINTENANCE_LIST_SAVED_STATE);
            }
            if (savedInstanceState.containsKey(STATUS_LIST_SAVED_STATE)) {
                mStatusList = savedInstanceState.getParcelableArrayList(STATUS_LIST_SAVED_STATE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(TOPICS_LIST_SAVED_STATE, mTopicsList);
        outState.putParcelableArrayList(NOTICES_LIST_SAVED_STATE, mNoticesList);
        outState.putParcelableArrayList(MAINTENANCE_LIST_SAVED_STATE, mMaintenanceList);
        outState.putParcelableArrayList(STATUS_LIST_SAVED_STATE, mStatusList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(getActivity(), this.getClass().getSimpleName(), this.getClass().getSimpleName());
    }

    private void showViewpager(){
        mBinding.tvNewsErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mBinding.newsViewpager.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mBinding.newsViewpager.setVisibility(View.INVISIBLE);
        mBinding.tvNewsErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void updateNews(){
        if (isOnline()) {
            mBinding.pbNewsLoadingIndicator.setVisibility(View.VISIBLE);
//            xivdbApiClient.getLodestone(this);
            getActivity().getSupportLoaderManager().restartLoader(FETCH_LODESTONE_LOADER, null, this);
        } else {
            mBinding.newsSwipeRefresh.setRefreshing(false);
            showErrorMessage();
        }
    }

    @Override
    public void onSuccess(Lodestone result) {
        showViewpager();
        mBinding.pbNewsLoadingIndicator.setVisibility(View.INVISIBLE);
        mBinding.newsSwipeRefresh.setRefreshing(false);
        for (Topic topic : result.getTopics()){
            mTopicsList.add(Parcels.wrap(topic));
        }
        for (Notice notice : result.getNotices()){
            mNoticesList.add(Parcels.wrap(notice));
        }
        for (Maintenance maintenance : result.getMaintenance()){
            mMaintenanceList.add(Parcels.wrap(maintenance));
        }
        for (Status status : result.getStatus()){
            mStatusList.add(Parcels.wrap(status));
        }
        if (mSectionsPagerAdapter != null){
            mSectionsPagerAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onFailure(Throwable throwable) {

    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Lodestone> onCreateLoader(int id, @Nullable final Bundle args) {
        switch (id) {
            case FETCH_LODESTONE_LOADER:
                return new AsyncTaskLoader<Lodestone>(getContext()) {

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        showViewpager();
                        mBinding.pbNewsLoadingIndicator.setVisibility(View.VISIBLE);
                        forceLoad();
                    }

                    @Nullable
                    @Override
                    public Lodestone loadInBackground() {
                        Uri builtUri = Uri.parse(XIVDB_BASE_URL).buildUpon()
                                .appendPath(LODESTONE_PATH)
                                .build();
                        URL url = null;

                        try {
                            url = new URL(builtUri.toString());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        if (url == null) {
                            return null;
                        }

                        HttpURLConnection urlConnection = null;
                        try {
                            urlConnection = (HttpURLConnection) url.openConnection();
                        } catch (IOException e) {
                            Timber.e(e);
                            return null;
                        }

                        try {
                            urlConnection.setRequestMethod("GET");
                        } catch (ProtocolException e) {
                            Timber.e(e);
                            return null;
                        }
                        try {
                            urlConnection.connect();
                        } catch (IOException e) {
                            Timber.e(e);
                            return null;
                        }
                        Gson gson = new Gson();
                        JsonParser jsonParser = new JsonParser();
                        JsonElement root = null;
                        try {
                            root = jsonParser.parse(new InputStreamReader((InputStream) urlConnection.getContent()));
                        } catch (IOException e) {
                            Timber.e(e);
                            return null;
                        }
                        urlConnection.disconnect();
                        return gson.fromJson(root, Lodestone.class);
                    }
                };
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Lodestone> loader, Lodestone data) {
        mBinding.pbNewsLoadingIndicator.setVisibility(View.INVISIBLE);
        mBinding.newsSwipeRefresh.setRefreshing(false);
        if (data != null) {
            for (Topic topic : data.getTopics()) {
                mTopicsList.add(Parcels.wrap(topic));
            }
            for (Notice notices : data.getNotices()) {
                mNoticesList.add(Parcels.wrap(notices));
            }
            for (Maintenance maintenance : data.getMaintenance()) {
                mMaintenanceList.add(Parcels.wrap(maintenance));
            }
            for (Status status : data.getStatus()) {
                mStatusList.add(Parcels.wrap(status));
            }
            mSectionsPagerAdapter.notifyDataSetChanged();
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Lodestone> loader) {
        mTopicsList.clear();
        mNoticesList.clear();
        mMaintenanceList.clear();
        mStatusList.clear();
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            if (position == 0) {
                return TopicsFragment.newInstance(mTopicsList);
            } else if (position == 1){
                return NoticesFragment.newInstance(mNoticesList);
            } else if (position == 2){
                return MaintenanceFragment.newInstance(mMaintenanceList);
            } else if (position == 3){
                return StatusFragment.newInstance(mStatusList);
            } else {
                return new Fragment();
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            if (object instanceof TopicsFragment) {
                ((TopicsFragment) object).update(mTopicsList);
            }
            if (object instanceof NoticesFragment) {
                ((NoticesFragment) object).update(mNoticesList);
            }
            if (object instanceof MaintenanceFragment) {
                ((MaintenanceFragment) object).update(mMaintenanceList);
            }
            if (object instanceof StatusFragment) {
                ((StatusFragment) object).update(mStatusList);
            }
            return super.getItemPosition(object);
        }
    }
}
