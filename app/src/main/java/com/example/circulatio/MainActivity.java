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
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
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
    public static boolean mIsCirculatioConnected;
    public static boolean mIsAddOnConnected;
    private BluetoothAdapter mBluetoothAdapter;
    //    private PowerManager.WakeLock wakeLock;
    private Utils mUtils;
    BluetoothManager bluetoothManager;
    Button btnConnection;
    Button startButton;
    Button addOnButton;
    AlertDialog.Builder addBLEOffDialog;
    AlertDialog.Builder addCirculatioNotFoundDialog;
    AlertDialog.Builder addAddOnNotFoundDialog;
    AlertDialog.Builder startMassageNotifDialog;
    private BroadcastReceiver sittingReceiver;
    private BroadcastReceiver standingReceiver;
    private BroadcastReceiver notifReceiver;

    BluetoothService bleService;

    TextView textViewName;
    TextView addOnView;
    ImageView activityType;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

//            BluetoothService.checkConnection();

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                public void run() {
//                    BluetoothService.checkConnection();

                    // Open request for bluetooth if turned off
                    Log.i("BLE", String.valueOf(BluetoothService.ConnectSuccess));
                    Log.i("BLE", String.valueOf(BluetoothService.addOnConnectSuccess));
                    Log.i("BLE", String.valueOf(mIsCirculatioConnected));
                    Log.i("BLE", String.valueOf(mIsAddOnConnected));
                    if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                        if (BluetoothService.ConnectSuccess) {
                            btnConnection.setText(R.string.connected);
                            btnConnection.setTextColor(getApplication().getResources().getColor(R.color.circulatio_green));
                            mIsCirculatioConnected = true;
                            startButton.setEnabled(true);
                            Log.i("BLT", "Bluetooth connected...");
                        }
                        if (BluetoothService.addOnConnectSuccess) {
                            mIsAddOnConnected = true;
                            addOnView.setVisibility(View.VISIBLE);
                            activityType.setVisibility(View.VISIBLE);
                            addOnButton.setVisibility(View.GONE);
                            Log.i("BLT", "Add on connected...");

                        }
                    }else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                        Intent intentCR = new Intent();
                        intentCR.setAction(Constants.ACTION_CIRCULATIO_RECONNECT);
                        // Data you need to pass to activity
                        getApplicationContext().sendBroadcast(intentCR);

                        Intent intentAR = new Intent();
                        intentAR.setAction(Constants.ACTION_ADDON_RECONNECT);
                        // Data you need to pass to activity
                        getApplicationContext().sendBroadcast(intentAR);

                        if (!BluetoothService.addOnConnectSuccess) {
                            mIsAddOnConnected = false;
                            addOnView.setVisibility(View.GONE);
                            activityType.setVisibility(View.GONE);
                            addOnButton.setVisibility(View.VISIBLE);
                            Log.i("BLT", "Add on disconnected...");
                        }
                        if (!BluetoothService.ConnectSuccess) {
                            btnConnection.setText(R.string.not_connected);
                            btnConnection.setTextColor(getApplication().getResources().getColor(R.color.red));
                            mIsCirculatioConnected = false;
                            startButton.setEnabled(false);
                            Log.i("BLT", "Bluetooth disconnected...");
                        }
                    }
                }
            }, 2000);
        }
    };

    private void startBluetoothCheckTask() {
        final Handler h = new Handler();
        final int delay = 5000; // milliseconds

        h.postDelayed(new Runnable() {
            public void run() {
                // Open request for bluetooth if turned off
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                }
                h.postDelayed(this, delay);
            }
        }, 0);
    }

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

            ServiceConnection connection=new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    BluetoothService.MyBinder binderr=(BluetoothService.MyBinder)service;
                    bleService=binderr.getServiceSystem();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };
            Intent intentStartService = new Intent(this, BluetoothService.class);
            getApplicationContext().startService(intentStartService);
        } else {
            Log.i("Main Activity", "Circulatio Bluetooth service already running. Don't start it again.");
        }
        return alreadyRunning;
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.ellipsis_menu, popup.getMenu());
        popup.show();

