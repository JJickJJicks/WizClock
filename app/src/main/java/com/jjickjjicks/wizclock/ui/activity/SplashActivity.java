package com.jjickjjicks.wizclock.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.jjickjjicks.wizclock.AccessSettings;
import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.ui.dialog.InternetDialog;


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
                    onlineProcess();
                } else {
                    offlineProcess();
                }
            }
        }, SPLASH_DISPLAY_TIME);


    }

    public void onlineProcess() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        ((AccessSettings) this.getApplication()).setAccessMode(AccessSettings.ONLINE_ACCESS);
        startActivity(intent);
        this.finish();
    }

    public void offlineProcess() {
        ((AccessSettings) this.getApplication()).setAccessMode(AccessSettings.OFFLINE_ACCESS);
    }
}
