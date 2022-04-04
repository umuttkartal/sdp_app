package com.example.circulatio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

public class MassageSetup extends AppCompatActivity {

    Button startButton;
    private Spinner intensitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage_setup);

        this.intensitySpinner = findViewById(R.id.massageSpeedSpinner);

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

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_CIRCULATIO_BUZZER_ON);
//                intent.putExtra("type", "02");
//                intent.putExtra("intensity", "14");
//                intent.putExtra("duration", "01");
                // Data you need to pass to activity
                getApplicationContext().sendBroadcast(intent);

                Intent inn1=getIntent();
                inn1=new Intent(MassageSetup.this,MassageCounter.class);
                startActivity(inn1);
                finish();
            }
        });
    }
}