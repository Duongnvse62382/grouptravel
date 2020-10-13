package com.fpt.gta.feature.managegroup.overviewgroup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.fpt.gta.R;
import com.fpt.gta.feature.managegroup.addgroup.AddGroupActivity;
import com.fpt.gta.feature.managegroup.groupschedule.GroupOnGoingFragment;
import com.fpt.gta.feature.managegroup.groupschedule.GroupGoneFragment;
import com.fpt.gta.feature.managegroup.groupschedule.GroupInComingFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter;
import com.squareup.picasso.Picasso;



public class GroupManageFragment extends Fragment implements View.OnClickListener {

    private ViewPager mViewPager;
    private SmartTabLayout mSmartTabLayout;
    private FragmentStatePagerItemAdapter mAdapter;
    private View mView;
    private ImageView imgAddGroup, imgProfileImage;
    private TextView txtNameUser;

    public GroupManageFragment() {

    }


    public static GroupManageFragment newInstance() {
        GroupManageFragment fragment = new GroupManageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void initalView() {
        txtNameUser = mView.findViewById(R.id.txtNameUser);
        imgAddGroup = mView.findViewById(R.id.imgAddGroup);
        imgProfileImage = mView.findViewById(R.id.imgProfileImage);
        mViewPager = mView.findViewById(R.id.viewpager);
        mSmartTabLayout = mView.findViewById(R.id.view_pager_tab);

    }

    public void initalData() {
        imgAddGroup.setOnClickListener(this::onClick);
        txtNameUser.setText("Hi! " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() !=null){
            Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(imgProfileImage);
        }else {
            Picasso.get().load(R.mipmap.member).into(imgProfileImage);
        }

        viewTabAdapter();


    }

    @Override
    public void onResume() {
        super.onResume();
        initalData();
    }


    public void viewTabAdapter() {
        FragmentPagerItems.Creator pageCreator = FragmentPagerItems.with(getContext());
        pageCreator.add(FragmentPagerItem.of("In Coming", GroupInComingFragment.class));
        pageCreator.add(FragmentPagerItem.of("On Going", GroupOnGoingFragment.class));
        pageCreator.add(FragmentPagerItem.of("Passed", GroupGoneFragment.class));
        FragmentPagerItems fragmentPagerItems = pageCreator.create();
        mAdapter = new FragmentStatePagerItemAdapter(getChildFragmentManager()
                , fragmentPagerItems);
        OverViewGroupTabAdapter tabAdapter = new OverViewGroupTabAdapter(getContext(), fragmentPagerItems);
        mSmartTabLayout.setCustomTabView(tabAdapter);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mSmartTabLayout.setViewPager(mViewPager);
        for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
            TextView textView = mSmartTabLayout.getTabAt(i).findViewById(R.id.name_tab);
            mViewPager.setCurrentItem(1, true);
            if (i == 1) {
                textView.setTextColor(Color.parseColor("#56A8A2"));
            } else {
                textView.setTextColor(Color.parseColor("#000000"));
            }
        }

        mSmartTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
                    TextView textView = mSmartTabLayout.getTabAt(i).findViewById(R.id.name_tab);
                    if (i == position) {
                        textView.setTextColor(Color.parseColor("#56A8A2"));
                    } else {
                        textView.setTextColor(Color.parseColor("#000000"));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_group_view, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initalView();
        initalData();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgAddGroup:
                Intent intent = new Intent(getContext(), AddGroupActivity.class);
                startActivity(intent);
                break;
        }
    }
}