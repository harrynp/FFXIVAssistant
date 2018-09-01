package com.github.harrynp.ffxivassistant.adapters.charactersearch;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.harrynp.ffxivassistant.R;
import com.github.harrynp.ffxivassistant.data.pojo.charactersearch.CharacterResult;
import com.github.harrynp.ffxivassistant.databinding.ListItemCharacterBinding;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CharacterSearchResultsAdapter extends RecyclerView.Adapter<CharacterSearchResultsAdapter.CharacterSearchResultsAdapterViewHolder> {
    private Context mContext;
    private CharacterSearchResultsAdapterOnClickHandler mClickHandler;
    private List<CharacterResult> mCharacterResults;
    private ListItemCharacterBinding mBinding;

    public CharacterSearchResultsAdapter(Context context, CharacterSearchResultsAdapterOnClickHandler clickHandler){
        mContext = context;
        mClickHandler = clickHandler;
        mCharacterResults = new ArrayList<>();
    }

    public void addCharacterResult(CharacterResult characterResult){
        mCharacterResults.add(characterResult);
        notifyDataSetChanged();
    }

    public void clear(){
        mCharacterResults.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CharacterSearchResultsAdapter.CharacterSearchResultsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_item_character, parent, false);
        return new CharacterSearchResultsAdapterViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterSearchResultsAdapter.CharacterSearchResultsAdapterViewHolder holder, int position) {
        CharacterResult characterResult = mCharacterResults.get(position);
        if (characterResult != null) {
            String icon = characterResult.getIcon();
            if (icon != null && !icon.isEmpty()) {
                Glide.with(mContext)
                        .load(icon)
                        .into(holder.circleIconImaveView);
            }
            holder.characterNameTextView.setText(characterResult.getName());
            holder.serverTextView.setText(characterResult.getServer());
        }
    }

    @Override
    public int getItemCount() {
        if (mCharacterResults == null) return 0;
        return mCharacterResults.size();
    }

    public interface CharacterSearchResultsAdapterOnClickHandler{
        void onClick(CharacterResult characterResult, int adapterPosition);
    }

    public class CharacterSearchResultsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CircleImageView circleIconImaveView;
        TextView characterNameTextView;
        TextView serverTextView;

        public CharacterSearchResultsAdapterViewHolder(View itemView) {
            super(itemView);
            circleIconImaveView = mBinding.circleIcon;
            characterNameTextView = mBinding.tvCharacterName;
            serverTextView = mBinding.tvServer;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mCharacterResults.get(adapterPosition), adapterPosition);
        }
    }
}
