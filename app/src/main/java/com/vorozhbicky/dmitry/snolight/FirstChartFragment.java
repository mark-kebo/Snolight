package com.vorozhbicky.dmitry.snolight;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstChartFragment extends Fragment {

    public FirstChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.one_chart, container, false);
        LineChart chart = (LineChart) myView.findViewById(R.id.chart);
//        chart.setOnChartGestureListener(Chart.this);
//        chart.setOnChartValueSelectedListener(Chart.this);

        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.getAxisRight().setEnabled(false);

        ArrayList<Entry> yVal = new ArrayList<>();

        yVal.add(new Entry(1, 60f));
        yVal.add(new Entry(2, 50f));
        yVal.add(new Entry(3, 80f));
        yVal.add(new Entry(4, 60f));
        yVal.add(new Entry(5, 20f));
        yVal.add(new Entry(6, 30f));
        yVal.add(new Entry(7, 30f));
        yVal.add(new Entry(8, 30f));
        yVal.add(new Entry(9, 10f));
        yVal.add(new Entry(10, 30f));
        yVal.add(new Entry(11, 30f));
        yVal.add(new Entry(12, 90f));
        LineDataSet setOne = new LineDataSet(yVal, "Data Set");

        setOne.setFillAlpha(110);
        setOne.setColor(Color.argb(255, 31, 99, 182));
        setOne.setLineWidth(2f);
        setOne.setValueTextSize(11f);
        setOne.setValueTextColor(Color.RED);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setOne);

        LineData lineData = new LineData(dataSets);

        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        return myView;
    }

}
