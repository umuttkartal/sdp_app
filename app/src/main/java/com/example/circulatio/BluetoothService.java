package com.example.circulatio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothService extends Service {
    private BluetoothAdapter mBluetoothAdapter;
    String addressHC = "98:D3:32:31:30:0F";
    //    String address = "84:CC:A8:11:F5:72";
    String address = "84:CC:A8:12:0D:E6";
    String addOnAddress = "A8:03:2A:EC:50:C2";
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    static BluetoothSocket btSocket = null;
    static BluetoothSocket addOnSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //    static final UUID myServiceUUID = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
//    static final UUID myUUID = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");
    private boolean ConnectSuccess = true;
    private boolean useAddOn = false;

    private BroadcastReceiver buzzerOnSignalReceiver;
    private BroadcastReceiver buzzerOffSignalReceiver;
    private BroadcastReceiver reconnectSignalReceiver;
    private BroadcastReceiver addOnLedOnReceiver;
    private BroadcastReceiver addOnLedOffReceiver;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onnnCreate");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            startMyOwnForeground();
        startMyOwnForeground();
    }

    public BluetoothSocket getBtSocket(){
        return btSocket;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        final int SERVICE_NOTIFICATION_ID = 8598001;
        String NOTIFICATION_CHANNEL_ID = "com.example.circulatio";
        String channelName = "Circulatio Bluetooth Service";
        NotificationChannel chan = null;
        chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Circulatio Bluetooth Service")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(SERVICE_NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        new Thread() {
            @Override
            public void run() {
                Log.i("BLUETOOTH", "Starting Circulatio...");
                startInForeground();
                initCirculatioService();
                initAddOnService();
                checkConnection();
            }
        }.start();
        return START_STICKY;
    }

    private void startInForeground() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification = new Notification.Builder(this).setContentTitle(
                    getText(R.string.notification_circulatio_title)).setContentText(
                    getText(R.string.notification_circulatio_text)).setSmallIcon(
                    R.drawable.ic_launcher_foreground).setContentIntent(pendingIntent).build();

            // Just use a "random" service ID
            final int SERVICE_NOTIFICATION_ID = 8598001;
            startForeground(SERVICE_NOTIFICATION_ID, notification);
        }
    }


    public void initAddOnService(){
        if(SetUpInfo.useAddOn) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            try {
                BluetoothDevice circulatioAddOn = mBluetoothAdapter.getRemoteDevice(addOnAddress);
                addOnSocket = circulatioAddOn.createInsecureRfcommSocketToServiceRecord(myUUID);
                addOnSocket.connect();
                receiveData(addOnSocket);
                Log.i("BLE", "Connected to the add on!!!");
            } catch (IOException e) {
                Log.i("BLE", "Could not connect to add on!!!");
            }
        }else{
            Log.i("BLE", "Not using add on!!!");
        }
    }

    public void initCirculatioService(){
//        BluetoothManager mBluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(
//                Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        try {
//            if ( btSocket==null || !isBtConnected ) {
//            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

            btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);

            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            btSocket.connect();
            ConnectSuccess=true;
