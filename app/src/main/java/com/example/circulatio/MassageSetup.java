package com.example.circulatio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MassageSetup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage_setup);

        ImageButton btnInfo = findViewById(R.id.buttonInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inn1 = getIntent();
                inn1 = new Intent(MassageSetup.this, UserManual.class);
                startActivity(inn1);
            }
        });

//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mIsCirculatioConnected && startButton.getText().equals(getString(R.string.start_massage))) {
//                    startButton.setText(R.string.stop_massage_button);
//                    Intent intent = new Intent();
//                    intent.setAction(Constants.ACTION_CIRCULATIO_BUZZER_ON);
//                    // Data you need to pass to activity
//                    getApplicationContext().sendBroadcast(intent);
//                }
//                else if (mIsCirculatioConnected && startButton.getText().equals(getString(R.string.stop_massage_button))) {
//                    startButton.setText(R.string.start_massage);
//                    Intent intent = new Intent();
//                    intent.setAction(Constants.ACTION_CIRCULATIO_BUZZER_OFF);
//                    // Data you need to pass to activity
//                    getApplicationContext().sendBroadcast(intent);
//                }
//            }
//        });
    }
}