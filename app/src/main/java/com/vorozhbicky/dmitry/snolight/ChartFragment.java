package com.vorozhbicky.dmitry.snolight;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ChartFragment extends Fragment {

    private static final String STRING_PATTERN =
            "^(A-?\\d?\\d?\\d?\\d?\\d?\\d)\\.?" + "(\\d?\\d?)" + "(B-?\\d?\\d?\\d?\\d?\\d?\\d)\\.?" + "(\\d?\\d?)" +
                    "(C-?\\d?\\d?\\d?\\d?\\d?\\d)\\.?" + "(\\d?\\d?)" + "(D-?\\d?\\d?\\d?\\d?\\d?\\d)\\.?" + "(\\d?\\d?)" +
                    "(E-?\\d?\\d?\\d?\\d?\\d?\\d)\\.?" + "(\\d?\\d?)" + "(F-?\\d?\\d?\\d?\\d?\\d?\\d)\\.?" + "(\\d?\\d?)" +
                    "(G-?\\d?\\d?\\d?\\d?\\d?\\d)\\.?" + "(\\d?\\d?)" + "(H-?\\d?\\d?\\d?\\d?\\d?\\d)\\.?" + "(\\d?\\d?)" +
                    "(I-?\\d?\\d?\\d?\\d?\\d?\\d)\\.?" + "(\\d?\\d?)" + "(J-?\\d?\\d?\\d?\\d?\\d?\\d)\\.?" + "(\\d?\\d?)" +
                    "(K-?\\d?\\d?\\d?\\d?\\d?\\d)\\.?" + "(\\d?\\d?)" + "(L-?\\d?\\d?\\d?\\d?\\d?\\d)\\.?" + "(\\d?\\d?)$";

    String numberInput;

    private ThreadConnectedCharts myThreadConnectedCharts;

    private StringBuilder sb;
    BufferedReader br;

    private boolean sendBool;

    View myView;

    Button button;

    public ChartFragment(String numberInput) {
        this.numberInput = numberInput;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.chart, container, false);
        BluetoothSocket bluetoothSocket = MainActivity.bluetoothSocket;
        sb = new StringBuilder();
        sendBool = true;
        button = myView.findViewById(R.id.buttonChart);
        System.out.println("-created FRAGMENT-");
        myThreadConnectedCharts = new ThreadConnectedCharts(bluetoothSocket);
        myThreadConnectedCharts.start(); // запуск потока приёма и отправки данных
        System.out.println("myThreadConnected created");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBiteToArduino();
            }
        });
        return myView;
    }

    @Override
    public void onDestroy() {
        System.out.println("-DESTROY FRAGMENT-");
        sb.delete(0, sb.length());
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();  // Always call the superclass method first
        System.out.println("_STOP FRAGMENT_");
        sb.delete(0, sb.length());
    }

    public class ThreadConnectedCharts extends Thread {
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutStream;

        ThreadConnectedCharts(BluetoothSocket socket) {
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
            while (!sendBool) {
                Thread.yield();        //Передать управление другим потокам
            }
            while (true) {
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
                                return;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        private void gettingLine(String sbprint) {
            int pa = sbprint.indexOf("A");
            int pb = sbprint.indexOf("B");
            int pc = sbprint.indexOf("C");
            int pd = sbprint.indexOf("D");
            int pe = sbprint.indexOf("E");
            int pf = sbprint.indexOf("F");
            int pg = sbprint.indexOf("G");
            int ph = sbprint.indexOf("H");
            int pi = sbprint.indexOf("I");
            int pj = sbprint.indexOf("J");
            int pk = sbprint.indexOf("K");
            int pl = sbprint.indexOf("L");
            final String[] readings = new String[12];
            readings[0] = sbprint.substring(pa + 1, pb);
            readings[1] = sbprint.substring(pb + 1, pc);
            readings[2] = sbprint.substring(pc + 1, pd);
            readings[3] = sbprint.substring(pd + 1, pe);
            readings[4] = sbprint.substring(pe + 1, pf);
            readings[5] = sbprint.substring(pf + 1, pg);
            readings[6] = sbprint.substring(pg + 1, ph);
            readings[7] = sbprint.substring(ph + 1, pi);
            readings[8] = sbprint.substring(pi + 1, pj);
            readings[9] = sbprint.substring(pj + 1, pk);
            readings[10] = sbprint.substring(pk + 1, pl);
            readings[11] = sbprint.substring(pl + 1, sbprint.length());

//            final Integer doublePress = Integer.valueOf(press);
//            @SuppressLint("DefaultLocale") final String strPressMm = String.format("%.2f", doublePress / 133.322);
//            final Double doubleTempDev = Double.valueOf(tempDev);
//            @SuppressLint("DefaultLocale") final String strTempDevF = String.format("%.2f", doubleTempDev * 1.8 + 32);
//            final Double doubleTempOne = Double.valueOf(tempOne);
//            @SuppressLint("DefaultLocale") final String strTempOneF = String.format("%.2f", doubleTempOne * 1.8 + 32);
//            final Double doubleTempTwo = Double.valueOf(tempTwo);
//            @SuppressLint("DefaultLocale") final String strTempTwoF = String.format("%.2f", doubleTempTwo * 1.8 + 32);

            LineChart chart = myView.findViewById(R.id.chart);

            chart.setDragEnabled(true);
            chart.setScaleEnabled(false);
            chart.getAxisRight().setEnabled(false);

            ArrayList<Entry> yVal = new ArrayList<>();

            for (int i = 0; i < 12; i++) {
                yVal.add(new Entry(i + 1, Float.valueOf(readings[i])));
            }
            LineDataSet setOne = new LineDataSet(yVal, "Data Set: " + numberInput);

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
        if (myThreadConnectedCharts != null) {
            sendBool = true;
            byte[] bytesToSend = numberInput.getBytes();
            myThreadConnectedCharts.write(bytesToSend);
        }
    }

    public static boolean testStringForWrite(String testString) {
        Pattern p = Pattern.compile(STRING_PATTERN);
        Matcher m = p.matcher(testString);
        return m.matches();
    }

}
