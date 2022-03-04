package com.example.circulatio;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private boolean mIsActivityRunning = false;

    private Bundle mSavedInstanceState;
    private boolean mIsCirculatioConnected;
    private BluetoothAdapter mBluetoothAdapter;
//    private PowerManager.WakeLock wakeLock;
    private Utils mUtils;

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
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(
                Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        startCirculatioService();
    }

    private void updateCirculatioConnection(boolean isConnected) {
        mIsCirculatioConnected = isConnected;
    }

//    @SuppressLint("InvalidWakeLockTag")
//    private void aquireWakeLockToKeepAppRunning() {
//        // Request wake lock to keep CPU running for all services of the app
//        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
//        wakeLock.acquire(10*60*1000L /*10 minutes*/);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "App was brought into foreground (Main Activity) resumed");
        mIsActivityRunning = true;
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