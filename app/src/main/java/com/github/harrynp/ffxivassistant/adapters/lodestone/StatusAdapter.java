package com.github.harrynp.ffxivassistant.adapters.lodestone;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.harrynp.ffxivassistant.R;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Status;
import com.github.harrynp.ffxivassistant.databinding.GridItemStatusBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusAdapterViewHolder> {

    private Context mContext;
    private final StatusAdapterOnClickHandler mClickHandler;
    private List<Status> mStatusList;
    private GridItemStatusBinding mBinding;

    public StatusAdapter(Context context, StatusAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mStatusList = new ArrayList<>();
    }

    public void addStatus(Status status){
        mStatusList.add(status);
        notifyDataSetChanged();
    }

    public void clear(){
        mStatusList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StatusAdapter.StatusAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.grid_item_status, parent, false);
        return new StatusAdapterViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.StatusAdapterViewHolder holder, int position) {
        Status status = mStatusList.get(position);
        if (status != null){
            holder.titleTextView.setText(status.getTitle());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = dateFormat.parse(status.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                String pattern = android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEMMMMddyyyyhhmmaa");
                SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
                String postDate = format.format(date);
                holder.postTimeTextView.setText(postDate);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mStatusList == null) return 0;
        return mStatusList.size();
    }

    public interface StatusAdapterOnClickHandler {
        void onClick(Status status, int adapterPosition);
    }

    public class StatusAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView titleTextView;
        TextView postTimeTextView;

        public StatusAdapterViewHolder(View itemView) {
            super(itemView);
            titleTextView = mBinding.tvStatusTitle;
            postTimeTextView = mBinding.tvStatusPostTime;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mStatusList.get(adapterPosition), adapterPosition);
        }
    }
}
