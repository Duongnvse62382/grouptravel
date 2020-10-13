package com.fpt.gta.feature.manageplan.activitytype;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.SuggestedActivityResponseDTO;
import com.fpt.gta.util.ImageLoaderUtil;


import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

final class SuggestedPlaceFilterSection extends Section {

    private List<SuggestedActivityResponseDTO> suggestedDTOList;
    private Context mContext;
    private final String title;
    private final int imgSection;
    private final ClickListener clickListener;


    public SuggestedPlaceFilterSection(List<SuggestedActivityResponseDTO> suggestedDTOList, Context mContext, String title, int imgSection, ClickListener clickListener) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.name_suggested_dialog)
                .headerResourceId(R.layout.section_suggested_header)
                .build());
        this.suggestedDTOList = suggestedDTOList;
        this.mContext = mContext;
        this.title = title;
        this.imgSection = imgSection;
        this.clickListener = clickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return suggestedDTOList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(final View view) {
        return new SuggestedPlaceViewHolder(view);
    }


    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final SuggestedPlaceViewHolder itemHolder = (SuggestedPlaceViewHolder) holder;
        itemHolder.txtNameSuggested.setText(suggestedDTOList.get(position).getStartPlace().getName());
        itemHolder.txtNameEndPlaceSuggested.setText(suggestedDTOList.get(position).getEndPlace().getName());
        String idGoogleStart = suggestedDTOList.get(position).getStartPlace().getGooglePlaceId();
        String idGoogleEnd = suggestedDTOList.get(position).getEndPlace().getGooglePlaceId();
        if (idGoogleStart.equals(idGoogleEnd)) {
            itemHolder.lnlNextFilter.setVisibility(View.GONE);
            itemHolder.lnlPlaceEndSuggested.setVisibility(View.GONE);
            itemHolder.txtNameEndPlaceSuggested.setText("");
        } else {
            itemHolder.txtNameEndPlaceSuggested.setText(suggestedDTOList.get(position).getEndPlace().getName());
            itemHolder.lnlPlaceEndSuggested.setVisibility(View.VISIBLE);
            itemHolder.lnlNextFilter.setVisibility(View.VISIBLE);
        }

        if(suggestedDTOList.get(position).getStartPlace().getPlaceImageList().size() != 0){
            ImageLoaderUtil.loadImage(mContext, suggestedDTOList.get(position).getStartPlace().getPlaceImageList().get(0).getUri(), itemHolder.imgSuggestedActivityStart);
        }else {
            Glide.with(mContext).load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(itemHolder.imgSuggestedActivityStart);
        }

        if(suggestedDTOList.get(position).getEndPlace().getPlaceImageList().size() != 0){
            ImageLoaderUtil.loadImage(mContext, suggestedDTOList.get(position).getEndPlace().getPlaceImageList().get(0).getUri(), itemHolder.imgSuggestedActivityEnd);
        }else {
            Glide.with(mContext).load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(itemHolder.imgSuggestedActivityEnd);
        }



        itemHolder.chbActivity.setChecked(suggestedDTOList.get(position).isSelected());
        itemHolder.chbActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggestedDTOList.get(position).setSelected(!suggestedDTOList.get(position).isSelected());
                itemHolder.chbActivity.setSelected(suggestedDTOList.get(position).isSelected());
                clickListener.onItemRootViewClicked(position, suggestedDTOList.get(position));
            }
        });


        itemHolder.lnlSuggestedFilterDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemHolder.chbActivity.performClick();
            }
        });


    }


    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new HeaderSuggestedPlaceViewHolder(view);
    }


    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final HeaderSuggestedPlaceViewHolder headerHolder = (HeaderSuggestedPlaceViewHolder) holder;
        headerHolder.tvTitle.setText(title);
        headerHolder.imgTitleTrip.setImageResource(imgSection);
    }

    interface ClickListener {
        void onItemRootViewClicked(final int itemAdapterPosition, SuggestedActivityResponseDTO suggestedActivityResponseDTO);
    }


}
