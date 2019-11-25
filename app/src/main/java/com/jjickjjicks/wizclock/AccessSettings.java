package com.jjickjjicks.wizclock;

import android.app.Application;

public class AccessSettings extends Application {
    public final static int ONLINE_ACCESS = 0, OFFLINE_ACCESS = 1;
    private int accessMode;

    public int getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(int accessMode) {
        this.accessMode = accessMode;
    }
}
