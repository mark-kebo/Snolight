package com.vorozhbicky.dmitry.snolight;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataReader extends AppCompatActivity {

    private static final String STRING_PATTERN =
            "^(H\\d?\\d)\\." + "(\\d\\d)" + "(T-?\\d?\\d)\\." + "(\\d\\d)" +
                    "(Q-?\\d?\\d)\\." + "(\\d\\d)" + "(W-?\\d?\\d)\\." + "(\\d\\d)" +
                    "(A\\d?\\d?\\d?\\d?\\d?\\d?)" + "(P\\d?\\d?\\d?\\d?\\d?\\d?\\d?)$";
    private ThreadConnected threadConnectedData;

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
    private int swTemp = 0, swPress = 0;

    private String stringOfArduino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_reader);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BluetoothSocket bluetoothSocket = MainActivity.bluetoothSocket;
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

        threadConnectedData = new ThreadConnected(bluetoothSocket);
        threadConnectedData.start(); // запуск потока приёма и отправки данных
        System.out.println("threadConnectedData created");
        stringOfArduino = null;
        while (stringOfArduino == null) {
            threadConnectedData.sendBiteToArduino("1");
            stringOfArduino = threadConnectedData.getSbprint();
        }
        gettingLine(stringOfArduino);
    }


    @Override
    public void onDestroy() {
        threadConnectedData.stopThread();
        super.onDestroy();
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
                sendAndGet();
                break;
            case R.id.action_graph_ch:
                intent = new Intent(getBaseContext(), ChartsActivity.class);// запуск потока приёма и отправки данных
                startActivity(intent);
                break;
            case R.id.action_settings:
                sendAndGet();
                break;
            case R.id.action_about_us:
                intent = new Intent(getBaseContext(), AboutUsActivity.class);// запуск потока приёма и отправки данных
                startActivity(intent);
        }
        return true;
    }


    private void gettingLine(String sbprint) {
        if (testStringForWrite(sbprint)) {
            int h = sbprint.indexOf("H");
            int t = sbprint.indexOf("T");
            int q = sbprint.indexOf("Q");
            int w = sbprint.indexOf("W");
            int a = sbprint.indexOf("A");
            int p = sbprint.indexOf("P");
            String tempTwo = sbprint.substring(t + 1, q);
            String tempOne = sbprint.substring(q + 1, w);
            String tempDev = sbprint.substring(w + 1, a);
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
            if (swTemp == 1) {
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

            if (swPress == 1) {
                textChangePressure.setText(strPressMm);
                textUnitsPressure.setText(R.string.StringMmRtSt);
            } else {
                textChangePressure.setText(press);
                textUnitsPressure.setText(R.string.Pascal);
            }

            textChangeWet.setText(wet);
            textChangeHeight.setText(sm);
        }
    }

    static boolean testStringForWrite(String testString) {
        Pattern p = Pattern.compile(STRING_PATTERN);
        Matcher m = p.matcher(testString);
        return m.matches();
    }

    void sendAndGet() {
        stringOfArduino = null;
        do {
            threadConnectedData.sendBiteToArduino("1");
            stringOfArduino = threadConnectedData.getSbprint();
        } while (!(testStringForWrite(stringOfArduino)) || (stringOfArduino == null));
        gettingLine(stringOfArduino);
    }
}