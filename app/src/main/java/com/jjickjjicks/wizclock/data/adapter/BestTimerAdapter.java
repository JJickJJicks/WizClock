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

public class BestTimerAdapter extends RecyclerView.Adapter<BestTimerAdapter.ViewHolder> {
    private ArrayList<TimerItem> list = null;
    private ArrayList<String> keyList = null;
    private Context context;

    public BestTimerAdapter(Context context) {
        this.list = new ArrayList<>();
        this.context = context;
    }

    public BestTimerAdapter(ArrayList<TimerItem> list, ArrayList<String> keyList, Context context) {
        this.list = list;
        this.keyList = keyList;
        this.context = context;
    }

    @NonNull
    @Override
    public BestTimerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.card_best_timers, parent, false); //TODO: layout 변    경
        BestTimerAdapter.ViewHolder vh = new BestTimerAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BestTimerAdapter.ViewHolder holder, int position) {
        holder.itemTitle.setText(list.get(position).getTitle());
        holder.itemDescription.setText(list.get(position).getDescribe());
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle, itemDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemDescription = itemView.findViewById(R.id.itemDescription);

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
