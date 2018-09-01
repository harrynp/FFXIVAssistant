package com.github.harrynp.ffxivassistant.adapters.devtracker;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
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
import com.github.harrynp.ffxivassistant.data.pojo.devtracker.Post;
import com.github.harrynp.ffxivassistant.databinding.GridItemDevtrackerBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DevTrackerAdapter extends RecyclerView.Adapter<DevTrackerAdapter.DevTrackerAdapterViewHolder> {

    private Context mContext;
    private final DevTrackerAdapterOnClickHandler mClickHandler;
    private List<Post> mPostList;
    private GridItemDevtrackerBinding mBinding;

    public DevTrackerAdapter(Context context, DevTrackerAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mPostList = new ArrayList<>();
    }

    public void addPost(Post post){
        mPostList.add(post);
        notifyDataSetChanged();
    }

    public void clear(){
        mPostList.clear();
        notifyDataSetChanged();
    }

    public List<Post> getPostList(){
        return mPostList;
    }

    @NonNull
    @Override
    public DevTrackerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.grid_item_devtracker, parent, false);
        return new DevTrackerAdapterViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull DevTrackerAdapterViewHolder holder, int position) {
        Post post = mPostList.get(position);
        if (post != null){
            holder.titleTextView.setText(post.getThread().getTitle());
            Date date = new Date(Long.parseLong(post.getThread().getTime()) * 1000L);
            if (date != null) {
                String pattern = android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEMMMMddyyyyhhmmaa");
                SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
                String postDate = format.format(date);
                holder.postTimeTextView.setText(postDate);
            }
            String username = post.getThread().getUser().getUsername();
            String colorString = post.getThread().getUser().getColor();
            int color = Color.parseColor(colorString);
            holder.usernameTextView.setText(username);
            holder.usernameTextView.setTextColor(color);
            String avatar = post.getThread().getUser().getAvatar();
            if (avatar != null && !avatar.isEmpty()){
                Glide.with(mContext)
                        .load(avatar)
                        .into(holder.avatarImageView);
            } else {
                holder.avatarImageView.setVisibility(View.GONE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.messageTextView.setText(Html.fromHtml(post.getThread().getMessage(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.messageTextView.setText(Html.fromHtml(post.getThread().getMessage()));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mPostList == null) return 0;
        return mPostList.size();
    }

    public interface DevTrackerAdapterOnClickHandler{
        void onClick(Post post, int adapterPosition);
    }

    public class DevTrackerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleTextView;
        TextView usernameTextView;
        TextView postTimeTextView;
        ImageView avatarImageView;
        TextView messageTextView;

        public DevTrackerAdapterViewHolder(View itemView) {
            super(itemView);
            titleTextView = mBinding.tvDevTrackerTitle;
            usernameTextView = mBinding.tvDevTrackerPostUsername;
            postTimeTextView = mBinding.tvDevTrackerPostTime;
            avatarImageView = mBinding.ivAvatar;
            messageTextView = mBinding.tvDevTrackerMessage;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mPostList.get(adapterPosition), adapterPosition);
        }
    }
}
