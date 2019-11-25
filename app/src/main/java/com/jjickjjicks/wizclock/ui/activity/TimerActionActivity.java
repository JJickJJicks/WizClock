package com.jjickjjicks.wizclock.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import io.fabric.sdk.android.Fabric;

import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.jjickjjicks.wizclock.R;

public class TimerActionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_action);

        Fabric.with(this, new Crashlytics());
    }
}
