package com.jjickjjicks.wizclock.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.jjickjjicks.wizclock.ui.dialog.InternetDialog;
import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.data.AccessSettings;

import androidx.appcompat.app.AppCompatActivity;

import static com.jjickjjicks.wizclock.data.AccessSettings.OFFLINE_ACCESS;
import static com.jjickjjicks.wizclock.data.AccessSettings.ONLINE_ACCESS;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (new InternetDialog(SplashActivity.this).getInternetStatus()) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    AccessSettings.setAccessMode(ONLINE_ACCESS);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    AccessSettings.setAccessMode(OFFLINE_ACCESS);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_TIME);


    }
}
