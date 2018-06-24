package com.vorozhbicky.dmitry.snolight;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Поток приема и отправки данных на метеостанцию
 */

public class ThreadConnected extends Thread {
    private final InputStream connectedInputStream;
    private final OutputStream connectedOutStream;
    private String sbprint;
    private StringBuilder sb;
    private boolean work;

    public void setbNumb(char bNumb) {
        this.bNumb = bNumb;
    }

    private char bNumb = '1';

    ThreadConnected(BluetoothSocket socket) {
        InputStream in = null;
        OutputStream ot = null;
        sb = new StringBuilder();
        work = true;
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
        while (work) {
            try {
                byte[] buffer = new byte[1];
                int bytes = connectedInputStream.read(buffer);
                String strIncom = new String(buffer, 0, bytes);
                sb.append(strIncom); // собираем символы в строку
                int endOfLineIndex = sb.indexOf("\r\n"); // определяем конец строки
                if (endOfLineIndex > 0) {
                    sbprint = sb.substring(0, endOfLineIndex);
                    sb.delete(0, sb.length());
                    System.out.println("THREAD---->" + sbprint);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    public void sendBiteToArduino() {
        byte bytesToSend = (byte) bNumb;
        try {
            connectedOutStream.write(bytesToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFinalStringet() {
        String stringOfArduino;
        do {
            stringOfArduino = sbprint;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (stringOfArduino == null);
        sbprint = null;
        return stringOfArduino;
    }
}