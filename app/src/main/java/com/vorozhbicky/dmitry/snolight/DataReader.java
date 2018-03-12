package com.vorozhbicky.dmitry.snolight;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataReader extends AppCompatActivity {

    private static final String STRING_PATTERN =
            "^(H\\d?\\d)\\." + "(\\d\\d)" + "(T-?\\d?\\d)\\." + "(\\d\\d)" +
                    "(Q-?\\d?\\d)\\." + "(\\d\\d)" + "(W-?\\d?\\d)\\." + "(\\d\\d)" +
                    "(A\\d?\\d?\\d?\\d?\\d?\\d?)" + "(P\\d?\\d?\\d?\\d?\\d?\\d?\\d?)$";
    private ThreadConnected myThreadConnected;
    private BluetoothSocket bluetoothSocket;

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

    private String tempDev, tempOne, tempTwo, press, wet, sm;
    private StringBuilder sb;
    BufferedReader br;

    private boolean sendBool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_reader);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bluetoothSocket = MainActivity.bluetoothSocket;
        sb = new StringBuilder();
        sendBool = true;
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

        myThreadConnected = new ThreadConnected(bluetoothSocket);
        myThreadConnected.start(); // запуск потока приёма и отправки данных
        System.out.println("myThreadConnected created");
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
                sendBiteToArduino();
                break;
            case R.id.action_graph_ch:
                intent = new Intent(getBaseContext(), ChartsActivity.class);// запуск потока приёма и отправки данных
                startActivity(intent);
                break;
            case R.id.action_settings:
                sendBiteToArduino();
                break;
            case R.id.action_about_us:
                intent = new Intent(getBaseContext(), AboutUsActivity.class);// запуск потока приёма и отправки данных
                startActivity(intent);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        System.out.println("------------DESTROY INFO FRAGMENT------------");
        sb.delete(0, sb.length());
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();  // Always call the superclass method first
        System.out.println("_________________________________PAUSE_______________________________");
        sb.delete(0, sb.length());
    }

    @Override
    public void onStart() {
        System.out.println("------------START INFO FRAGMENT------------");
        sendBiteToArduino();
        super.onStart();
    }

    public class ThreadConnected extends Thread {    // Поток - приём и отправка данных
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutStream;

        ThreadConnected(BluetoothSocket socket) {
            InputStream in = null;
            OutputStream ot = null;
            try {
                in = socket.getInputStream();
                ot = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectedInputStream = in;
            connectedOutStream = ot;
        }

        @Override
        public void run() { // Приём данных
            //Пытаемся получить данные
            while (true) {
                while (!sendBool) {
                    Thread.yield();        //Передать управление другим потокам
                }
                if (sendBool) {
                    String read, sbprint;
                    boolean temp;
                    br = new BufferedReader(new InputStreamReader(connectedInputStream));
                    try {
                        if ((read = br.readLine()) != null) {
                            sb.append(read);
                            sbprint = sb.toString();
                            sb.delete(0, sb.length());
                            temp = testStringForWrite(sbprint);
                            System.out.println(sbprint);
                            if (temp) {
                                gettingLine(sbprint);
                                sendBool = false;
                            }
                        } else {
                            br.close();
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        private void gettingLine(String sbprint) {
            int h = sbprint.indexOf("H");
            int t = sbprint.indexOf("T");
            int q = sbprint.indexOf("Q");
            int w = sbprint.indexOf("W");
            int a = sbprint.indexOf("A");
            int p = sbprint.indexOf("P");
            tempTwo = sbprint.substring(t + 1, q);
            tempOne = sbprint.substring(q + 1, w);
            tempDev = sbprint.substring(w + 1, a);
            press = sbprint.substring(p + 1, sbprint.length());
            wet = sbprint.substring(h + 1, t);
            sm = sbprint.substring(a + 1, p);
            final Integer doublePress = Integer.valueOf(press);
            @SuppressLint("DefaultLocale") final String strPressMm = String.format("%.2f", doublePress / 133.322);
            final Double doubleTempDev = Double.valueOf(tempDev);
            @SuppressLint("DefaultLocale") final String strTempDevF = String.format("%.2f", doubleTempDev * 1.8 + 32);
            final Double doubleTempOne = Double.valueOf(tempOne);
            @SuppressLint("DefaultLocale") final String strTempOneF = String.format("%.2f", doubleTempOne * 1.8 + 32);
            final Double doubleTempTwo = Double.valueOf(tempTwo);
            @SuppressLint("DefaultLocale") final String strTempTwoF = String.format("%.2f", doubleTempTwo * 1.8 + 32);
            runOnUiThread(new Runnable() { // Вывод данных
                @Override
                public void run() {
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
            });
        }


        private void write(byte[] buffer) {
            try {
                System.out.println("connectedOutStream.write(buffer);");
                connectedOutStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendBiteToArduino() {
        if (myThreadConnected != null) {
            sendBool = true;
            byte[] bytesToSend = "1".getBytes();
            myThreadConnected.write(bytesToSend);
        }
    }

    public static boolean testStringForWrite(String testString) {
        Pattern p = Pattern.compile(STRING_PATTERN);
        Matcher m = p.matcher(testString);
        return m.matches();
    }
}
