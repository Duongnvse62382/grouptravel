package com.fpt.gta.feature.managetrip.map;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.LatLongDTO;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    private GoogleMap gMap;
    private List<LatLongDTO> latLongDTOList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    private List<LatLng> latLngList = new ArrayList<>();
    private ImageView imgMapBack;
    private Polyline polyline = null;
    private int red = 0;
    private int green = 0;
    private int primary = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        imgMapBack = findViewById(R.id.imgMapBack);
        Bundle bundle = getIntent().getExtras();
        latLongDTOList = (List<LatLongDTO>) bundle.getSerializable("listLastLong");
//        for (LatLongDTO latLongDTO : latLongDTOList) {
//            LatLng latLng = new LatLng(latLongDTO.getLattitude(), latLongDTO.getLongtitue());
//            latLngList.add(latLng);
//        }
        initData();
    }

    public void initData() {
        imgMapBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                String trip;

                for (int i = 0; i < latLongDTOList.size(); i++) {
                    LatLng latLng = new LatLng(latLongDTOList.get(i).getLattitude(), latLongDTOList.get(i).getLongtitue());
                    latLngList.add(latLng);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title(latLongDTOList.get(i).getPlaceName());
                    Marker marker = gMap.addMarker(markerOptions);
                    markerList.add(marker);
                }
                if (polyline != null) polyline.remove();
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(latLngList).clickable(true)
                        .color(Color.CYAN);
                polyline = gMap.addPolyline(polylineOptions);
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0), 2f));

            }
        });
    }
}
