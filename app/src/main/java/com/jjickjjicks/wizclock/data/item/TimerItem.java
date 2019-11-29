package com.jjickjjicks.wizclock.data.item;

import com.google.firebase.database.Exclude;
import com.jjickjjicks.wizclock.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TimerItem {
    public final static int ONLINE = 0, OFFLINE = 1;
    final private static int TYPE_STUDY = 0, TYPE_HEALTH = 1, TYPE_COOK = 2, TYPE_ETC = 3;

    private String title, describe, authorName, authorEmail, regDate;
    private int onlineCheck, type;
    private TimerData timerData;

    public TimerItem() {
    }

    public TimerItem(String Json) {
        try {
            JSONObject jsonObject = new JSONObject(Json);
            this.onlineCheck = jsonObject.getInt("onlineCheck");
            this.title = jsonObject.getString("title");
            this.describe = jsonObject.getString("describe");
            this.authorName = jsonObject.getString("authorName");
            this.authorEmail = jsonObject.getString("authorEmail");
            this.type = jsonObject.getInt("type");
            this.regDate = jsonObject.getString("regDate");
            this.timerData = new TimerData(jsonObject.getString("timerData"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public TimerItem(HashMap<String, Object> map) {
        this.onlineCheck = Integer.valueOf(map.get("onlineCheck").toString());
        this.title = map.get("title").toString();
        this.describe = map.get("describe").toString();
        this.authorName = map.get("authorName").toString();
        this.authorEmail = map.get("authorEmail").toString();
        this.type = Integer.valueOf(map.get("type").toString());
        this.regDate = map.get("regDate").toString();
        this.timerData = new TimerData((String) map.get("timerData"));
    }

    public TimerItem(int onlineCheck, String title, String describe, String authorName, String authorEmail, int type, TimerData timerData) {
        this.onlineCheck = onlineCheck;
        this.title = title;
        this.describe = describe;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.type = type;
        this.regDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREAN).format(Calendar.getInstance().getTime());
        this.timerData = timerData;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate() {
        this.regDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREAN).format(Calendar.getInstance().getTime());
    }

    public int getOnlineCheck() {
        return onlineCheck;
    }

    public String getTitle() {
        return title;
    }

    public String getDescribe() {
        return describe;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public int getType() {
        return type;
    }

    public TimerData getTimerData() {
        return timerData;
    }

    public int getTypeIcon() {
        switch (this.type) {
            case TYPE_STUDY:
                return R.drawable.ic_study;
            case TYPE_HEALTH:
                return R.drawable.ic_health;
            case TYPE_COOK:
                return R.drawable.ic_cook;
            default:
                return R.drawable.ic_etc;
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("onlineCheck", onlineCheck);
        result.put("title", title);
        result.put("describe", describe);
        result.put("authorName", authorName);
        result.put("authorEmail", authorEmail);
        result.put("type", type);
        result.put("regDate", regDate);
        result.put("timerData", timerData.toString());
        return result;
    }

    @Exclude
    public String toString() {
        HashMap<String, Object> map = new HashMap<>(toMap());
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }
}
