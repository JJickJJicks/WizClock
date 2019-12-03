package com.jjickjjicks.wizclock.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.data.item.Member;

import java.util.ArrayList;

public class BestUserAdapter extends RecyclerView.Adapter<BestUserAdapter.ViewHolder> {
    private ArrayList<Member> list = null;
    private Context context;

    public BestUserAdapter(Context context) {
        this.list = new ArrayList<>();
        this.context = context;
    }

    public BestUserAdapter(ArrayList<Member> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public BestUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.card_best_users, parent, false); //TODO: layout 변경
        BestUserAdapter.ViewHolder vh = new BestUserAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BestUserAdapter.ViewHolder holder, int position) {
        holder.userName.setText(list.get(position).getName());
        String photo = list.get(position).getPhotoUrl();

        if (photo == null || photo.equals(""))
            Glide.with(context).load(R.drawable.blank_profile_image).into(holder.profileImage);
        else {
            Glide.with(context).load(photo).into(holder.profileImage);
        }
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView userName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
        }
    }
}
