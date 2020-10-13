package com.fpt.gta.feature.managesuggestedactivity.overviewsuggestedactivity;

import android.content.Context;
import android.util.Log;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.util.ImageLoaderUtil;

import java.util.List;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

final class PlaceSuggestedSection extends Section {

    private List<PlaceDTO> placeDTOList;
    private Context mContext;
    private final String title;
    private final int imgSection;
    private final PlaceSuggestedSection.ClickListener clickListener;


    public PlaceSuggestedSection(List<PlaceDTO> placeDTOList, Context mContext, String title, int imgSection, ClickListener clickListener) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.place_suggested_dialog)
                .headerResourceId(R.layout.section_place_suggested_header)
                .build());
        this.placeDTOList = placeDTOList;
        this.mContext = mContext;
        this.title = title;
        this.imgSection = imgSection;
        this.clickListener = clickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return placeDTOList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(final View view) {
        return new PlaceSuggestedViewHolder(view);
    }


    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final PlaceSuggestedViewHolder itemHolder = (PlaceSuggestedViewHolder) holder;
        itemHolder.txtNamePlaceLocal.setText(placeDTOList.get(position).getName());
        itemHolder.txtPlaceSuggestedAddress.setText(placeDTOList.get(position).getAddress());
        itemHolder.chbPlace.setChecked(placeDTOList.get(position).isSelected());
        if (placeDTOList.get(position).getPlaceImageList().size() != 0) {
            ImageLoaderUtil.loadImage(mContext, placeDTOList.get(position).getPlaceImageList().get(0).getUri(), itemHolder.imgSuggestedPlace);
        } else {
            Glide.with(mContext).load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(itemHolder.imgSuggestedPlace);
        }

        itemHolder.chbPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeDTOList.get(position).setSelected(!placeDTOList.get(position).isSelected());
                itemHolder.chbPlace.setSelected(placeDTOList.get(position).isSelected());
                clickListener.onItemRootViewClicked(position, placeDTOList.get(position));
            }
        });


        itemHolder.lnlPlaceSuggestedDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemHolder.chbPlace.performClick();
            }

        });


    }



    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new HeaderPlaceSuggestedViewHolder(view);
    }


    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final HeaderPlaceSuggestedViewHolder headerHolder = (HeaderPlaceSuggestedViewHolder) holder;
        headerHolder.tvTitle.setText(title);
        headerHolder.imgTitleTrip.setImageResource(imgSection);
    }


    interface ClickListener {
        void onItemRootViewClicked(final int itemAdapterPosition, PlaceDTO placeDTO);
    }


}
