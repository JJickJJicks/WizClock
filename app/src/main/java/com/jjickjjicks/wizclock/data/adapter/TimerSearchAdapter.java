package com.jjickjjicks.wizclock.data.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.data.item.TimerItem;
import com.jjickjjicks.wizclock.ui.activity.TimerItemInfoActivity;

import java.util.ArrayList;

public class TimerSearchAdapter extends RecyclerView.Adapter<TimerSearchAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TimerItem> list = null;
    private ArrayList<String> keyList = null;

    public TimerSearchAdapter(ArrayList<TimerItem> list, ArrayList<String> keyList, Context context) {
        this.context = context;
        this.list = list;
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public TimerSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.card_search_item, parent, false);
        TimerSearchAdapter.ViewHolder vh = new TimerSearchAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemName.setText(list.get(position).getTitle());
        holder.itemDescription.setText(list.get(position).getDescribe());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.tvTimerTitle);
            itemDescription = itemView.findViewById(R.id.tvTimerDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, TimerItemInfoActivity.class);
                        intent.putExtra("mode", TimerItem.ONLINE);
                        intent.putExtra("key", keyList.get(position));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}