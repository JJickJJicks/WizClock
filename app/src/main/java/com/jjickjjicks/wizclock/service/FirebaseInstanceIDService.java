package com.jjickjjicks.wizclock.service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() == null)
            return;

        String title = (remoteMessage.getNotification().getTitle() == null) ? "WizClock FCM TEST" : remoteMessage.getNotification().getTitle();
        String body = (remoteMessage.getNotification().getBody() == null) ? "죄송합니다. WizClock FCM TEST 중입니다." : remoteMessage.getNotification().getBody();
        String channelID = remoteMessage.getNotification().getChannelId();

        AppNotificationManager appNotificationManager = new AppNotificationManager(this);
        if (channelID == null)
            appNotificationManager.showFCMDefaultNotification(title, body);
        else
            appNotificationManager.showFCMFunctionalNotification(title, body, channelID);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        /*
         * 기존의 FirebaseInstanceIdService에서 수행하던 토큰 생성, 갱신 등의 역할은 이제부터
         * FirebaseMessaging에 새롭게 추가된 위 메소드를 사용하면 된다.
         */
    }
}