package com.khadijael.places;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    View circle1;
    ImageView circle2;
    private Handler handlerAnimation = new Handler();
    private Boolean statusAnimation = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        circle1 = findViewById(R.id.imgAnimation1);
        circle2 = findViewById(R.id.imgAnimation2);
        startPulse();
        Thread t1 = new Thread(){
            @Override
            public void run() {
                try {

                    sleep(6000);
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    stopPulse();
                    startActivity(intent);
                    SplashActivity.this.finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t1.start();
    }




    private void startPulse() {
        runnable.run();

    }

    private void stopPulse() {
        handlerAnimation.removeCallbacks(runnable);
    }

    private Runnable runnable = new  Runnable() {
        public void run() {

            circle1.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1000)
                    .withEndAction (new Runnable() {
                        @Override
                        public void run() {
                            //rcle1.clearAnimation();
                            circle1.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(0);}
                    }).start();



            circle2.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(700)
                    .withEndAction (new Runnable() {
                        @Override
                        public void run() {
                            //rcle2.clearAnimation();
                            circle2.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(0);}
                    }).start();
            /**rcle2.scaleX = 1f,
             circle2.scaleY = 1f,
             circle2.alpha = 1f*/


            handlerAnimation.postDelayed(this, 1500);
        }
    };


}