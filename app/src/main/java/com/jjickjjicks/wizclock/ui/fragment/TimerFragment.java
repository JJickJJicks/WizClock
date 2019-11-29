package com.jjickjjicks.wizclock.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.jjickjjicks.wizclock.ui.activity.TimerEditorActivity;
import com.jjickjjicks.wizclock.ui.activity.TimerItemInfoActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;

public class TimerFragment extends Fragment implements View.OnClickListener {
    final private int TIMER_CREATE = 1;
    private ArrayList<TimerItem> timerItmeList;
    private ArrayList<String> keyList;
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
            new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("삭제하시겠습니까?")
                    .setContentText("오프라인만 삭제됩니다")
                    .setConfirmText("삭제")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            String key = keyList.get(position);
                            timerItmeList.remove(position);
                            keyList.remove(position);

                            SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences("TimerItem", 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.remove(key);
                            if (editor.commit()) {
                                adapter.notifyItemRemoved(position);
                                sDialog.setTitleText("삭제되었습니다!")
                                        .setConfirmText("확인")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            } else {
                                sDialog.setTitleText("문제가 발생했어요!")
                                        .setConfirmText("확인")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            }
                        }
                    })
                    .show();
        }
    };

    private ItemTouchHelper.SimpleCallback editMotion = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            final int position = viewHolder.getAdapterPosition();

            Intent intent = new Intent(getContext(), TimerItemInfoActivity.class);
            intent.putExtra("mode", timerItmeList.get(position).getType());
            intent.putExtra("key", keyList.get(position));
            startActivity(intent);
            updateUI();
        }
    };

    private TextView btnTimerAdd;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timer, container, false);


        recyclerView = root.findViewById(R.id.listViewTimeItem);
        btnTimerAdd = root.findViewById(R.id.btnTimerAdd);

        btnTimerAdd.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        new ItemTouchHelper(deleteMotion).attachToRecyclerView(recyclerView);
        new ItemTouchHelper(editMotion).attachToRecyclerView(recyclerView);

        updateUI();

        return root;
    }

    private void updateUI() {
        timerItmeList = new ArrayList<>();
        timerItmeList.clear();

        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences("TimerItem", 0);

        HashMap<String, Object> data = new HashMap<>(preferences.getAll());
        keyList = new ArrayList<>(data.keySet());

        for (String key : keyList) {
            String JsonLoad = preferences.getString(key, null);
            timerItmeList.add(new TimerItem(JsonLoad));
        }
        adapter = new TimerItemAdapter(timerItmeList, getContext());
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnTimerAdd) {
            Intent intent = new Intent(getContext(), TimerEditorActivity.class);
            startActivityForResult(intent, TIMER_CREATE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TIMER_CREATE && resultCode == RESULT_OK) {
            updateUI();
        }
    }
}