package com.fpt.gta.feature.searchautocomplete;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.PlaceDTO;

import java.util.ArrayList;
import java.util.List;

public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder> {
    private Context mContext;
    private List<PlaceDTO> placeDTOList;
    private Integer searchType;
    private OnSearchClickListener onSearchClickListener;

    public PlacesAutoCompleteAdapter(Context mContext, List<PlaceDTO> placeDTOList, Integer searchType) {
        this.mContext = mContext;
        this.placeDTOList = placeDTOList;
        this.searchType = searchType;
    }

    @NonNull
    @Override
    public PredictionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(R.layout.place_recycler_item_layout, parent, false);
        return new PredictionHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionHolder holder, int position) {

        holder.name.setText(placeDTOList.get(position).getName());
        holder.address.setText(placeDTOList.get(position).getAddress());

        holder.mRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSearchClickListener != null) {
                    onSearchClickListener.onSearchClickListener(placeDTOList.get(position), position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (placeDTOList == null) {
            return 0;
        } else {
            return placeDTOList.size();
        }
    }

    public void notifyDataSetChangePlace(List<PlaceDTO> dtoList) {
        placeDTOList = new ArrayList<>();
        placeDTOList = dtoList;
    }

    public class PredictionHolder extends RecyclerView.ViewHolder {
        private TextView address, name;
        private LinearLayout mRow;

        PredictionHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.place_name);
            address = itemView.findViewById(R.id.place_address);
            mRow = itemView.findViewById(R.id.place_item_view);
        }
    }


    public interface OnSearchClickListener {
        void onSearchClickListener(PlaceDTO placeDTO, int position);
    }

    public void setOnSearchClickListener(PlacesAutoCompleteAdapter.OnSearchClickListener onSearchClickListener) {
        this.onSearchClickListener = onSearchClickListener;
    }
}
