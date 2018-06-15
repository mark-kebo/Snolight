package com.vorozhbicky.dmitry.snolight;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DataReader extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;

    private TextView textChangeWet;
    private TextView textChangePressure;
    private TextView textChangeTemperaturesTwo;
    private TextView textChangeTemperaturesOne;
    private TextView textChangeTemperaturesIn;
    private TextView textChangeHeight;
    private TextView textUnitsTemperaturesOne;
    private TextView textUnitsTemperaturesIn;
    private TextView textUnitsTemperaturesTwo;
    private TextView textUnitsPressure;
    private String swTemp = "0", swPress = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("CREATE");
        setContentView(R.layout.activity_data_reader);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView textStereoTemperaturesIn = findViewById(R.id.textStereoTemperaturesIn);
        TextView textViewForPredictionWeather = findViewById(R.id.textViewForPredictionWeather);
        textChangeWet = findViewById(R.id.textChangeWet);
        textChangePressure = findViewById(R.id.textChangePressure);
        textChangeTemperaturesTwo = findViewById(R.id.textChangeTemperaturesTwo);
        textChangeTemperaturesOne = findViewById(R.id.textChangeTemperaturesOne);
        textChangeTemperaturesIn = findViewById(R.id.textChangeTemperaturesIn);
        textChangeHeight = findViewById(R.id.textChangeHeight);
        textUnitsTemperaturesIn = findViewById(R.id.textUnitsTemperaturesIn);
        textUnitsTemperaturesOne = findViewById(R.id.textUnitsTemperaturesOne);
        textUnitsTemperaturesTwo = findViewById(R.id.textUnitsTemperaturesTwo);
        textUnitsPressure = findViewById(R.id.textUnitsPressure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        Intent intent;
        switch (id) {
            case R.id.update:
                MainActivity.threadConnectedData.setbNumb('1');
                MainActivity.threadConnectedData.sendBiteToArduino();
                String s = MainActivity.threadConnectedData.getFinalStringet();
                gettingLine(s);
                break;
            case R.id.action_settings:
                intent = new Intent(getBaseContext(), SettingsActivity.class);// запуск потока приёма и отправки данных
                startActivity(intent);
                break;
            case R.id.action_about_us:
                intent = new Intent(getBaseContext(), AboutUsActivity.class);// запуск потока приёма и отправки данных
                startActivity(intent);
                break;
            case R.id.action_graph_temp_one:
                intent = new Intent(this, ChartActivity.class);
                intent.putExtra("number", '5');
                startActivity(intent);
                break;
            case R.id.action_graph_temp_two:
                intent = new Intent(this, ChartActivity.class);
                intent.putExtra("number", '3');
                startActivity(intent);
                break;
            case R.id.action_graph_temp_three:
                intent = new Intent(this, ChartActivity.class);
                intent.putExtra("number", '4');
                startActivity(intent);
                break;
            case R.id.action_graph_press:
                intent = new Intent(this, ChartActivity.class);
                intent.putExtra("number", '2');
                startActivity(intent);
                break;
            case R.id.action_graph_wet:
                intent = new Intent(this, ChartActivity.class);
                intent.putExtra("number", '6');
                startActivity(intent);
                break;
        }
        return true;
    }

    private void gettingLine(String sbprint) {
        int h = sbprint.indexOf("H");
        int t = sbprint.indexOf("T");
        int q = sbprint.indexOf("Q");
        int w = sbprint.indexOf("W");
        int a = sbprint.indexOf("A");
        int p = sbprint.indexOf("P");
        String tempDev = sbprint.substring(w + 1, a);
        String tempOne = sbprint.substring(q + 1, w);
        String tempTwo = sbprint.substring(t + 1, q);
        String press = sbprint.substring(p + 1, sbprint.length());
        String wet = sbprint.substring(h + 1, t);
        String sm = sbprint.substring(a + 1, p);

        final Integer doublePress = Integer.valueOf(press);
        @SuppressLint("DefaultLocale") final String strPressMm = String.format("%.2f", doublePress / 133.322);
        final Double doubleTempDev = Double.valueOf(tempDev);
        @SuppressLint("DefaultLocale") final String strTempDevF = String.format("%.2f", doubleTempDev * 1.8 + 32);
        final Double doubleTempOne = Double.valueOf(tempOne);
        @SuppressLint("DefaultLocale") final String strTempOneF = String.format("%.2f", doubleTempOne * 1.8 + 32);
        final Double doubleTempTwo = Double.valueOf(tempTwo);
        @SuppressLint("DefaultLocale") final String strTempTwoF = String.format("%.2f", doubleTempTwo * 1.8 + 32);
        if (swTemp.equals("1")) {
            textChangeTemperaturesIn.setText(strTempDevF);
            textChangeTemperaturesOne.setText(strTempOneF);
            textChangeTemperaturesTwo.setText(strTempTwoF);
            textUnitsTemperaturesIn.setText(R.string.temp_f);
            textUnitsTemperaturesOne.setText(R.string.temp_f);
            textUnitsTemperaturesTwo.setText(R.string.temp_f);
        } else {
            textChangeTemperaturesIn.setText(tempDev);
            textChangeTemperaturesOne.setText(tempOne);
            textChangeTemperaturesTwo.setText(tempTwo);
            textUnitsTemperaturesIn.setText(R.string.temp_c);
            textUnitsTemperaturesOne.setText(R.string.temp_c);
            textUnitsTemperaturesTwo.setText(R.string.temp_c);
        }
        if (swPress.equals("1")) {
            textChangePressure.setText(strPressMm);
            textUnitsPressure.setText(R.string.StringMmRtSt);
        } else {
            textChangePressure.setText(press);
            textUnitsPressure.setText(R.string.Pascal);
        }
        textChangeWet.setText(wet);
        textChangeHeight.setText(sm);
    }

    @Override
    protected void onResume() {
        swPress = mSharedPreferences.getString("press_list", "null");
        swTemp = mSharedPreferences.getString("far_list", "null");

        String s = null;
        while (s == null) {
            MainActivity.threadConnectedData.setbNumb('1');
            MainActivity.threadConnectedData.sendBiteToArduino();
            s = MainActivity.threadConnectedData.getFinalStringet();
            System.out.println("DATA IN---------->" + s);
        }
        gettingLine(s);
        super.onResume();
    }
}