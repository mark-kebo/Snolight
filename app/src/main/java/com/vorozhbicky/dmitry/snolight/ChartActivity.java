package com.vorozhbicky.dmitry.snolight;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

    private SharedPreferences mSharedPreferences;

    private char number;
    private String nameChart;
    private String swTemp = "0", swPress = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        number = intent.getCharExtra("number", '1');
        switch (number) {
            case '2':
                nameChart = "Изменение давления";
                break;
            case '3':
                nameChart = "Изменение темп-ры №2";
                break;
            case '4':
                nameChart = "Изменение темп-ры №1";
                break;
            case '5':
                nameChart = "Изменение темп-ры в помещении";
                break;
            case '6':
                nameChart = "Изменение влажности";
                break;
            default:
                nameChart = "График изменения";
        }
        super.setTitle(nameChart);
    }

    @Override
    protected void onResume() {
        swPress = mSharedPreferences.getString("press_list", "null");
        swTemp = mSharedPreferences.getString("far_list", "null");
        String s = null;
        while (s == null) {
            MainActivity.threadConnectedData.setbNumb(number);
            MainActivity.threadConnectedData.sendBiteToArduino();
            s = MainActivity.threadConnectedData.getFinalStringet();
        }
        gettingLine(s);
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

    @SuppressLint("DefaultLocale")
    private void gettingLine(String string) {
        int pa, pb, pc, pd, pe, pf, pg, ph;
        String nameVal = "...";

        pa = string.indexOf("A");
        pb = string.indexOf("B");
        pc = string.indexOf("C");
        pd = string.indexOf("D");
        pe = string.indexOf("E");
        pf = string.indexOf("F");
        pg = string.indexOf("G");
        ph = string.indexOf("H");

        ArrayList<String> valsForChart = new ArrayList<>();
        LineChart chart = findViewById(R.id.chart);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.getAxisRight().setEnabled(false);

        ArrayList<Entry> yVal = new ArrayList<>();

        if (number == '2' && swPress.equals("1")) {
            nameVal = "мм.рт.ст.";
            String temp;
            if ((temp = addStringToChart(pa, pb, string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Integer.valueOf(temp) / 133.322));
            }
            if ((temp = addStringToChart(pb, pc, string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Integer.valueOf(temp) / 133.322));
            }
            if ((temp = addStringToChart(pc, pd, string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Integer.valueOf(temp) / 133.322));
            }
            if ((temp = addStringToChart(pd, pe, string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Integer.valueOf(temp) / 133.322));
            }
            if ((temp = addStringToChart(pe, pf, string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Integer.valueOf(temp) / 133.322));
            }
            if ((temp = addStringToChart(pf, pg, string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Integer.valueOf(temp) / 133.322));
            }
            if ((temp = addStringToChart(pg, ph, string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Integer.valueOf(temp) / 133.322));
            }
            if ((temp = addStringToChart(ph, string.length(), string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Integer.valueOf(temp) / 133.322));
            }
        } else if ((number == '3' || number == '4' || number == '5') && swTemp.equals("1")) {
            nameVal = "°F";
            String temp;
            if ((temp = addStringToChart(pa, pb, string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Double.valueOf(temp) * 1.8 + 32));
            }
            if ((temp = addStringToChart(pb, pc, string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Double.valueOf(temp) * 1.8 + 32));
            }
            if ((temp = addStringToChart(pc, pd, string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Double.valueOf(temp) * 1.8 + 32));
            }
            if ((temp = addStringToChart(pd, pe, string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Double.valueOf(temp) * 1.8 + 32));
            }
            if ((temp = addStringToChart(pe, pf, string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Double.valueOf(temp) * 1.8 + 32));
            }
            if ((temp = addStringToChart(pf, pg, string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Double.valueOf(temp) * 1.8 + 32));
            }
            if ((temp = addStringToChart(pg, ph, string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Double.valueOf(temp) * 1.8 + 32));
            }
            if ((temp = addStringToChart(ph, string.length(), string)) != null) {
                valsForChart.add(String.format("%.2f",
                        Double.valueOf(temp) * 1.8 + 32));
            }
        } else {
            if (number == '2' && swPress.equals("0"))
                nameVal = "Pa";
            else if ((number == '3' || number == '4' || number == '5') && swTemp.equals("0"))
                nameVal = "°C";
            else nameVal = "%";
            String temp;
            if ((temp = addStringToChart(pa, pb, string)) != null) {
                valsForChart.add(temp);
            }
            if ((temp = addStringToChart(pb, pc, string)) != null) {
                valsForChart.add(temp);
            }
            if ((temp = addStringToChart(pc, pd, string)) != null) {
                valsForChart.add(temp);
            }
            if ((temp = addStringToChart(pd, pe, string)) != null) {
                valsForChart.add(temp);
            }
            if ((temp = addStringToChart(pe, pf, string)) != null) {
                valsForChart.add(temp);
            }
            if ((temp = addStringToChart(pf, pg, string)) != null) {
                valsForChart.add(temp);
            }
            if ((temp = addStringToChart(pg, ph, string)) != null) {
                valsForChart.add(temp);
            }
            if ((temp = addStringToChart(ph, string.length(), string)) != null) {
                valsForChart.add(temp);
            }
        }

        for (int i = 0; i < valsForChart.size(); i++) {
            yVal.add(new Entry(i + 1, Float.valueOf(valsForChart.get(i))));
        }
        valsForChart.clear();

        LineDataSet setOne = new LineDataSet(yVal, "Единицы измерения: " + nameVal);

        setOne.setFillAlpha(110);
        setOne.setCircleColor(Color.rgb(244, 67, 54));
        setOne.setDrawCircleHole(false);
        setOne.setColor(Color.rgb(70, 183, 100));
        setOne.setLineWidth(2f);
        setOne.setValueTextSize(11f);
        setOne.setValueTextColor(Color.rgb(244, 67, 54));

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
