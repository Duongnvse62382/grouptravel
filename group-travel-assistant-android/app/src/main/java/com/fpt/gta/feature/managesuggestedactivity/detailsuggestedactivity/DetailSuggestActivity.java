package com.fpt.gta.feature.managesuggestedactivity.detailsuggestedactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.fpt.gta.R;

public class DetailSuggestActivity extends AppCompatActivity {
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_detail_suggest );
        initView ();
    }

    public void initView(){
        imgBack = (ImageView) findViewById ( R.id.imgDetailSuggestedBack );
        imgBack.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                finish ();
            }
        } );
    }
}