package com.vorozhbicky.dmitry.snolight;

import android.bluetooth.BluetoothSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Поток приема и отправки данных на метеостанцию
 */

public class ThreadConnected extends Thread {
    private final InputStream connectedInputStream;
    private final OutputStream connectedOutStream;
    private String sbprint;
    private StringBuilder sb;
    private boolean manager = false;
    private boolean work = true;

    ThreadConnected(BluetoothSocket socket) {
        InputStream in = null;
        OutputStream ot = null;
        sb = new StringBuilder();
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
        BufferedReader br;
        String read;
        while (work) {
            try {
                br = new BufferedReader(new InputStreamReader(connectedInputStream));
                if (manager && ((read = br.readLine()) != null)) {
                    sb.append(read);
                    sbprint = sb.toString();
                    sb.delete(0, sb.length());
                    System.out.println(sbprint);
                    manager = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void write(byte[] buffer) {
        try {
            connectedOutStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendBiteToArduino(String s) {
        manager = true;
        byte[] bytesToSend = s.getBytes();
        write(bytesToSend);
    }

    String getSbprint() {
        return sbprint;
    }

    void stopThread() {
        work = false;
    }

}