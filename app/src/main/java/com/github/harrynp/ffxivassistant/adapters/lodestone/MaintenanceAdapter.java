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
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Maintenance;
import com.github.harrynp.ffxivassistant.databinding.GridItemMaintenanceBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MaintenanceAdapter extends RecyclerView.Adapter<MaintenanceAdapter.MaintenanceAdapterViewHolder>{

    private Context mContext;
    private final MaintenanceAdapterOnClickHandler mClickHandler;
    private List<Maintenance> mMaintenanceList;
    private GridItemMaintenanceBinding mBinding;

    public MaintenanceAdapter(Context context, MaintenanceAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mMaintenanceList = new ArrayList<>();
    }

    public void addMaintenance(Maintenance maintenance){
        mMaintenanceList.add(maintenance);
        notifyDataSetChanged();
    }

    public void clear(){
        mMaintenanceList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MaintenanceAdapter.MaintenanceAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.grid_item_maintenance, parent, false);
        return new MaintenanceAdapterViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MaintenanceAdapter.MaintenanceAdapterViewHolder holder, int position) {
        Maintenance maintenance = mMaintenanceList.get(position);
        if (maintenance != null){
            if (maintenance.getTag() != null && !maintenance.getTag().isEmpty()){
                holder.tagTextView.setText(maintenance.getTag());
            } else if (maintenance.getTag().isEmpty()) {
                holder.tagTextView.setVisibility(View.GONE);
            }
            holder.titleTextView.setText(maintenance.getTitle());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = dateFormat.parse(maintenance.getTime());
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
        if (mMaintenanceList == null) return 0;
        return mMaintenanceList.size();
    }

    public interface MaintenanceAdapterOnClickHandler {
        void onClick(Maintenance maintenance, int adapterPosition);
    }

    public class MaintenanceAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tagTextView;
        TextView titleTextView;
        TextView postTimeTextView;

        public MaintenanceAdapterViewHolder(View itemView) {
            super(itemView);
            tagTextView = mBinding.tvMaintenanceTag;
            titleTextView = mBinding.tvMaintenanceTitle;
            postTimeTextView = mBinding.tvMaintenancePostTime;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mMaintenanceList.get(adapterPosition), adapterPosition);
        }
    }
}
