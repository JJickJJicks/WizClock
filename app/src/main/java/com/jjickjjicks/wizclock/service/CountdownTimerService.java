//package com.jjickjjicks.wizclock;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.AppNotificationManager;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.graphics.BitmapFactory;
//import android.media.AudioAttributes;
//import android.net.Uri;
//import android.os.Build;
//import android.os.CountDownTimer;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.jjickjjicks.wizclock.data.adapter.TimerAdapter;
//
//import androidx.core.app.NotificationCompat;
//
//
//import java.util.Locale;
//
//public class CountdownTimerService {
//    private Context activity;
//    private static final String NOTIFICATION_CHANNEL_ID = "NotifyChannel";
//    private TextView mTextViewCountDown;
//    private Button mButtonStartPause, mButtonReset;
//    private CountDownTimer mCountDownTimer;
//    private String mTimerStatus = "stop";
//    private long mTimeLeftInMillis, remainTime;
//    private int remainIndex, repeatCnt;
//    private TimerAdapter adapter;
//
//    public CountdownTimerService(Button mButtonStartPause, Button mButtonReset, TextView mTextViewCountDown, TimerAdapter adapter, int repeatCnt, Context activity) {
//        this.mButtonStartPause = mButtonStartPause;
//        this.mButtonReset = mButtonReset;
//        this.mTextViewCountDown = mTextViewCountDown;
//        this.adapter = adapter;
//        this.repeatCnt = repeatCnt;
//        this.activity = activity;
//    }
//
//    public void setInformation(Button mButtonStartPause, Button mButtonReset, TextView mTextViewCountDown, TimerAdapter adapter, int repeatCnt, Context activity) {
//        this.mButtonStartPause = mButtonStartPause;
//        this.mButtonReset = mButtonReset;
//        this.mTextViewCountDown = mTextViewCountDown;
//        this.adapter = adapter;
//        this.repeatCnt = repeatCnt;
//        this.activity = activity;
//    }
//
//    public void activation() {
//        if (mTimerStatus == "run") {
//            pauseTimer();
//        } else if (mTimerStatus == "pause") {
//            startTimer(remainIndex, remainTime);
//        } else if (adapter.getCount() != 0) {
//            makeRepetition(repeatCnt);
//            remainIndex = 0;
//            startTimer(0, adapter.getItem(0).getmTimeLeftInMillis());
//        }
//    }
//
//    private void startTimer(int index, long time) {
//        mTimerStatus = "run";
//        mButtonStartPause.setText(R.string.pause);
//        mButtonReset.setVisibility(View.INVISIBLE);
//        actTimer(index, time);
//    }
//
//    private void actTimer(final int index, long time) {
//        mTimeLeftInMillis = time;
//        updateCountDownText();
//        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                mTimeLeftInMillis = millisUntilFinished;
//                updateCountDownText();
//            }
//
//            @Override
//            public void onFinish() {
//                if (index + 1 != adapter.getCount()) {
//                    createNotification("중간 항목 완료", "중간 항목이 완료되었습니다.");
//                    actTimer(index + 1, adapter.getItem(index + 1).getmTimeLeftInMillis());
//                } else {
//                    createNotification("모든 항목 완료", "모든 항목이 완료되었습니다.");
//                    mTimerStatus = "stop";
//                    mButtonStartPause.setText(R.string.start);
//                    mButtonStartPause.setVisibility(View.INVISIBLE);
//                    mButtonReset.setVisibility(View.VISIBLE);
//                }
//            }
//        }.start();
//    }
//
//    private void makeRepetition(int cnt) {
//        long[] temp = new long[adapter.getCount()];
//        for (int i = 0; i < temp.length; i++)
//            temp[i] = adapter.getItem(i).getmTimeLeftInMillis();
//        for (int i = 0; i < cnt - 1; i++) {
//            for (int j = 0; j < temp.length; j++)
//                adapter.addTimer(adapter.getCount() + 1, temp[j]);
//        }
//    }
//
//    private void pauseTimer() {
//        mCountDownTimer.cancel();
//        mTimerStatus = "pause";
//        remainTime = mTimeLeftInMillis;
//        mButtonStartPause.setText(R.string.start);
//        mButtonReset.setVisibility(View.VISIBLE);
//    }
//
//    public void resetTimer() {
//        mTimerStatus = "stop";
//        mButtonReset.setVisibility(View.INVISIBLE);
//        mButtonStartPause.setVisibility(View.VISIBLE);
//        mTimeLeftInMillis = 0;
//        updateCountDownText();
//        adapter.clear();
//    }
//
//    public void updateCountDownText() {
//        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
//        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
//        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
//
//        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
//
//        mTextViewCountDown.setText(timeLeftFormatted);
//    }
//
//    public void createNotification(String title, String text) {
//        AppNotificationManager notificationManager = (AppNotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + activity.getPackageName() + "/" + R.raw.shipbell); // Alarm mp3 위치 지정(오류 있는 듯)
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, NOTIFICATION_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_launcher_foreground) //BitMap 이미지 요구
//                .setContentTitle(title)
//                .setContentText(text)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
//                .setSound(soundUri)
//                .setAutoCancel(true);
//
//        //OREO API 26 이상에서는 채널 필요
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            builder.setSmallIcon(R.drawable.ic_launcher); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
//            builder.setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher)); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
//            int importance = AppNotificationManager.IMPORTANCE_HIGH;
//
//            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, title, importance);
//            channel.setDescription(text);
//            AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                    .build();
//            channel.setSound(soundUri, audioAttributes);
//
//            // 노티피케이션 채널을 시스템에 등록
//            assert notificationManager != null;
//            notificationManager.createNotificationChannel(channel);
//        } else {
//            builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
//            builder.setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher)); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
//        }
//        assert notificationManager != null;
//        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
//    }
//
//    public String getmTimerStatus() {
//        return mTimerStatus;
//    }
//}
