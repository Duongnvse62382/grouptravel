package com.fpt.gta.feature.managegroup.overviewgroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fpt.gta.R;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.fpt.gta.feature.setting.SettingFragment;
import com.fpt.gta.feature.profile.ProfileFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;



public class OverViewCustomTabAdapter implements SmartTabLayout.TabProvider {
    private Context mContext;

    public OverViewCustomTabAdapter(Context context) {
        this.mContext = context;
    }

    public enum MainActivityPages {
        TAB_1(0, "Nhóm", R.mipmap.team, GroupManageFragment.newInstance()),
        TAB_2(1, "Thông Tin Cá Nhân", R.mipmap.information, ProfileFragment.newInstance());
//        TAB_3(2, "Cài Đặt", R.mipmap.setting, SettingFragment.newInstance());

        public int index;
        public String title;
        public int resourceId;
        public Fragment fragment;

        MainActivityPages(int index, String title, int resourceId, Fragment fragment) {
            this.index = index;
            this.title = title;
            this.resourceId = resourceId;
            this.fragment = fragment;
        }

    }


    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        View v = LayoutInflater.from(this.mContext).inflate(R.layout.activity_main_tab_icon, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (v != null) {
            ImageView icon = (ImageView) v.findViewById(R.id.activity_main_tab_icon);
            icon.setImageResource( MainActivityPages.values()[position].resourceId);
            TextView mNameTab = v.findViewById(R.id.name_tab);
            mNameTab.setText( MainActivityPages.values()[position].title);
        }
        return v;
    }
}
