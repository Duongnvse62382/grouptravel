package com.fpt.gta.feature.managesuggestedactivity.overviewsuggestedactivity;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;

final class PlaceSuggestedViewHolder extends RecyclerView.ViewHolder {

    TextView txtNamePlaceLocal;
    TextView txtPlaceSuggestedAddress;
    LinearLayout lnlPlaceSuggestedDialog;
    ImageView imgSuggestedPlace;
    CheckBox chbPlace;

    public PlaceSuggestedViewHolder(@NonNull View itemView) {
        super(itemView);
        txtNamePlaceLocal = itemView.findViewById(R.id.txtNamePlaceLocal);
        txtPlaceSuggestedAddress = itemView.findViewById(R.id.txtPlaceSuggestedAddress);
        lnlPlaceSuggestedDialog = itemView.findViewById(R.id.lnlPlaceSuggestedDialog);
        imgSuggestedPlace = itemView.findViewById(R.id.imgSuggestedPlace);
        chbPlace = itemView.findViewById(R.id.chbPlace);
    }
}
