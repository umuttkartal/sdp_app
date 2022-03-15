package com.example.circulatio;

import com.example.circulatio.BluetoothService;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.Lifecycle;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

    private Bundle mSavedInstanceState;
    private boolean mIsCirculatioConnected;
    private BluetoothAdapter mBluetoothAdapter;
//    private PowerManager.WakeLock wakeLock;
    private Utils mUtils;
    BluetoothManager bluetoothManager;
    Button btnConnection;
    Button startButton;
    AlertDialog.Builder addBLEOffDialog;
    AlertDialog.Builder addCirculatioNotFoundDialog;

    TextView textViewName;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Do something if connected
                btnConnection.setText(R.string.connected);
                mIsCirculatioConnected = true;
                startButton.setEnabled(true);
                Log.i("BLT", "Bluetooth connected...");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Do something if disconnected
                btnConnection.setText(R.string.not_connected);
                mIsCirculatioConnected = false;
                startButton.setEnabled(false);
                Log.i("BLT", "Bluetooth disconnected...");
            }
        }
    };

    public void blinkBLTButton(View view) {
        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blinking_animation_not_repeated);
        btnConnection.startAnimation(startAnimation);
    }

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
            getApplicationContext().startService(intentStartService);
//            mIsCirculatioConnected = true;
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

        textViewName = findViewById(R.id.deviceName);
        User.loadUserData(getApplicationContext());
        String name = User.getName();
        name = name + "'s Circulatio";
        textViewName.setText(name);

        btnConnection = findViewById(R.id.bltButton);

        startButton = findViewById(R.id.btnStartMassage1);
        startButton.setEnabled(false);

//        startButton.setEnabled(mIsCirculatioConnected);

        // Load connection state
        if (mSavedInstanceState != null) {

//            mIsCirculatioConnected = mSavedInstanceState.getBoolean(Constants.IS_CIRCULATIO_CONNECTED);
//            updateCirculatioConnection(mIsCirculatioConnected);
            System.out.println("CIGUBUGU Connected?" + mIsCirculatioConnected);
        }

        bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(
                Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        startCirculatioService();

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter1.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter1.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mReceiver, filter1);

        addBLEOffDialog = new AlertDialog.Builder(this).setTitle("Device Bluetooth Off")
                .setMessage("Please turn on Bluetooth on your device to use Circulatio")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }});
        addCirculatioNotFoundDialog = new AlertDialog.Builder(this).setTitle("Circulatio Not Found")
                .setMessage("Please ensure Circulatio is turned on")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }});

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsCirculatioConnected && startButton.getText().equals(getString(R.string.start_massage))) {
                    startButton.setText(R.string.stop_massage_button);
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_CIRCULATIO_BUZZER_ON);
                    // Data you need to pass to activity
                    getApplicationContext().sendBroadcast(intent);
                }
                else if (mIsCirculatioConnected && startButton.getText().equals(getString(R.string.stop_massage_button))) {
                    startButton.setText(R.string.start_massage);
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_CIRCULATIO_BUZZER_OFF);
                    // Data you need to pass to activity
                    getApplicationContext().sendBroadcast(intent);
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



        btnConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mIsCirculatioConnected && (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF)){
                    addBLEOffDialog.show();
                }
                else if(!mIsCirculatioConnected && (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON)){
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_CIRCULATIO_RECONNECT);
                    // Data you need to pass to activity
                    getApplicationContext().sendBroadcast(intent);
                    btnConnection.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(!mIsCirculatioConnected){
                                addCirculatioNotFoundDialog.show();
                            }
                            else {
                                startButton.setEnabled(true);
                            }
                        }
                    }, 2000);

                }
            }
        });

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

        if(mIsCirculatioConnected){
            btnConnection.setText(R.string.connected);
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
        unregisterReceiver(mReceiver);

        Intent in = new Intent(getApplicationContext(), BluetoothService.class);
        getApplicationContext().stopService(in);

        super.onDestroy();
    }
}