//        if (mIsCirculatioConnected){
//            popup.getMenu().findItem(1).setVisible(false);
//            popup.getMenu().findItem(2).setVisible(true);
//        }
//        else{
//            popup.getMenu().findItem(1).setVisible(true);
//            popup.getMenu().findItem(2).setVisible(false);
//        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.connect) {
                    if(!mIsCirculatioConnected && (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF)){
                        addBLEOffDialog.show();
                    }
                    else if(!mIsCirculatioConnected && (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON)){
                        Intent intent = new Intent();
                        intent.setAction(Constants.ACTION_CIRCULATIO_RECONNECT);
                        // Data you need to pass to activity
                        getApplicationContext().sendBroadcast(intent);

                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            public void run() {
                                // Open request for bluetooth if turned off
                                if(!mIsCirculatioConnected){
                                    addCirculatioNotFoundDialog.show();
                                }
                                else {
                                    startButton.setEnabled(true);
                                }
                            }
                        }, 5000);
                    }
                }
                else if (menuItem.getItemId() == R.id.disconnect) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_CIRCULATIO_DISCONNECT);
                    // Data you need to pass to activity
                    getApplicationContext().sendBroadcast(intent);
                    btnConnection.setText(R.string.not_connected);
                    btnConnection.setTextColor(getApplication().getResources().getColor(R.color.red));
                }
                else if (menuItem.getItemId() == R.id.rename) {
                    // TODO
                    return true;
                }
                else if (menuItem.getItemId() == R.id.delete) {
                    // TODO
                    return true;
                }

                return false;
            }
        });
    }

    @SuppressLint({"MissingPermission", "ResourceType"})
    public void initMainActivity() {
        Log.i("MainActivity", "Initialising main activity");

        // Setup the part of the layout which is the same for both modes
        setContentView(R.layout.activity_home_screen);

        textViewName = findViewById(R.id.deviceName);
        User.loadUserData(getApplicationContext());
        String name = User.getName();
        name = name + "'s Circulatio";
        textViewName.setText(name);


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

        startBluetoothCheckTask();
        startCirculatioService();

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
//        filter1.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter1.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mReceiver, filter1);

        activityType = findViewById(R.id.manPositionImage);
        addOnButton = findViewById(R.id.addOnButton);
        addOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_ADDON_RECONNECT);
                // Data you need to pass to activity
                getApplicationContext().sendBroadcast(intent);
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    public void run() {
                        // Open request for bluetooth if turned off
                        if(!mIsAddOnConnected){
                            addAddOnNotFoundDialog.show();
                        }
                        else {
                            addOnView.setVisibility(View.VISIBLE);
                            activityType.setVisibility(View.VISIBLE);
                            addOnButton.setVisibility(View.GONE);
                        }
                    }
                }, 5000);
            }
        });
        addOnView = findViewById(R.id.yourPosition);

        if(SetUpInfo.useAddOn) {
            if (mIsAddOnConnected) {
                addOnView.setVisibility(View.VISIBLE);
                activityType.setVisibility(View.VISIBLE);
                addOnButton.setVisibility(View.GONE);
            } else {
                addOnView.setVisibility(View.GONE);
                activityType.setVisibility(View.GONE);
                addOnButton.setVisibility(View.VISIBLE);
            }
            initReceivers();
        }else{
            addOnView.setVisibility(View.GONE);
            activityType.setVisibility(View.GONE);
            addOnButton.setVisibility(View.GONE);
            TextView tView = findViewById(R.id.AddOnTitle);
            tView.setVisibility(View.GONE);
            TextView addOnFrame = findViewById(R.id.AddOnFrame);
            addOnFrame.setVisibility(View.GONE);
        }

        btnConnection = findViewById(R.id.bltButton);

        startButton = findViewById(R.id.btnStartMassage1);
        startButton.setEnabled(false);

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

        addAddOnNotFoundDialog = new AlertDialog.Builder(this).setTitle("Add On Not Found")
                .setMessage("Please ensure add on is turned on")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }});

        startMassageNotifDialog = new AlertDialog.Builder(this).setTitle("Please Start Massage")
                .setMessage("You have been sitting for too long, please activate massage!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }});

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inn1=getIntent();
                inn1=new Intent(MainActivity.this,MassageSetup.class);
                startActivity(inn1);
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



//        btnConnection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(!mIsCirculatioConnected && (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF)){
//                    addBLEOffDialog.show();
//                }
//                else if(!mIsCirculatioConnected && (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON)){
//                    Intent intent = new Intent();
//                    intent.setAction(Constants.ACTION_CIRCULATIO_RECONNECT);
//                    // Data you need to pass to activity
//                    getApplicationContext().sendBroadcast(intent);
//                    btnConnection.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(!mIsCirculatioConnected){
//                                addCirculatioNotFoundDialog.show();
//                            }
//                            else {
//                                startButton.setEnabled(true);
//                            }
//                        }
//                    }, 2000);
//
//                }
//            }
//        });

    }

    private void updateCirculatioConnection(boolean isConnected) {
        mIsCirculatioConnected = isConnected;
    }

    private void initReceivers(){
        final IntentFilter sittingIntent = new IntentFilter();
        sittingIntent.addAction(Constants.ACTION_USER_SITTING);
        sittingReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("BLE", "Got sitting message");
                activityType.setImageResource(R.drawable.man_sitting_on_chair_silhouette);
            }
        };
        registerReceiver(sittingReceiver, sittingIntent);

        final IntentFilter standingIntent = new IntentFilter();
        standingIntent.addAction(Constants.ACTION_USER_STANDING);
        standingReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("BLE", "Got standing message");
                activityType.setImageResource(R.drawable.man_standing_silhouette);
            }
        };
        registerReceiver(standingReceiver, standingIntent);

        final IntentFilter notifIntent = new IntentFilter();
        notifIntent.addAction(Constants.ACTION_MASSAGE_NOTIF);
        notifReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("BLE", "Got notification message");
                startMassageNotifDialog.show();
            }
        };
        registerReceiver(notifReceiver, notifIntent);



    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "App was brought into foreground (Main Activity) resumed");
        mIsActivityRunning = true;

        Boolean isTouched = false;

//        if(mIsCirculatioConnected){
//            btnConnection.setText(R.string.connected);
//            btnConnection.setTextColor(getApplication().getResources().getColor(R.color.circulatio_green));
//        }
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