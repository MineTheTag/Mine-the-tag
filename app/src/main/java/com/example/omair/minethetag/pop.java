package com.example.omair.minethetag;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.widget.Chronometer;
import android.widget.TextView;

public class pop extends Activity {

    TextView timer ;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        timer = (TextView) findViewById(R.id.textView2);
        getWindow().setLayout((int) (width * 0.55), (int) (height * 0.25));

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                startActivity(new Intent(pop.this, MainActivity.class));
            }
        }.start();
    }
}
