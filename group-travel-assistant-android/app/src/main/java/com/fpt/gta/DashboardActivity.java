package com.fpt.gta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.fpt.gta.feature.profile.ProfileFragment;

import butterknife.ButterKnife;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        clickToProfile();
    }


    public void clickToProfile() {
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_test, new ProfileFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
