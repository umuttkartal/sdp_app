package com.example.circulatio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * This class control the Setup Info Screen.
 */
public class SetUpInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private String[] deviceList = {"Circulatio Device"};

    private EditText textEnterName;
    private Spinner spinnerBluetoothDevice;
    private EditText textEnterPin;
    private CheckBox checkboxUserManual;
    private Button buttonSubmit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_info);

        this.textEnterName  = findViewById(R.id.enter_name);
        this.spinnerBluetoothDevice = findViewById(R.id.bluetooth_devices_spinner);
        this.textEnterPin =  findViewById(R.id.enter_pin);
        this.checkboxUserManual = findViewById(R.id.checkbox_user_manual);
        this.buttonSubmit = findViewById(R.id.btn_submit_setup_info);

        spinnerBluetoothDevice.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,deviceList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBluetoothDevice.setAdapter(aa);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String name = textEnterName.getText().toString();
                String deviceId = "";//spinnerBluetoothDevice.getText().toString();
                String pin = textEnterPin.getText().toString();
                boolean userManual = checkboxUserManual.isChecked();

                if (User.isValidUserData(name, deviceId, pin, userManual)) {
                    User.createUser(name, deviceId, pin, userManual, getApplicationContext());
                    Intent i = new Intent(SetUpInfo.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(),deviceList[position] , Toast.LENGTH_LONG).show();
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
}