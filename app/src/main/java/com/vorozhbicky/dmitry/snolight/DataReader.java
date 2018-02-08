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

public class DataReader extends AppCompatActivity {

    private ThreadConnected myThreadConnected;
    private BluetoothSocket bluetoothSocket = MainActivity.bluetoothSocket;

    private TextView textStereoTemperaturesIn, textViewForPredictionWeather, textChangeWet,
            textChangePressure, textChangeTemperaturesTwo, textChangeTemperaturesOne,
            textChangeTemperaturesIn, textChangeHeight, textUnitsTemperaturesOne, textUnitsTemperaturesIn,
            textUnitsTemperaturesTwo, textUnitsPressure;
    private int swTemp = 0, swPress = 0;

    private String tempDev, tempOne, tempTwo, press, wet, sm;
    private String sbprint;
    private int h, t, q, w, a, p, b;
    private StringBuilder sb = new StringBuilder();
    BufferedReader br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_reader);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBiteToArduino();
            }
        });

        textStereoTemperaturesIn = findViewById(R.id.textStereoTemperaturesIn);
        textViewForPredictionWeather = findViewById(R.id.textViewForPredictionWeather);
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

//        temperatureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // в зависимости от значения isChecked выводим нужное сообщение
//                if (isChecked) {
//                    swTemp = 1;
//                } else {
//                    swTemp = 0;
//                }
//                sendBiteToArduino();
//            }
//        });
//
//        pressureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                // в зависимости от значения isChecked выводим нужное сообщение
//                if (isChecked) {
//                    swPress = 1;
//                } else {
//                    swPress = 0;
//                }
//                sendBiteToArduino();
//            }
//        });
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
        switch (id) {
            case R.id.action_graph_ch:
                break;
            case R.id.action_settings:
                break;
            case R.id.action_about_us:
                Intent intent = new Intent(getBaseContext(), AboutUsActivity.class);// запуск потока приёма и отправки данных
                startActivity(intent);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        System.out.println("------------DESTROY INFO FRAGMENT------------");
        sb.delete(0, sb.length());
        sbprint = null;
        super.onDestroy();
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
                br = new BufferedReader(new InputStreamReader(connectedInputStream));
                String read;
                try {
                    while ((read = br.readLine()) != null) {
                        System.out.println(read);
                        Thread.sleep(1000);
                        sb.append(read);
                        sbprint = sb.toString();
                        gettingLine();
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        private void gettingLine() {
            h = sbprint.indexOf("H");
            t = sbprint.indexOf("T");
            q = sbprint.indexOf("Q");
            w = sbprint.indexOf("W");
            a = sbprint.indexOf("A");
            p = sbprint.indexOf("P");
            b = sbprint.indexOf("B");
            tempDev = sbprint.substring(t + 1, q);
            tempOne = sbprint.substring(q + 1, w);
            tempTwo = sbprint.substring(w + 1, a);
            press = sbprint.substring(p + 1, b);
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
            byte[] bytesToSend = "A".getBytes();
            myThreadConnected.write(bytesToSend);
        }
    }

}
