package com.jjickjjicks.wizclock.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.data.adapter.TimerItemAdapter;
import com.jjickjjicks.wizclock.data.item.TimerItem;
import com.jjickjjicks.wizclock.ui.activity.TimerAddActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class TimerFragment extends Fragment implements View.OnClickListener {
    final private int TIMER_CREATE = 1;
    private ArrayList<TimerItem> timerItmeList;
    private RecyclerView recyclerView;
    private TimerItemAdapter adapter;
    private ItemTouchHelper.SimpleCallback deleteMotion = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            final int position = viewHolder.getAdapterPosition();
            new AlertDialog.Builder(getContext())
                    .setTitle("삭제").setMessage("삭제하시겠습니까? 온라인 데이터는 삭제되지 않습니다.")
                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            TimerItem timerItem = timerItmeList.get(position);
                            timerItmeList.remove(position);

                            SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences("TimerItem", 0);
                            SharedPreferences.Editor editor = preferences.edit();

                            // 오프라인 저장소에서도 삭제
                            ArrayList<String> keyList = new ArrayList<>();
                            String Json = preferences.getString("key", null);
                            if (Json != null) {
                                try {
                                    JSONArray jsonArray = new JSONArray(Json);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        keyList.add(jsonArray.optString(i));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            keyList.remove(String.valueOf(timerItem.getKey()));
                            JSONArray jsonArray = new JSONArray();
                            for (String i : keyList) {
                                jsonArray.put(i);
                            }
                            String keyJson = "[]";
                            if (!keyList.isEmpty())
                                keyJson = jsonArray.toString();
                            editor.putString("key", keyJson);
                            editor.remove(String.valueOf(timerItem.getKey()));
                            editor.apply();

                            adapter.notifyItemRemoved(position);
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            updateUI();
                        }
                    })
                    .show();
        }
    };
    private TextView btnTimerAdd;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timer, container, false);


        recyclerView = root.findViewById(R.id.listViewTimeItem);
        btnTimerAdd = root.findViewById(R.id.btnTimerAdd);

        btnTimerAdd.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // setup swipe to remove item
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(deleteMotion);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        updateUI();

        return root;
    }

    private void updateUI() {
        timerItmeList = new ArrayList<>();
        timerItmeList.clear();

        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences("TimerItem", 0);

        ArrayList<String> keyList = new ArrayList<>();
        String Json = preferences.getString("key", null);
        if (Json != null) {
            Log.d("JsonCheck", Json);
            try {
                JSONArray jsonArray = new JSONArray(Json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    keyList.add(jsonArray.optString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (String key : keyList) {
            String JsonLoad = preferences.getString(key, null);
            timerItmeList.add(new TimerItem(JsonLoad));
        }
        adapter = new TimerItemAdapter(timerItmeList);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnTimerAdd) {
            Intent intent = new Intent(getContext(), TimerAddActivity.class);
            startActivityForResult(intent, TIMER_CREATE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TIMER_CREATE && resultCode == RESULT_OK) {

        }
    }
}