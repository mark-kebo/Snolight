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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import static android.R.layout.simple_list_item_1;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
    private ListView readingsListView;
    private TextView noDataMessage;

    private char number;
    private String swTemp = "0", swPress = "0";
    private String nameChartLine;

    private ArrayList<String> readingsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        noDataMessage = findViewById(R.id.noDataMessage);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        number = intent.getCharExtra("number", '1');
        String nameChart;
        switch (number) {
            case '2':
                nameChart = getString(R.string.chart_change_pressure);
                nameChartLine = getString(R.string.chart_pressure_to);
                break;
            case '3':
                nameChart = getString(R.string.chart_change_temp_two);
                nameChartLine = getString(R.string.chart_temp_two_to);
                break;
            case '4':
                nameChart = getString(R.string.chart_change_temp_one);
                nameChartLine = getString(R.string.chart_temp_one_to);
                break;
            case '5':
                nameChart = getString(R.string.chart_change_temp_in);
                nameChartLine = getString(R.string.chart_temp_in_to);
                break;
            case '6':
                nameChart = getString(R.string.chart_change_wet);
                nameChartLine = getString(R.string.chart_wet_to);
                break;
            default:
                nameChart = getString(R.string.app_name);
        }
        super.setTitle(nameChart);
        readingsListView = findViewById(R.id.readings_list_view);
    }

    @Override
    protected void onResume() {
        readingsList.clear();
        swPress = mSharedPreferences.getString("press_list", "null");
        swTemp = mSharedPreferences.getString("far_list", "null");
        String gettingString = null;
        while (gettingString == null) {
            MainActivity.threadConnectedData.setbNumb(number);
            MainActivity.threadConnectedData.sendBiteToArduino();
            gettingString = MainActivity.threadConnectedData.getFinalStringet();
        }
        if (!gettingString.equals("404")) {
            noDataMessage.setVisibility(View.INVISIBLE);
            gettingLine(gettingString);
        }
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
        String nameVal;

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
        chart.getAxisRight().setEnabled(false);
        // no description text
        chart.getDescription().setEnabled(false);
        // enable touch gestures
        chart.setTouchEnabled(true);
        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setMaxHighlightDistance(300);
        chart.getXAxis().setEnabled(false);

        YAxis y = chart.getAxisLeft();
        y.setLabelCount(6, false);
        y.setTextColor(Color.GRAY);
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.GRAY);

        ArrayList<Entry> yVal = new ArrayList<>();

        if (number == '2' && swPress.equals("1")) {
            nameVal = getString(R.string.StringMmRtSt);
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
            nameVal = getString(R.string.temp_f);
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
                nameVal = getString(R.string.Pascal);
            else if ((number == '3' || number == '4' || number == '5') && swTemp.equals("0"))
                nameVal = getString(R.string.temp_c);
            else nameVal = getString(R.string.percentages);
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
            yVal.add(new Entry(i + 1, Float.valueOf(valsForChart.get(valsForChart.size() - (i + 1)))));
            readingsList.add("Показание " + (i + 1) + "ч. назад: " +
                    Float.valueOf(valsForChart.get(valsForChart.size() - (i + 1))) + nameVal);
        }
        valsForChart.clear();

        chart.setVisibility(View.VISIBLE);
        // dont forget to refresh the drawing
        chart.invalidate();
        LineDataSet setOne = new LineDataSet(yVal, nameChartLine + " " + nameVal);

        setOne.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        setOne.setCubicIntensity(0.2f);
        setOne.setLineWidth(1.8f);
        setOne.setFillColor(Color.GRAY);
        setOne.setFillAlpha(100);
        setOne.setDrawVerticalHighlightIndicator(false);
        setOne.setCircleColor(Color.rgb(50, 110, 215));
        setOne.setCircleRadius(2);
        setOne.setDrawCircleHole(false);
        setOne.setColor(Color.rgb(50, 170, 215));
        setOne.setValueTextSize(11f);
        setOne.setValueTextColor(Color.rgb(231, 85, 101));

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setOne);

        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        ArrayAdapter<String> readingsListAdapter = new ArrayAdapter<>(this, simple_list_item_1, readingsList);
        readingsListView.setAdapter(readingsListAdapter);
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