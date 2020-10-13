package com.fpt.gta.feature.managetransaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.TripReponseDTO;

import java.util.List;

public class SpinnerCityAdapter extends ArrayAdapter<TripReponseDTO> {
    private Context mContext;
    private List<TripReponseDTO> tripReponseDTOList;
    private LayoutInflater mInflater;
    private final int mResource;

    public SpinnerCityAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<TripReponseDTO> objects) {
        super(context, resource, 0, objects);
        this.mInflater = LayoutInflater.from(context);
        this.mResource = resource;
        this.tripReponseDTOList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return tripReponseDTOList.size();
    }

    @Override
    public TripReponseDTO getItem(int position) {
        return tripReponseDTOList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = mInflater.inflate(mResource, parent, false);

        TextView txtSpinnerCity = (TextView) view.findViewById(R.id.txtSpinnerCity);

        TripReponseDTO tripReponseDTO = tripReponseDTOList.get(position);
        txtSpinnerCity.setText(tripReponseDTOList.get(position).getStartPlace().getName());

        return view;
    }

}
