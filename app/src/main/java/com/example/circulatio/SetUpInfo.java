package com.example.circulatio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * This class control the Setup Info Screen.
 */
public class SetUpInfo extends AppCompatActivity {

    private EditText textEnterName;
    private Spinner spinnerBluetoothDevice;
    private CheckBox checkboxUserManual;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_info);

        this.textEnterName  = findViewById(R.id.enter_name);
        this.spinnerBluetoothDevice = findViewById(R.id.bluetooth_devices_spinner);
        this.checkboxUserManual = findViewById(R.id.checkbox_user_manual);
        this.buttonSubmit = findViewById(R.id.btn_submit_setup_info);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String name = textEnterName.getText().toString();
                String deviceId = "";//spinnerBluetoothDevice.getText().toString();
                boolean userManual = checkboxUserManual.isChecked();

                if (User.isValidUserData(name, deviceId, userManual)) {
                    User.createUser(name, deviceId, userManual, getApplicationContext());
                    Intent i = new Intent(SetUpInfo.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }


}