package com.example.circulatio;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * This class control the Setup Info Screen.
 */
public class SetUpInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


//    private String[] deviceList = {"Circulatio Device"};
    private ArrayList<String> deviceList = new ArrayList<>();

    private EditText textEnterName;
    private Spinner spinnerBluetoothDevice;
    private EditText textEnterPin;
    private CheckBox checkboxUserManual;
    private Button buttonSubmit;

    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;

    private ArrayList<String> addressList = new ArrayList<>();

    private String deviceAddress = null;

    private String selectedDevice = null;

    AlertDialog.Builder wrongInputDialog;

    AlertDialog.Builder wrongDeviceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_info);

        this.textEnterName  = findViewById(R.id.enter_name);
        this.spinnerBluetoothDevice = findViewById(R.id.bluetooth_devices_spinner);
        this.textEnterPin =  findViewById(R.id.enter_pin);
        this.checkboxUserManual = findViewById(R.id.checkbox_user_manual);
        this.buttonSubmit = findViewById(R.id.btn_submit_setup_info);

        ImageButton btnInfo =  findViewById(R.id.buttonInfo1);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent inn1=getIntent();
                inn1=new Intent(SetUpInfo.this,UserManual.class);
                startActivity(inn1);
            }
        });


        wrongInputDialog = new AlertDialog.Builder(this).setTitle("Wrong Input")
                .setMessage("Please ensure you entered correct information")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }});

        wrongDeviceDialog = new AlertDialog.Builder(this).setTitle("Wrong Device Selected")
                .setMessage("Please ensure you picked the right device")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }});

        buttonSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String name = textEnterName.getText().toString();
                String deviceId = "";//spinnerBluetoothDevice.getText().toString();
                String pin = textEnterPin.getText().toString();
                boolean userManual = checkboxUserManual.isChecked();

                if (User.isValidUserData(name, deviceId, pin, userManual) && selectedDevice.contains("Circulatio")) {
                    User.createUser(name, deviceId, pin, userManual, getApplicationContext());
                    Intent i = new Intent(SetUpInfo.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else if(User.isValidUserData(name, deviceId, pin, userManual) && !selectedDevice.contains("Circulatio")){

                    wrongDeviceDialog.show();
                }
                else{
                    wrongInputDialog.show();
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(), deviceList.get(position), Toast.LENGTH_LONG).show();
        deviceAddress = addressList.get(position);
        selectedDevice = deviceList.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // Needs Implemented
    }

    public void blinkInfoButton(View view) {
        ImageButton button = (ImageButton) findViewById(R.id.buttonInfo1);
        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blinking_animation_not_repeated);
        button.startAnimation(startAnimation);
    }


    @Override
    protected void onResume() {
        super.onResume();
        
        spinnerBluetoothDevice.setOnItemSelectedListener(this);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if ( myBluetooth==null ) {
            Toast.makeText(getApplicationContext(), "Bluetooth device not available", Toast.LENGTH_LONG).show();
            finish();
        }
        else if ( !myBluetooth.isEnabled() ) {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }

        pairedDevices = myBluetooth.getBondedDevices();
//        ArrayList list = new ArrayList();

        if ( pairedDevices.size() > 0 ) {
            for ( BluetoothDevice bt : pairedDevices ) {
                deviceList.add(bt.getName().toString());
                addressList.add(bt.getAddress().toString());
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, deviceList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBluetoothDevice.setAdapter(aa);
    }
}