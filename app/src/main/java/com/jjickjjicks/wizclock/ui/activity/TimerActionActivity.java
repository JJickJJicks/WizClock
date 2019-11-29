package com.jjickjjicks.wizclock.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.data.adapter.SingleTimeDataAdapter;
import com.jjickjjicks.wizclock.data.item.SingleTimeData;
import com.jjickjjicks.wizclock.data.item.TimerData;
import com.jjickjjicks.wizclock.service.AppNotificationManager;

import java.util.ArrayList;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import io.fabric.sdk.android.Fabric;

public class TimerActionActivity extends AppCompatActivity implements View.OnClickListener {
    final static int MODE_FIRST = 0, MODE_START = 1, MODE_PAUSE = 2, MODE_STOP = 3;
    private static final CircularProgressIndicator.ProgressTextAdapter TIME_TEXT_ADAPTER = new CircularProgressIndicator.ProgressTextAdapter() {
        @Override
        public String formatText(double time) {
            int hours = (int) (time / 3600);
            time %= 3600;
            int minutes = (int) (time / 60);
            int seconds = (int) (time % 60);
            StringBuilder sb = new StringBuilder();
            if (hours < 10) {
                sb.append(0);
            }
            sb.append(hours).append(":");
            if (minutes < 10) {
                sb.append(0);
            }
            sb.append(minutes).append(":");
            if (seconds < 10) {
                sb.append(0);
            }
            sb.append(seconds);
            return sb.toString();
        }

        public String formatText(long time) {
            return formatText(Double.valueOf(time));
        }
    };
    private CircularProgressIndicator circularProgress;
    private RecyclerView timeList;
    private Button btnStartPause, btnReset;
    private long remainTime = 0;
    private long mTimeLeftInMillis;
    private ArrayList<SingleTimeData> timeArrayList, currentArrayList;
    private SingleTimeDataAdapter adapter = new SingleTimeDataAdapter();
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_action);

        Fabric.with(this, new Crashlytics());

        String JsonLoad = getIntent().getStringExtra("timerData");

        timeArrayList = new ArrayList<>();
        ArrayList<Long> tempTimeList = new TimerData(JsonLoad).getRecursiveTimeList();
        for (long i : tempTimeList)
            timeArrayList.add(new SingleTimeData(i));

        circularProgress = findViewById(R.id.circular_progress);
        timeList = findViewById(R.id.timeList);
        btnStartPause = findViewById(R.id.btnStartPause);
        btnReset = findViewById(R.id.btnReset);

        btnStartPause.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        updateUI();
    }

    private void actTimer(long time, final long totalTime) {
        mTimeLeftInMillis = time;
        setTimerUI(mTimeLeftInMillis, totalTime);
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                setTimerUI(mTimeLeftInMillis, totalTime);
            }

            @Override
            public void onFinish() {
                setTimerUI(mTimeLeftInMillis, totalTime);
                currentArrayList.remove(0);
                adapter = new SingleTimeDataAdapter(currentArrayList);
                timeList.setAdapter(adapter);
                AppNotificationManager appNotificationManager = new AppNotificationManager(TimerActionActivity.this);
                if (currentArrayList.size() > 0) {
                    appNotificationManager.showFCMDefaultNotification("중간 항목 완료", "중간 항목이 완료되었습니다.");
                    actTimer(currentArrayList.get(0).getMiliSecond(), currentArrayList.get(0).getMiliSecond());
                } else {
                    appNotificationManager.showFCMDefaultNotification("모든 항목 완료", "모든 항목이 완료되었습니다.");
                    setBtnUI(MODE_STOP);
                }
            }
        }.start();
    }

    private void updateUI() {
        currentArrayList = new ArrayList<>();
        currentArrayList.addAll(timeArrayList);

        adapter = new SingleTimeDataAdapter(currentArrayList);
        timeList.setAdapter(adapter);

        setTimerUI(currentArrayList.get(0).getMiliSecond(), currentArrayList.get(0).getMiliSecond());
        remainTime = currentArrayList.get(0).getMiliSecond();
        setBtnUI(MODE_FIRST);
    }

    private void setTimerUI(long currentTime, long totalTime) {
        circularProgress.setMaxProgress(totalTime / 1000);
        circularProgress.setCurrentProgress((int) currentTime / 1000);
        circularProgress.setProgressTextAdapter(TIME_TEXT_ADAPTER);
    }

    private void setBtnUI(int mode) {
        switch (mode) {
            case MODE_FIRST:
                btnStartPause.setText(R.string.start);
                btnStartPause.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.INVISIBLE);
                break;
            case MODE_START:
                btnStartPause.setText(R.string.pause);
                btnStartPause.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.INVISIBLE);
                break;
            case MODE_PAUSE:
                btnStartPause.setText(R.string.start);
                btnStartPause.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                break;
            case MODE_STOP:
                btnStartPause.setText(R.string.start);
                btnStartPause.setVisibility(View.INVISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnStartPause) {
            if (btnStartPause.getText().toString().equals(getResources().getString(R.string.start))) {
                actTimer(remainTime, currentArrayList.get(0).getMiliSecond());
                setBtnUI(MODE_START);
            } else {
                mCountDownTimer.cancel();
                remainTime = mTimeLeftInMillis;
                setBtnUI(MODE_PAUSE);
            }
        } else if (v.getId() == R.id.btnReset) {
            updateUI();
            setBtnUI(MODE_FIRST);
        }
    }
}
