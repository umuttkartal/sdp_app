package com.example.circulatio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BLE extends AppCompatActivity {

    Button btn1, btn2, btn3, btn4, btnDis;
    //    String address = null;
    String address = "98:D3:32:31:30:0F";

    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    InputStream receivedData = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        Intent intent = getIntent();

//        address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS);

//        btn1 =  findViewById(R.id.led_off);
//        btn2 =  findViewById(R.id.led_on);
//        btn3 =  findViewById(R.id.buzzer_on);
//        btn4 =  findViewById(R.id.buzzer_off);
//        //For additional actions to be performed
//        btnDis = findViewById(R.id.button4);

        new BLE.ConnectBT().execute();

//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View v) {
//                sendSignal("0");
//            }
//        });
//
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View v) {
//                sendSignal("1");
//            }
//        });
//        btn3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View v) {
//                sendSignal("2");
//            }
//        });
//        btn4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View v) {
//                sendSignal("3");
//            }
//        });
//
//        btnDis.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View v) {
//                Disconnect();
//            }
//        });
    }


    private void sendSignal ( String number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number.toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void receiveData(Intent intent, Context ctx){
//        if ( btSocket != null ) {
//            try {
//                receivedData = btSocket.getInputStream();
//                boolean flagBlock = true;
//                int a = 10;
//                byte[] b = new byte[45];
//                int rec = 0;
//                while(flagBlock) {
//                    if (receivedData.available() > 0) {
//                        rec = receivedData.read(b);
//
//                        Log.i("READ", String.valueOf(rec));
//                        if (rec == a) {
//                            System.out.println(rec);
//                            notific();
//                            flagBlock = false;
//                        }
//                        receivedData.close();
//                        flagBlock = false;
//                    }
//                }
//
////                return receivedData.read();
//            } catch (IOException e) {
//                msg("Error");
//            }
//        }
        new Thread(){
            @Override
            public void run() {
                if (btSocket != null) {
                    try {
                        InputStream socketInputStream = btSocket.getInputStream();
                        byte[] buffer = new byte[256];
                        int bytes;

                        // Keep looping to listen for received messages
                        while (true) {
                            try {
//                                bytes = socketInputStream.read(buffer);            //read bytes from input buffer
//                                String readMessage = new String(buffer, 0, bytes);
//                                // Send the obtained bytes to the UI Activity via handler
//                                Log.i("logging", readMessage + "");
                                int a = 10;
                                byte[] b = new byte[45];
                                int rec = 0;
                                if (socketInputStream.available() > 0) {
                                    rec = socketInputStream.read();

                                    Log.i("READ", String.valueOf(rec));
                                    if (rec == a) {
                                        Log.i("READ", "Immobility algorithm kicked in!");
                                    }
//                                        System.out.println(rec);
////                                        notific();
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0);
//
//                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, String.valueOf(1))
//                                                .setContentTitle("My notification")
//                                                .setContentText("Hello World!")
//                                                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                                                // Set the intent that will fire when the user taps the notification
//                                                .setContentIntent(pendingIntent)
//                                                .setAutoCancel(true);
//
//                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
//
//                                        // notificationId is a unique int for each notification that you must define
//                                        notificationManager.notify(1, builder.build());
//                                    }
//                                    socketInputStream.close();
                                }
                            } catch (IOException e) {
                                Log.i("EXCEPTION", "BREAK");
                                break;
                            }
                        }
                    } catch (IOException e) {
                        msg("Error on read!!");
                    }
                }
            }
        }.start();


//        return 0;
    }

    private void Disconnect () {
        if ( btSocket!=null ) {
            try {
                btSocket.close();
            } catch(IOException e) {
                msg("Error");
            }
        }

        finish();
    }

    public void notific(){

        Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf(1))
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(BLE.this, "Connecting...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = myBluetooth.getRemoteDevice(address);
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
//                    receivedData = btSocket.getInputStream();
//                    int a = 10;
//                    byte[] b = new byte[10];
//                    int rec = receivedData.read();
//                    Log.i("READ", String.valueOf(rec));
//                    if(rec == a){
//                        System.out.println(rec);
//                        notific();
//                    }
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Try again.");
                finish();
            } else {
                msg("Connected");
                isBtConnected = true;
            }

            progress.dismiss();
//            receiveData(getIntent(), getApplicationContext());

        }
    }
}