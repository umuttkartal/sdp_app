package com.example.circulatio;

import com.example.circulatio.BluetoothService;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private boolean mIsActivityRunning = false;

    String address = "98:D3:32:31:30:0F";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Bundle mSavedInstanceState;
    private boolean mIsCirculatioConnected;
    private BluetoothAdapter mBluetoothAdapter;
//    private PowerManager.WakeLock wakeLock;
    private Utils mUtils;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsActivityRunning = true;
        mSavedInstanceState = savedInstanceState;
        mUtils = Utils.getInstance();
        initMainActivity();

    }

    private boolean startCirculatioService() {
        boolean alreadyRunning = Utils.isServiceRunning(BluetoothService.class, this);
        // Only start service if it is not already running.
        Log.i(this.getClass().getCanonicalName(), String.format("Circulatio Service starting: already running? = %s", alreadyRunning));
        if (!alreadyRunning) {
            Log.i("Main Activity", "Started Circulatio Bluetooth service");
            Intent intentStartService = new Intent(this, BluetoothService.class);
            startService(intentStartService);
            mIsCirculatioConnected = true;
//            try {
//                myBluetooth = BluetoothAdapter.getDefaultAdapter();
//                BluetoothDevice device = myBluetooth.getRemoteDevice(address);
//                btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
//                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
//                btSocket.connect();
//                mIsCirculatioConnected = true;
//            }catch (IOException e) {
//                mIsCirculatioConnected=false;
//            }
        } else {
            Log.i("Main Activity", "Circulatio Bluetooth service already running. Don't start it again.");
        }
        return alreadyRunning;
    }

    @SuppressLint("MissingPermission")
    public void initMainActivity() {
        Log.i("MainActivity", "Initialising main activity");

        // Setup the part of the layout which is the same for both modes
        setContentView(R.layout.activity_home_screen);

//        aquireWakeLockToKeepAppRunning();


        // Load connection state
        if (mSavedInstanceState != null) {

            mIsCirculatioConnected = mSavedInstanceState.getBoolean(Constants.IS_CIRCULATIO_CONNECTED);
            updateCirculatioConnection(mIsCirculatioConnected);
            System.out.println("CIGUBUGU");
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(
                Context.BLUETOOTH_SERVICE);

        System.out.println("CIGUBUGU" + bluetoothManager);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        try {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            btSocket.connect();
        } catch (IOException e) {
            Log.i("BLE", "Could not connect to device!!!");
        }

        startCirculatioService();
    }

    private void updateCirculatioConnection(boolean isConnected) {
        mIsCirculatioConnected = isConnected;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "App was brought into foreground (Main Activity) resumed");
        mIsActivityRunning = true;

        Boolean isTouched = false;
        SwitchCompat switch1 = findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(mIsCirculatioConnected) {
                    if (isChecked) {
//                        Intent intent = new Intent();
//                        intent.setAction(Constants.ACTION_CIRCULATIO_BUZZER_ON);
//                        // Data you need to pass to activity
//                        getApplicationContext().sendBroadcast(intent);
                        sendSignal("1");

                    } else {
//                        Intent intent = new Intent();
//                        intent.setAction(Constants.ACTION_CIRCULATIO_BUZZER_ON);
//                        // Data you need to pass to activity
//                        getApplicationContext().sendBroadcast(intent);
                        sendSignal("0");
                    }
                }
                else{
                    Log.i("BL ERROR", "Circulatio is not connected!");
                }
            }
        });

        ImageButton btnInfo =  findViewById(R.id.buttonInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent inn1=getIntent();
                inn1=new Intent(MainActivity.this,UserManual.class);
                startActivity(inn1);
            }
        });

    }

    private void sendSignal (String number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number.toString().getBytes());
            } catch (IOException e) {
                Log.i("Error", "Error");
            }
        }
    }

    @Override
    protected void onPause() {
        mIsActivityRunning = false;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i("DF", "App is being destroyed");

        super.onDestroy();
    }
}