package com.jjickjjicks.wizclock.data.item;

public class SingleTimeData {
    private int hour = 0;
    private int minute = 0;
    private int second = 0;
    private long miliSecond = 0;

    public SingleTimeData(long miliSecond) {
        this.miliSecond = miliSecond;
        this.hour = (int) ((miliSecond / 1000) / 3600);
        this.minute = (int) (((miliSecond / 1000) % 3600) / 60);
        this.second = (int) (((miliSecond / 1000) % 3600) % 60);
    }

    public SingleTimeData(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.miliSecond = (hour * 1000 * 3600) + (minute * 1000 * 60) + (second * 1000);
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public long getMiliSecond() {
        return miliSecond;
    }
}
