package com.fpt.gta.feature.searchautocomplete;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fpt.gta.R;
import com.fpt.gta.data.dto.PlaceDTO;
import com.fpt.gta.presenter.GetorCrawlPresenter;
import com.fpt.gta.presenter.PrintSearchSuggestedPlacePresenter;
import com.fpt.gta.presenter.SearchPlaceCitiesPresenter;
import com.fpt.gta.presenter.SearchPlacePresenter;
import com.fpt.gta.util.DialogShowErrorMessage;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.fpt.gta.view.GetorCrawlView;
import com.fpt.gta.view.PrintSearchSuggestedPlaceView;
import com.fpt.gta.view.SearchPlaceCitiesView;
import com.fpt.gta.view.SearchPlaceView;

import java.util.ArrayList;
import java.util.List;

public class PlaceAutoCompleteActivity extends AppCompatActivity implements SearchPlaceCitiesView, SearchPlaceView, GetorCrawlView {

    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private RecyclerView recyclerView;
    private EditText mEdtSearch;
    private ImageView imgBack;
    private Integer idTrip;
    private List<PlaceDTO> placesDTOList;
    private SearchPlaceCitiesPresenter mSearchPlaceCitiesPresenter;
    private SearchPlacePresenter mSearchPlacePresenter;
    private GetorCrawlPresenter mGetorCrawlPresenter;
    private Integer searchType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_auto_complete);
        searchType = (Integer) getIntent().getExtras().getSerializable(GTABundle.SEARCHTYPE);
        try {
            idTrip = (Integer) getIntent().getExtras().getSerializable(GTABundle.IDTRIP);
        }catch (Exception e){
            e.printStackTrace();
        }


        mSearchPlaceCitiesPresenter = new SearchPlaceCitiesPresenter(this, this);
        mSearchPlacePresenter = new SearchPlacePresenter(this, this);
        mGetorCrawlPresenter = new GetorCrawlPresenter(this, this);
        imgBack = findViewById(R.id.imgSearchAutoCompleteBack);
        recyclerView = (RecyclerView) findViewById(R.id.places_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        (mEdtSearch = findViewById(R.id.place_search)).addTextChangedListener(filterTextWatcher);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    public void updateUI() {
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, placesDTOList, searchType);
        recyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.setOnSearchClickListener(new PlacesAutoCompleteAdapter.OnSearchClickListener() {
            @Override
            public void onSearchClickListener(PlaceDTO placeDTO, int position) {
                mEdtSearch.setText(placeDTO.getName() + "");
                if(idTrip != null){
                    mGetorCrawlPresenter.getorCrawl(placeDTO.getGooglePlaceId(), idTrip);
                }else {
                    mGetorCrawlPresenter.getorCrawl(placeDTO.getGooglePlaceId(), -1);
                }
            }
        });
        mAutoCompleteAdapter.notifyDataSetChangePlace(placesDTOList);
    }




    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (s.length() > 1) {
                if (searchType == 1) {
                    mSearchPlaceCitiesPresenter.searchPlaceCities(s.toString());
                } else {
                    mSearchPlacePresenter.searchPlace(s.toString());
                }
                if (recyclerView.getVisibility() == View.GONE) {
                    recyclerView.setVisibility(View.VISIBLE);
                }

            } else {
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    @Override
    public void searchPlaceCitiesSS(List<PlaceDTO> placeDTOList) {
        if (placeDTOList != null) {
            placesDTOList = new ArrayList<>();
            placesDTOList = placeDTOList;
            updateUI();
        }
    }

    @Override
    public void searchPlaceCitiesFail(String message) {

    }

    @Override
    public void searchPlaceSS(List<PlaceDTO> placeDTOList) {
        if (placeDTOList != null) {
            placesDTOList = new ArrayList<>();
            placesDTOList = placeDTOList;
            updateUI();
        }
    }

    @Override
    public void searchPlaceFail(String message) {

    }


    @Override
    public void crawlSuccess(PlaceDTO placeDTO) {
        Intent intent = new Intent();
        intent.putExtra(GTABundle.RESUTLPLACEEDITTRIP, placeDTO);
        setResult(RESULT_OK, intent);
        if(placeDTO.getIsTooFar().equals(true)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(Html.fromHtml("<font color='#FDD017'>"+ placeDTO.getName() +" too far City, do you  want to go?</font>"));
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }else {
            finish();
        }
    }

    @Override
    public void crawlFail(String messageFail) {

    }

}