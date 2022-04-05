package com.example.circulatio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

public class MassageSetup extends AppCompatActivity {

    Button startButton;
    Button recommendedButton;
    private Spinner intensitySpinner;
    private EditText textEnterLength;
    String length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage_setup);

        this.intensitySpinner = findViewById(R.id.massageSpeedSpinner);
        this.textEnterLength =  findViewById(R.id.massageLengthSpinner);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.massageIntensity, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        intensitySpinner.setAdapter(adapter);

        ImageButton btnInfo = findViewById(R.id.buttonInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inn1 = getIntent();
                inn1 = new Intent(MassageSetup.this, UserManual.class);
                startActivity(inn1);
            }
        });

        startButton = findViewById(R.id.btnStartMassage);
        recommendedButton = findViewById(R.id.btn_prefill_massage);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer length = Integer.parseInt(textEnterLength.getText().toString()) * 60;
                Log.i("MASSAGE SETUP", length.toString());

                String zeros = "00000";
                String out = zeros.substring(0, 5 - length.toString().length()) + length.toString();


                String intensity = intensitySpinner.getSelectedItem().toString();
                String intensityNum = "14";
                if (intensity.equals("Low")) {
                    intensityNum = "51";
                }
                if (intensity.equals("Medium")) {
                    intensityNum = "47";
                }
                if (intensity.equals("High")) {
                    intensityNum = "14";
                }

                MassageController.setLength(length, getApplicationContext());
                MassageController.setIntensity(Integer.parseInt(intensityNum), getApplicationContext());
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_CIRCULATIO_BUZZER_ON);
                intent.putExtra("intensity", intensityNum);
                intent.putExtra("duration", out);
                // Data you need to pass to activity
                getApplicationContext().sendBroadcast(intent);

                Intent inn1 = getIntent();
                inn1 = new Intent(MassageSetup.this, MassageCounter.class);
                startActivity(inn1);
                finish();
            }
        });

        recommendedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer length = 15 * 60;
                Log.i("MASSAGE SETUP", length.toString());

                String zeros = "00000";
                String out = zeros.substring(0, 5 - length.toString().length()) + length.toString();

                MassageController.setLength(length, getApplicationContext());
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_CIRCULATIO_BUZZER_ON);
                intent.putExtra("intensity", "14");
                intent.putExtra("duration", out);
                // Data you need to pass to activity
                getApplicationContext().sendBroadcast(intent);

                Intent inn1=getIntent();
                inn1=new Intent(MassageSetup.this,MassageCounter.class);
                startActivity(inn1);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.massageIntensity, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        intensitySpinner.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}