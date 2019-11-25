package com.jjickjjicks.wizclock.data;

import android.app.Application;

public class AccessSettings extends Application {
    public final static int OFFLINE_ACCESS = 0, ONLINE_ACCESS = 1;

    private int accessMode;

    public static int getAccessMode() {
        return accessMode;
    }

    public static void setAccessMode(int accessMode) {
        this.accessMode = accessMode;
    }
}