//                    }
//            }
        } catch (IOException e) {
            ConnectSuccess = false;
            Log.i("BLE", "Could not connect to device!!!");
        }

        final IntentFilter buzzerOn = new IntentFilter();
        buzzerOn.addAction(Constants.ACTION_CIRCULATIO_BUZZER_ON);
        buzzerOnSignalReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("BLE", "Got buzzer on message");
                sendSignal("1", btSocket); // 1 for led on, 2 for buzzer on
                if(addOnSocket!=null){
                    sendSignal("7", addOnSocket);
                }
            }
        };
        registerReceiver(buzzerOnSignalReceiver, buzzerOn);

        final IntentFilter buzzerOff = new IntentFilter();
        buzzerOff.addAction(Constants.ACTION_CIRCULATIO_BUZZER_OFF);
        buzzerOffSignalReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("BLE", "Got buzzer off message");
                sendSignal("0", btSocket); // 0 for led off, 3 for buzzer off
                if(addOnSocket!=null){
                    sendSignal("8", addOnSocket);
                }
            }
        };
        registerReceiver(buzzerOffSignalReceiver, buzzerOff);

        final IntentFilter reconnectSignal = new IntentFilter();
        reconnectSignal.addAction(Constants.ACTION_CIRCULATIO_RECONNECT);
        reconnectSignalReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("BLE", "Got reconnect message");
                reconnect();
            }
        };
        registerReceiver(reconnectSignalReceiver, reconnectSignal);
    }

    private void reconnect(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        try {
//            if ( btSocket==null || !isBtConnected ) {
//            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

            btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);

            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            btSocket.connect();
            ConnectSuccess=true;
//                    }
//            }
        } catch (IOException e) {
            ConnectSuccess = false;
            Log.i("BLE", "Could not connect to device!!!");
        }
    }

    private void sendSignal ( String number, BluetoothSocket socket ) {
        if ( socket != null ) {
            try {
                socket.getOutputStream().write(number.toString().getBytes());

            } catch (IOException e) {
                Log.i("BL", "Error");
            }
        }
    }

    private void Disconnect () {
        if ( btSocket!=null ) {
            try {
                btSocket.close();
            } catch(IOException e) {
                Log.i("BLE", "Error on disconnection");
            }
        }
        if(addOnSocket!=null){
            try{
                addOnSocket.close();
            }catch (IOException e){
                Log.i("BLE", "Error on addOn disconnection");
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.i("BLUETOOTH", "Service has been stopped");
        Disconnect();
        super.onDestroy();
//        unregisterReceiver(buzzerOnSignalReceiver);
//        unregisterReceiver(buzzerOffSignalReceiver);

        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("BLUETOOTH", "Main Bluetooth service stopped by Android");
        unregisterReceiver(buzzerOnSignalReceiver);
        unregisterReceiver(buzzerOffSignalReceiver);
        unregisterReceiver(reconnectSignalReceiver);
        return super.onUnbind(intent);
    }

    private void checkConnection(){
        new Thread(){
            @Override
            public void run(){
                if ( btSocket!=null ) {
                    if(btSocket.isConnected()) {
                        Log.i("BLE", "BTSOCKET CONNECTED");
                    }
                    else {
                        Log.i("BLE", "BTSOCKET NOT CONNECTED");
                    }
                }

                if(addOnSocket!=null){
                    if(addOnSocket.isConnected()){
                        Log.i("BLE", "ADDONSOCKET CONNECTED");

                    }else{
                        Log.i("BLE", "ADDONSOCKET NOT CONNECTED");
                    }
                }

            }
        };
    }

    private void receiveData(BluetoothSocket socket){
        new Thread(){
            @Override
            public void run() {
                if (socket != null) {
                    try {
                        InputStream socketInputStream = socket.getInputStream();
                        byte[] buffer = new byte[256];
                        int bytes;

                        // Keep looping to listen for received messages
                        while (true) {
                            try {
                                bytes = socketInputStream.read(buffer);            //read bytes from input buffer
                                String readMessage = new String(buffer, 0, bytes);
                                switch (readMessage) {
                                    case "5": {
                                        Intent intent = new Intent();
                                        intent.setAction(Constants.ACTION_USER_SITTING);
                                        // Data you need to pass to activity
                                        getApplicationContext().sendBroadcast(intent);
                                        break;
                                    }
                                    case "6": {
                                        Intent intent = new Intent();
                                        intent.setAction(Constants.ACTION_USER_STANDING);
                                        // Data you need to pass to activity
                                        getApplicationContext().sendBroadcast(intent);
                                        break;
                                    }
                                    case "3": {
                                        Intent intent = new Intent();
                                        intent.setAction(Constants.ACTION_MASSAGE_NOTIF);
                                        // Data you need to pass to activity
                                        getApplicationContext().sendBroadcast(intent);
                                        break;
                                    }
                                }
                                // Send the obtained bytes to the UI Activity via handler
                                Log.i("logging", readMessage + "");
                            } catch (IOException e) {
                                Log.i("EXCEPTION", "BREAK");
                                break;
                            }
                        }
                    } catch (IOException e) {
                        Log.i("READ", "Error on read!!");
                    }
                }
            }
        }.start();
    }
}
