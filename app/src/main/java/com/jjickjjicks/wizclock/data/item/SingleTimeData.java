package com.jjickjjicks.wizclock.data.item;

public class SingleTimeData {
    private int hour = 0;
    private int minute = 0;
    private int second = 0;

    public SingleTimeData(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
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
        return (hour * 1000 * 3600) + (minute * 1000 * 60) + (second * 1000);
    }
}
