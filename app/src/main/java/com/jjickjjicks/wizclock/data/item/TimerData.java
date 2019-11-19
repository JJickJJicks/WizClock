package com.jjickjjicks.wizclock.data.item;

import android.util.Log;

import com.google.firebase.database.Exclude;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TimerData {
    private int timeCnt;
    private List<Long> timeList;

    public TimerData() {
        this.timeCnt = 1;
        this.timeList = new ArrayList<>();
    }

    public TimerData(String Json) {
        try {
            Log.d("JsonCheck 2", Json);
            JSONObject jsonObject = new JSONObject(Json);
            this.timeCnt = jsonObject.getInt("timeCnt");
            JSONArray timeListArr = jsonObject.getJSONArray("timeList");
            timeList = new ArrayList<>();
            for (int i = 0; i < timeListArr.length(); i++)
                timeList.add(timeListArr.optLong(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public TimerData(int timeCnt, List<Long> timeList) {
        this.timeCnt = timeCnt;
        this.timeList = timeList;
    }

    public void setTimeCnt(int timeCnt) {
        this.timeCnt = timeCnt;
    }

    public void setmTimeLeftInMillis(ArrayList<Long> timeList) {
        this.timeList = timeList;
    }

    public void addTime(long miliSecond) {
        timeList.add(miliSecond);
    }

    public int getTimeCnt() {
        return timeCnt;
    }

    public ArrayList<Long> getTimeList() { // Data View 용
        return new ArrayList<Long>(timeList);
    }

    public ArrayList<Long> getRecursiveTimeList() { // 반복으로 생성된 timeList(최종 실행용)
        ArrayList<Long> recursiveTimeList = new ArrayList<>();
        for (int i = 0; i < timeCnt; i++)
            recursiveTimeList.addAll(timeList);

        return recursiveTimeList;
    }

    // 이하 출력용 계산 메소드
    private int getHour(long mTimeLeftInMillis) {
        return (int) (mTimeLeftInMillis / 1000) / 3600;
    }

    private int getMin(long mTimeLeftInMillis) {
        return (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
    }

    private int getSec(long mTimeLeftInMillis) {
        return (int) (mTimeLeftInMillis / 1000) % 60;
    }

    public String getTime(long mTimeLeftInMillis) {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", getHour(mTimeLeftInMillis), getMin(mTimeLeftInMillis), getSec(mTimeLeftInMillis));
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("timeCnt", timeCnt);
        result.put("timeList", timeList);
        return result;
    }

    @Exclude
    public String toString() {
        HashMap<String, Object> map = new HashMap<>(toMap());
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }
}
