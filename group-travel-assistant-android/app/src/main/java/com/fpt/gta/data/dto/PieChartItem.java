package com.fpt.gta.data.dto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;

import com.fpt.gta.R;
import com.fpt.gta.util.ChangeValue;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.math.BigDecimal;

public class PieChartItem extends ChartItem {
    private final SpannableString mCenterText;

    public PieChartItem(ChartData<?> cd, BigDecimal totalBudget, Context c) {
        super(cd);

        mCenterText = generateCenterText(totalBudget);
    }

    @Override
    public int getItemType() {
        return TYPE_PIECHART;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, Context c) {
        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(c).inflate(
                    R.layout.list_item_piechart, null);
            holder.chart = convertView.findViewById(R.id.chart);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // apply styling
        holder.chart.setHoleRadius(52f);
        holder.chart.setTransparentCircleRadius(57f);
        holder.chart.setCenterText(mCenterText);
        holder.chart.setCenterTextSize(9f);
        holder.chart.setUsePercentValues(true);
        holder.chart.setExtraOffsets(5, 10, 50, 10);
        holder.chart.getDescription().setEnabled(false);
        mChartData.setValueTextSize(9f);
        mChartData.setValueTextColor(Color.BLACK);
        mChartData.setValueFormatter(new PercentFormatter());
        // set data


        holder.chart.setData((PieData) mChartData);
        holder.chart.invalidate();

        Legend l = holder.chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        holder.chart.animateY(900);
        return convertView;
    }

    private SpannableString generateCenterText(BigDecimal totalBudget) {

        SpannableString s = new SpannableString("Total Budget\n" + "\n" + ChangeValue.formatBigCurrency(totalBudget));
//
//        s.setSpan(new RelativeSizeSpan(1.6f), 0, 15, 0);
//        s.setSpan(new ForegroundColorSpan(ColorTemplate.VORDIPLOM_COLORS[0]), 0, 15, 0);
//        s.setSpan(new RelativeSizeSpan(.9f), 16, 19, 0);
//        s.setSpan(new ForegroundColorSpan(Color.GRAY), 16, 19, 0);
//        s.setSpan(new RelativeSizeSpan(1.4f), 20, s.length(), 0);
//        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 20, s.length(), 0);
        return s;
    }

    private static class ViewHolder {
        PieChart chart;
    }
}
