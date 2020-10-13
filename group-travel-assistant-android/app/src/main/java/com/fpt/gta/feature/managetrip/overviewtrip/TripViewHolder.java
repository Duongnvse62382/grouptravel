package com.fpt.gta.feature.managetrip.overviewtrip;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.gta.R;

final class TripViewHolder  extends RecyclerView.ViewHolder {


    public TextView txtTripName;
    public TextView txtTripDateStart;
    public TextView txtTripDateEnd;
    public TextView txtTripDateStartUTC;
    public TextView txtTripDateEndUTC;
    public LinearLayout lnlRecycleViewTrip;
    public LinearLayout lnlEditTrip;
    public LinearLayout lnlTimeZonePoint;
    public ImageView imgImageTrip;
    public TextView txtDayOfTrip;
    public View rootView;
    public TripViewHolder(@NonNull View itemView) {
        super(itemView);
        rootView = itemView;
        txtTripName = (TextView) itemView.findViewById(R.id.txtTripName);
        txtTripDateStart = (TextView) itemView.findViewById(R.id.txtTripDateStart);
        txtTripDateEnd = (TextView) itemView.findViewById(R.id.txtTripDateEnd);
        txtTripDateStartUTC = (TextView) itemView.findViewById(R.id.txtTripDateStartUTC);
        txtTripDateEndUTC = (TextView) itemView.findViewById(R.id.txtTripDateEndUTC);
        lnlEditTrip = (LinearLayout) itemView.findViewById ( R.id.lnl_edit_trip);
        lnlTimeZonePoint =  (LinearLayout) itemView.findViewById ( R.id.lnlTimeZonePoint);
        lnlRecycleViewTrip = (LinearLayout) itemView.findViewById ( R.id.lnlRowTripItem );
        imgImageTrip = (ImageView) itemView.findViewById(R.id.imgImageTrip);
        txtDayOfTrip = (TextView) itemView.findViewById(R.id.txtDayOfTrip);
    }

}
