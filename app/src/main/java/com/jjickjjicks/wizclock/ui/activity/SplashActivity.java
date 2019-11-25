package com.jjickjjicks.wizclock.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.jjickjjicks.wizclock.ui.dialog.InternetDialog;
import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.data.item.AccessSettings;

import androidx.appcompat.app.AppCompatActivity;

import static com.jjickjjicks.wizclock.data.item.AccessSettings.OFFLINE_ACCESS;
import static com.jjickjjicks.wizclock.data.item.AccessSettings.ONLINE_ACCESS;

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
                AccessSettings accessSettings = new AccessSettings();
                if (new InternetDialog(SplashActivity.this).getInternetStatus()) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    accessSettings.setAccessMode(ONLINE_ACCESS);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    accessSettings.setAccessMode(OFFLINE_ACCESS);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_TIME);


    }
}
