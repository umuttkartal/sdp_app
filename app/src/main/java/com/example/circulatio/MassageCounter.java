package com.example.circulatio;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
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

    AlertDialog.Builder addBLEOffDialog;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // Open request for bluetooth if turned off
            Log.i("BLE", String.valueOf(BluetoothService.ConnectSuccess));
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                Intent intentCR = new Intent();
                intentCR.setAction(Constants.ACTION_CIRCULATIO_RECONNECT);
                // Data you need to pass to activity
                getApplicationContext().sendBroadcast(intentCR);
            }
            if(!BluetoothService.ConnectSuccess){
                addBLEOffDialog.show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage_counter);

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        registerReceiver(mReceiver, filter1);

        addBLEOffDialog = new AlertDialog.Builder(this).setTitle("Connection Lost")
                .setMessage("Please go back to the main page to your Circulatio")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent inn1=getIntent();
                        inn1=new Intent(MassageCounter.this,MainActivity.class);
                        startActivity(inn1);
                        finish();
                    }});

        countDownTimerText = (TextView)findViewById(R.id.textCountdown);
        pauseButton = (Button)findViewById(R.id.pauseButton);
        stopButton = (Button)findViewById(R.id.stopButton);

        setTimer(MassageController.getLength(getApplicationContext()));

//        Log.i("MASSAGE COUNTER", MassageController.getLength(getApplicationContext()).toString());

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
                    countDownTimer.cancel();
                }else if((pauseButton.getText().toString()).equals("CONTINUE")){
                    String zeros = "00000";
                    String out = zeros.substring(0, 5 - Integer.toString(timerLeft).length()) + Integer.toString(timerLeft);
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_CIRCULATIO_BUZZER_ON);
                    intent.putExtra("intensity", Integer.toString(MassageController.getIntensity(getApplicationContext())));
                    intent.putExtra("duration", out);
                    // Data you need to pass to activity
                    getApplicationContext().sendBroadcast(intent);
                    setTimer(timerLeft);
                    pauseButton.setText(R.string.pause);
                }
            }
        });
    }

    public void setTimer(Integer time) {
        countDownTimer = new CountDownTimer((int)time * ONE_SECOND_IN_MILLISECONDS,ONE_SECOND_IN_MILLISECONDS) {

            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int)(Math.floor((millisUntilFinished / 1000) / 60));
                int seconds = (int)((millisUntilFinished / 1000) % 60);

                timerLeft = (int)(millisUntilFinished / 1000);
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
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);

    }
}