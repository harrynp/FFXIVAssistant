package com.github.harrynp.ffxivassistant.adapters.lodestone;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.harrynp.ffxivassistant.R;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Topic;
import com.github.harrynp.ffxivassistant.databinding.GridItemTopicBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.TopicsAdapterViewHolder>{
    private Context mContext;
    private final TopicsAdapterOnClickHandler mClickHandler;
    private List<Topic> mTopicList;
    private GridItemTopicBinding mBinding;

    public TopicsAdapter(Context context, TopicsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mTopicList = new ArrayList<>();
    }

    public void addTopic(Topic topic){
        mTopicList.add(topic);
        notifyDataSetChanged();
    }

    public void clear(){
        mTopicList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopicsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.grid_item_topic, parent, false);
        return new TopicsAdapterViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull TopicsAdapterViewHolder holder, int position) {
        Topic topic = mTopicList.get(position);
        if (topic != null){
            if (topic.getBanner() != null && !topic.getBanner().isEmpty()){
                Glide.with(mContext)
                        .load(topic.getBanner())
                        .into(holder.bannerImageView);
            } else {
                holder.bannerImageView.setVisibility(View.GONE);
            }
            holder.titleTextView.setText(topic.getTitle());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = dateFormat.parse(topic.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                String pattern = android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEMMMMddyyyyhhmmaa");
                SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
                String postDate = format.format(date);
                holder.postTimeTextView.setText(postDate);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.summaryTextView.setText(Html.fromHtml(topic.getHtml(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.summaryTextView.setText(Html.fromHtml(topic.getHtml()));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mTopicList == null) return 0;
        return mTopicList.size();
    }

    public interface TopicsAdapterOnClickHandler {
        void onClick(Topic topic, int adapterPosition);
    }

    public class TopicsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView bannerImageView;
        TextView titleTextView;
        TextView postTimeTextView;
        TextView summaryTextView;

        private TopicsAdapterViewHolder(View itemView) {
            super(itemView);
            bannerImageView = mBinding.ivTopicBanner;
            titleTextView = mBinding.tvTopicTitle;
            postTimeTextView = mBinding.tvTopicPostTime;
            summaryTextView = mBinding.tvTopicSummary;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mTopicList.get(adapterPosition), adapterPosition);
        }
    }
}
