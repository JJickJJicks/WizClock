package com.jjickjjicks.wizclock;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TimerAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<TimerData> listData = new ArrayList<>();

    public TimerAdapter() {
    }

    @Override
    public int getCount() {
        Log.i("TAG", "getCount");
        return listData.size();
    }

    @Override
    public TimerData getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final Context context = parent.getContext();
            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.listview_timer, parent, false);
        }

        TextView oTextCnt = (TextView) convertView.findViewById(R.id.timerCnt);
        TextView oTextTime = (TextView) convertView.findViewById(R.id.timerTime);

        TimerData listViewItem = listData.get(position);

        oTextCnt.setText(String.valueOf(listViewItem.getTimeCnt()));
        oTextTime.setText(listViewItem.getTime());

        return convertView;
    }

    public void addTimer(int cnt, long time) {
        TimerData item = new TimerData();

        item.setTimeCnt(cnt);
        item.setmTimeLeftInMillis(time);

        listData.add(item);
    }

    public void clear(){
        listData.clear();
    }
}
