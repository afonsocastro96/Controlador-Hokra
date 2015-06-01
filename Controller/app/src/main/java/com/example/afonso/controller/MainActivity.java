package com.example.afonso.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends Activity {
    private float endX = 0;
    private float endY = 0;

    private short analogXAxis = 0;
    private short analogYAxis = 0;
    private boolean actButton = false;

    private String hostName = "192.168.43.60";
    private int port = 25294;
    private Socket socket;
    private DataOutputStream data;
    boolean connected = false;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller);
        ImageButton actionButton = (ImageButton) findViewById(R.id.actionButton);
        actionButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(connected) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Log.d("PC", "Foi a true");
                        actButton = true;
                        sendInfo();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        Log.d("PC", "Foi a false");
                        actButton = false;
                        sendInfo();
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    }
                }
                return true;
            }
        });

        final ImageButton connect = (ImageButton)findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.popup, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.edittext);

                // set dialog message
                alertDialogBuilder.setTitle("Connect to server")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        hostName = userInput.getText().toString();
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
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

        final ImageButton analogStick = (ImageButton) findViewById(R.id.analogStick);
        analogStick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (connected) {
                    endX = 2 * analogStick.getPivotX();
                    endY = 2 * analogStick.getPivotY();
                    if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                        analogXAxis = (short) ((event.getX() * 255) / endX);
                        analogYAxis = (short) ((event.getY() * 255) / endY);

                        if (event.getX() > 255) {
                            analogXAxis = 255;
                        } else if (event.getX() < 0) {
                            analogXAxis = 0;
                        }

                        if (event.getY() > 255) {
                            analogYAxis = 255;
                        } else if (event.getY() < 0) {
                            analogYAxis = 0;
                        }
                        Log.v("touchd x val of cap img", analogXAxis + "");
                        Log.v("touchd y val of cap img", analogYAxis + "");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        analogXAxis = 128;
                        analogYAxis = 128;
                        Log.v("touchd x val of cap img", analogXAxis + "");
                        Log.v("touchd y val of cap img", analogYAxis + "");
                    }
                    sendInfo();
                }
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(connected) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        connected = true;
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
