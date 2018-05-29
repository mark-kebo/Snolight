package com.vorozhbicky.dmitry.snolight;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {

    private char number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        number = intent.getCharExtra("number", '1');
        System.out.println("------------------------------->" + number);
    }

    @Override
    protected void onResume() {
        String s = null;
        while (s == null) {
            MainActivity.threadConnectedData.setbNumb(number);
            MainActivity.threadConnectedData.sendBiteToArduino();
            s = MainActivity.threadConnectedData.getFinalStringet();
            System.out.println("CHART---------->" + s);
        }
        gettingLine(s);
        System.out.println("onResume()");
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, DataReader.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void gettingLine(String string) {
        int pa, pb, pc, pd, pe, pf, pg, ph;

        pa = string.indexOf("A");
        pb = string.indexOf("B");
        pc = string.indexOf("C");
        pd = string.indexOf("D");
        pe = string.indexOf("E");
        pf = string.indexOf("F");
        pg = string.indexOf("G");
        ph = string.indexOf("H");

//            final Integer doublePress = Integer.valueOf(press);
//            @SuppressLint("DefaultLocale") final String strPressMm = String.format("%.2f", doublePress / 133.322);
//            final Double doubleTempDev = Double.valueOf(tempDev);
//            @SuppressLint("DefaultLocale") final String strTempDevF = String.format("%.2f", doubleTempDev * 1.8 + 32);
//            final Double doubleTempOne = Double.valueOf(tempOne);
//            @SuppressLint("DefaultLocale") final String strTempOneF = String.format("%.2f", doubleTempOne * 1.8 + 32);
//            final Double doubleTempTwo = Double.valueOf(tempTwo);
//            @SuppressLint("DefaultLocale") final String strTempTwoF = String.format("%.2f", doubleTempTwo * 1.8 + 32);

        ArrayList<String> valsForChart = new ArrayList<>();
        LineChart chart = findViewById(R.id.chart);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.getAxisRight().setEnabled(false);

        ArrayList<Entry> yVal = new ArrayList<>();

        valsForChart.add(addStringToChart(pa, pb, string));
        valsForChart.add(addStringToChart(pb, pc, string));
        valsForChart.add(addStringToChart(pc, pd, string));
        valsForChart.add(addStringToChart(pd, pe, string));
        valsForChart.add(addStringToChart(pe, pf, string));
        valsForChart.add(addStringToChart(pf, pg, string));
        valsForChart.add(addStringToChart(pg, ph, string));
        valsForChart.add(addStringToChart(ph, string.length(), string));

        for (int i = 0; i < 8; i++) {
            if (valsForChart.get(i) != null) {
                yVal.add(new Entry(i + 1, Float.valueOf(valsForChart.get(i))));
            }
        }
        valsForChart.clear();

        LineDataSet setOne = new LineDataSet(yVal, "Data Set: " + string);

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
    }

    private String addStringToChart(int startSt, int endSt, String string) {
        System.out.println(string);
        String valForChart;
        if (startSt > -1 && endSt > -1) {
            valForChart = string.substring(startSt + 1, endSt);
            return valForChart;
        } else if (startSt > -1 && endSt < 0) {
            valForChart = string.substring(startSt + 1, string.length());
            return valForChart;
        } else {
            return null;
        }
    }
}
