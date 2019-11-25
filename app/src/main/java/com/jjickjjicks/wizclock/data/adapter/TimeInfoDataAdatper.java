package com.jjickjjicks.wizclock.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.data.item.SingleTimeData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TimeInfoDataAdatper extends RecyclerView.Adapter<TimeInfoDataAdatper.ViewHolder> {
    private ArrayList<SingleTimeData> list = null;

    public TimeInfoDataAdatper() {
        this.list = new ArrayList<>();
    }

    public TimeInfoDataAdatper(ArrayList<SingleTimeData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public TimeInfoDataAdatper.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.card_info_time, parent, false); //TODO: layout 변경
        TimeInfoDataAdatper.ViewHolder vh = new TimeInfoDataAdatper.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeInfoDataAdatper.ViewHolder holder, int position) {
        holder.tvSingleTime.setText(String.format("%02d", list.get(position).getHour()) + " : " + String.format("%02d", list.get(position).getMinute()) + " : " + String.format("%02d", list.get(position).getSecond()));
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    public ArrayList<Long> toArrayList() {
        ArrayList<Long> result = new ArrayList<>();
        for (SingleTimeData i : list)
            result.add(i.getMiliSecond());
        return result;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSingleTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSingleTime = itemView.findViewById(R.id.tvSingleTime);
        }
    }
}
