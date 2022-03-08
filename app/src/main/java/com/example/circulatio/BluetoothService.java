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
import java.util.UUID;

public class BluetoothService extends Service {
    private BluetoothAdapter mBluetoothAdapter;
    String address = "98:D3:32:31:30:0F";

    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    static BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean ConnectSuccess = true;

    private BroadcastReceiver buzzerOnSignalReceiver;
    private BroadcastReceiver buzzerOffSignalReceiver;
    private BroadcastReceiver reconnectSignalReceiver;


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
        String NOTIFICATION_CHANNEL_ID = "com.specknet.airrespeck";
        String channelName = "Airrespeck Bluetooth Service";
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
                sendSignal("1");
            }
        };
        registerReceiver(buzzerOnSignalReceiver, buzzerOn);

        final IntentFilter buzzerOff = new IntentFilter();
        buzzerOff.addAction(Constants.ACTION_CIRCULATIO_BUZZER_OFF);
        buzzerOffSignalReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("BLE", "Got buzzer off message");
                sendSignal("0");
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

    private void sendSignal ( String number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number.toString().getBytes());

            } catch (IOException e) {
                Log.i("BL", "Error");
            }
        }
    }


    @Override
    public void onDestroy() {
        Log.i("BLUETOOTH", "Service has been stopped");
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
}
