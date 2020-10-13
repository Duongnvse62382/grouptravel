package com.fpt.gta.feature.managetrip.overviewtrip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.TripReponseDTO;
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.ImageLoaderUtil;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.util.ZonedDateTimeUtil;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;


final class TripSection extends Section {

    private List<TripReponseDTO> tripDTOlist;
    private Context mContext;
    private int isAdmin;
    private List<Date> mListDate;
    private final String title;
    private final int imgSection;
    private final ClickListener clickListener;

    TripSection(@NonNull List<TripReponseDTO> tripDTOlist, Context mContext, @NonNull int isAdmin,
                @NonNull String title, int imgSection, @NonNull ClickListener clickListener) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.row_recycleview_list_trip)
                .headerResourceId(R.layout.section_trip_header)
                .build());
        this.tripDTOlist = tripDTOlist;
        this.mContext = mContext;
        this.isAdmin = isAdmin;
        this.title = title;
        this.imgSection = imgSection;
        this.clickListener = clickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return tripDTOlist.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(final View view) {
        return new TripViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final TripViewHolder itemHolder = (TripViewHolder) holder;

        if(tripDTOlist.get(position).getElectedPlan() != null){
            itemHolder.txtTripName.setText(tripDTOlist.get(position).getStartPlace().getName() + " (Plan Elected)");
        }else {
            itemHolder.txtTripName.setText(tripDTOlist.get(position).getStartPlace().getName());
        }

        itemHolder.txtTripDateStart.setText(ZonedDateTimeUtil.convertDateTimeToString(tripDTOlist.get(position).getStartAt()));
        itemHolder.txtTripDateEnd.setText(ZonedDateTimeUtil.convertDateTimeToString(tripDTOlist.get(position).getEndAt()));

        String timeZoneJourney = SharePreferenceUtils.getStringSharedPreference(mContext, GTABundle.TIMEZONEGROUP);
        String dateGo = ZonedDateTimeUtil.convertDateTimeToString((tripDTOlist.get(position).getStartAt()));
        String dateEnd = ZonedDateTimeUtil.convertDateTimeToString((tripDTOlist.get(position).getEndAt()));

        if (!tripDTOlist.get(position).getStartPlace().getTimeZone().equals(timeZoneJourney)) {
            itemHolder.lnlTimeZonePoint.setVisibility(View.VISIBLE);
            Date timezoneStratUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(tripDTOlist.get(position).getStartUtcAt(), timeZoneJourney);
            itemHolder.txtTripDateStartUTC.setText(timeZoneJourney + " : " + ZonedDateTimeUtil.convertDateTimeToString(timezoneStratUtcJourey));
            Date timezoneEndUtcJourey = ZonedDateTimeUtil.convertDateFromUtcToTimeZone(tripDTOlist.get(position).getEndUtcAt(), timeZoneJourney);
            itemHolder.txtTripDateEndUTC.setText(ZonedDateTimeUtil.convertDateTimeToString(timezoneEndUtcJourey));
        } else {
            itemHolder.lnlTimeZonePoint.setVisibility(View.GONE);
        }

        DateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            mListDate = getDatesBetween(myFormat.parse(dateGo), myFormat.parse(dateEnd));
            if (tripDTOlist.get(position).getStartPlace() != null) {
                if (mListDate.size() == 1) {
                    itemHolder.txtDayOfTrip.setText(mListDate.size() + " day for this City");
                } else {
                    itemHolder.txtDayOfTrip.setText(mListDate.size() + " days for this City");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (tripDTOlist.get(position).getStartPlace().getPlaceImageList().size() != 0) {
            ImageLoaderUtil.loadImage(mContext, tripDTOlist.get(position).getStartPlace().getPlaceImageList().get(0).getUri(), itemHolder.imgImageTrip);
        } else {
            Glide.with(mContext).load("https://www.allianceplast.com/wp-content/uploads/2017/11/no-image.png").into(itemHolder.imgImageTrip);
        }

        Integer groupStatus = SharePreferenceUtils.getIntSharedPreference(mContext, GTABundle.GROUPSTATUS);
        itemHolder.lnlEditTrip.setVisibility(View.GONE);
        if (!groupStatus.equals(GroupStatus.PLANNING)) {
            itemHolder.lnlEditTrip.setVisibility(View.GONE);
        } else {
            if (isAdmin == MemberRole.ADMIN) {
                itemHolder.lnlEditTrip.setVisibility(View.VISIBLE);
            }
        }

        itemHolder.rootView.setOnClickListener(v -> clickListener.onItemRootViewClicked(this, position, tripDTOlist.get(position)));
        itemHolder.lnlEditTrip.setOnClickListener(v -> clickListener.onItemEditTripClicked(this, position, tripDTOlist.get(position)));
    }

    public static List<Date> getDatesBetween(
            Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar) || calendar.equals(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new HeaderTripViewHolder(view);
    }


    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final HeaderTripViewHolder headerHolder = (HeaderTripViewHolder) holder;
        headerHolder.tvTitle.setText(title);
        headerHolder.imgTitleTrip.setImageResource(imgSection);
    }

    interface ClickListener {
        void onItemRootViewClicked(@NonNull final TripSection section, final int itemAdapterPosition, TripReponseDTO tripReponseDTO);

        void onItemEditTripClicked(@NonNull final TripSection section, final int itemAdapterPosition, TripReponseDTO tripReponseDTO);
    }

}
