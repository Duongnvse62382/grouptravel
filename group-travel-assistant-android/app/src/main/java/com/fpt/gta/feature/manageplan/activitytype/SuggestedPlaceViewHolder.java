package com.fpt.gta.feature.manageplan.activitytype;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;

final class SuggestedPlaceViewHolder extends RecyclerView.ViewHolder {

    TextView txtNameSuggested;
    TextView txtNameEndPlaceSuggested;
    LinearLayout lnlSuggestedFilterDialog;
    CheckBox chbActivity;
    LinearLayout lnlNextFilter, lnlPlaceEndSuggested;
    ImageView imgSuggestedActivityStart, imgSuggestedActivityEnd;

    public SuggestedPlaceViewHolder(@NonNull View itemView) {
        super(itemView);
        txtNameSuggested = itemView.findViewById(R.id.txtNamePlaceSuggested);
        txtNameEndPlaceSuggested = itemView.findViewById(R.id.txtNameEndPlaceSuggested);
        lnlSuggestedFilterDialog = itemView.findViewById(R.id.lnlSuggestedFilterDialog);
        lnlPlaceEndSuggested = itemView.findViewById(R.id.lnlPlaceEndSuggested);
        chbActivity = itemView.findViewById(R.id.chbActivity);
        lnlNextFilter = itemView.findViewById(R.id.lnlNextFilter);
        imgSuggestedActivityStart = itemView.findViewById(R.id.imgSuggestedActivityStart);
        imgSuggestedActivityEnd = itemView.findViewById(R.id.imgSuggestedActivityEnd);
        txtNameSuggested.setSelected(true);
        txtNameEndPlaceSuggested.setSelected(true);
    }
}
