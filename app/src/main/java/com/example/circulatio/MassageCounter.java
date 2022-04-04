package com.example.circulatio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MassageCounter extends AppCompatActivity {

    private int ONE_SECOND_IN_MILLISECONDS = 1000;

    private TextView countDownTimerText;
    private Button pauseButton;
    private Button stopButton;

    private int timerLeft;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage_counter);

        countDownTimerText = (TextView)findViewById(R.id.textCountdown);
        pauseButton = (Button)findViewById(R.id.pauseButton);
        stopButton = (Button)findViewById(R.id.stopButton);

        setTimer(MassageController.getLength(getApplicationContext()));

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = MassageController.StopMessage();

                //send stop message via bluetooth

                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_CIRCULATIO_BUZZER_OFF);
                // Data you need to pass to activity
                getApplicationContext().sendBroadcast(intent);

//                Intent in = new Intent(getApplicationContext(), BluetoothService.class);
//                getApplicationContext().stopService(in);

//                Handler h = new Handler();
//                h.postDelayed(new Runnable() {
//                    public void run() {
//                        // Open request for bluetooth if turned off
//                        Intent i = new Intent(MassageCounter.this, MainActivity.class);
//                        startActivity(i);
//                        finish();
//                    }
//                }, 2000);
                Intent i = new Intent(MassageCounter.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((pauseButton.getText().toString()).equals("PAUSE")) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_CIRCULATIO_BUZZER_OFF);
                    // Data you need to pass to activity
                    getApplicationContext().sendBroadcast(intent);
                    pauseButton.setText(R.string.continue_massage);
                }else if((pauseButton.getText().toString()).equals("CONTINUE")){
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_CIRCULATIO_BUZZER_ON);
                    // Data you need to pass to activity
                    getApplicationContext().sendBroadcast(intent);
                    pauseButton.setText(R.string.pause);
                }

            }
        });
    }

    public void setTimer(int time) {
        countDownTimer = new CountDownTimer(time * ONE_SECOND_IN_MILLISECONDS,ONE_SECOND_IN_MILLISECONDS) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int)(Math.floor((millisUntilFinished / 1000) / 60));
                int seconds = (int)((millisUntilFinished / 1000) % 60);

                countDownTimerText.setText(String.format("%02d : %02d",minutes,seconds));
            }

            @Override
            public void onFinish() {
                countDownTimerText.setText("Done");
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_CIRCULATIO_BUZZER_OFF);
                // Data you need to pass to activity
                getApplicationContext().sendBroadcast(intent);
            }
        };
    }
}