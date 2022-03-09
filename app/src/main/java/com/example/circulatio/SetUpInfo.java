package com.example.circulatio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class SetUpInfo extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_info);
    }

    public void blinkInfoButton(View view) {
        ImageButton button = (ImageButton) findViewById(R.id.buttonInfo1);
        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blinking_animation_not_repeated);
        button.startAnimation(startAnimation);
    }
}