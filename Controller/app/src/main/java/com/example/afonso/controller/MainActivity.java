package com.example.afonso.controller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends Activity {
    private short analogXAxis = 0;
    private short analogYAxis = 0;
    private float endX = 0;
    private float endY = 0;
    private boolean actButton = false;
    private String hostName = "192.168.43.60";
    private int port = 25293;
    private Socket socket;
    private DataOutputStream data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread t = new Thread(new Runnable(){
            public void run() {
                connect();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.controller);
        ImageButton actionButton = (ImageButton) findViewById(R.id.actionButton);
        actionButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("PC", "Foi a true");
                    actButton = true;
                    sendInfo();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("PC", "Foi a false");
                    actButton = false;
                    sendInfo();
                }
                else if(event.getAction() == MotionEvent.ACTION_MOVE){}
                return true;
            }
        });

        final ImageButton analogStick = (ImageButton) findViewById(R.id.analogStick);
        analogStick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                endX = 2 * analogStick.getPivotX();
                endY = 2 * analogStick.getPivotY();
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    analogXAxis = (short) ((event.getX() * 255) / endX);
                    analogYAxis = (short) ((event.getY() * 255) / endY);

                    if (event.getX() > 255) {
                        analogXAxis = 255;
                    } else if(event.getX() < 0){
                        analogXAxis = 0;
                    }

                    if (event.getY() > 255) {
                        analogYAxis = 255;
                    } else if(event.getY() < 0){
                        analogYAxis = 0;
                    }
                    Log.v("touchd x val of cap img", analogXAxis + "");
                    Log.v("touchd y val of cap img", analogYAxis + "");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    analogXAxis = 0;
                    analogYAxis = 0;
                    Log.v("touchd x val of cap img", 0 + "");
                    Log.v("touchd y val of cap img", 0 + "");
                }
                sendInfo();
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void connect(){
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(hostName, port));
            if(!socket.isConnected()) {
                System.out.println("Nao me conectei...");
                return;
            }
            else {
                System.out.println("I am connected!");
            }
            data = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void sendInfo(){
        try {
            data.writeBoolean(actButton);
            data.writeInt(analogXAxis);
            data.writeInt(analogYAxis);
            data.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
