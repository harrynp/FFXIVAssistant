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
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Notice;
import com.github.harrynp.ffxivassistant.databinding.GridItemNoticeBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoticesAdapter extends RecyclerView.Adapter<NoticesAdapter.NoticesAdapterViewHolder>{
    private Context mContext;
    private final NoticesAdapterOnClickHandler mClickHandler;
    private List<Notice> mNoticeList;
    private GridItemNoticeBinding mBinding;

    public NoticesAdapter(Context context, NoticesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mNoticeList = new ArrayList<>();
    }

    public void addNotice(Notice notice){
        mNoticeList.add(notice);
        notifyDataSetChanged();
    }

    public void clear(){
        mNoticeList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoticesAdapter.NoticesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.grid_item_notice, parent, false);
        return new NoticesAdapterViewHolder(mBinding.getRoot());    }

    @Override
    public void onBindViewHolder(@NonNull NoticesAdapter.NoticesAdapterViewHolder holder, int position) {
        Notice notice = mNoticeList.get(position);
        if (notice != null){
            holder.titleTextView.setText(notice.getTitle());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = dateFormat.parse(notice.getTime());
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
        if (mNoticeList == null) return 0;
        return mNoticeList.size();
    }

    public interface NoticesAdapterOnClickHandler {
        void onClick(Notice notice, int adapterPosition);
    }

    public class NoticesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView titleTextView;
        TextView postTimeTextView;

        public NoticesAdapterViewHolder(View itemView) {
            super(itemView);
            titleTextView = mBinding.tvNoticeTitle;
            postTimeTextView = mBinding.tvNoticePostTime;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mNoticeList.get(adapterPosition), adapterPosition);
        }
    }
}
