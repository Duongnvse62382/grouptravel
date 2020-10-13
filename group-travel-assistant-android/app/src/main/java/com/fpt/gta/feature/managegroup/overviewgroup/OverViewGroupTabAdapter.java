package com.fpt.gta.feature.managegroup.overviewgroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;

import com.fpt.gta.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class OverViewGroupTabAdapter implements SmartTabLayout.TabProvider {

    private Context mContext;
    private FragmentPagerItems fragmentPagerItems;

    public OverViewGroupTabAdapter(Context mContext, FragmentPagerItems fragmentPagerItems) {
        this.mContext = mContext;
        this.fragmentPagerItems = fragmentPagerItems;
    }

    @Override
    public View createTabView(ViewGroup viewGroup, int position, PagerAdapter pagerAdapter) {
        View v = LayoutInflater.from(this.mContext).inflate(R.layout.fragment_group_tab, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (v != null) {
            TextView mNameTab = v.findViewById(R.id.name_tab);
            mNameTab.setText(fragmentPagerItems.get(position).getTitle());
        }
        return v;
    }
}