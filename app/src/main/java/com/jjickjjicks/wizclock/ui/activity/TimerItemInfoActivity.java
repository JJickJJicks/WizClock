package com.jjickjjicks.wizclock.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jjickjjicks.wizclock.AccessSettings;
import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.data.adapter.TimeInfoDataAdatper;
import com.jjickjjicks.wizclock.data.item.SingleTimeData;
import com.jjickjjicks.wizclock.data.item.TimerData;
import com.jjickjjicks.wizclock.data.item.TimerItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.fabric.sdk.android.Fabric;

public class TimerItemInfoActivity extends AppCompatActivity implements View.OnClickListener {
    final static private int TIMER_EDIT = 10;

    private TextView tvTimerItemName, tvTimerItemDescription, tvAuthorName, tvAuthorEmail, tvType, tvRecursive;
    private Button btnEdit, btnAddTimerItem;
    private RecyclerView rvTimerItem;
    private FirebaseUser user;
    private TimerItem timerItem;
    private String key;
    private int mode = 0;
    private ArrayList<SingleTimeData> singleTimeDataArrayList = new ArrayList<>();
    private TimeInfoDataAdatper adapter = new TimeInfoDataAdatper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_item_info);

        Fabric.with(this, new Crashlytics());

        setTitle("Timer Item Information");

        tvTimerItemName = findViewById(R.id.tvTimerItemName);
        tvTimerItemDescription = findViewById(R.id.tvTimerItemDescription);
        tvAuthorName = findViewById(R.id.tvAuthorName);
        tvAuthorEmail = findViewById(R.id.tvAuthorEmail);
        tvType = findViewById(R.id.tvType);
        tvRecursive = findViewById(R.id.tvRecursive);
        btnEdit = findViewById(R.id.btnEdit);
        btnAddTimerItem = findViewById(R.id.btnAddTimerItem);
        rvTimerItem = findViewById(R.id.rvTimerItem);

        btnEdit.setEnabled(false);

        btnAddTimerItem.setOnClickListener(this);
        btnEdit.setOnClickListener(this);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        mode = intent.getIntExtra("mode", 0);

        getUI(key);
    }

    private void checkOwn(final String key) {
        SharedPreferences preferences = getSharedPreferences("TimerItem", 0);
        HashMap<String, Object> data = new HashMap<>(preferences.getAll());

        if (((AccessSettings) this.getApplication()).getAccessMode() == AccessSettings.ONLINE_ACCESS && !data.containsKey(key))
            btnAddTimerItem.setEnabled(true);
        else
            btnAddTimerItem.setEnabled(false);
    }

    private void getUI(final String key) {
        checkOwn(key);
        if (mode == TimerItem.ONLINE)
            getOnlineUI(key);
        else
            getOfflineUI(key);
    }

    // 아이템 설정이 online임과 동시에 현재 상태가 online일 때 활성화 되는 UI
    private void getOnlineUI(final String key) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("Check", "Method Load Complete");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("timer");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(String.valueOf(key))) {
                        timerItem = new TimerItem(new HashMap<String, Object>((Map) snapshot.getValue()));

                        Log.d("Check", user.getEmail() + " / " + timerItem.getAuthorEmail());
                        if (user.getEmail().equals(timerItem.getAuthorEmail())) {
                            Log.d("Check", "Same User Email");
                            btnEdit.setEnabled(true);
                        }

                        TimerData timerData = timerItem.getTimerData();

                        tvTimerItemName.setText(timerItem.getTitle());
                        tvTimerItemDescription.setText(timerItem.getDescribe());
                        tvAuthorName.setText(timerItem.getAuthorName());
                        tvAuthorEmail.setText(timerItem.getAuthorEmail());
                        tvType.setText(String.valueOf(timerItem.getType()));
                        tvRecursive.setText(String.valueOf(timerData.getTimeCnt()));

                        ArrayList<Long> timeList = timerData.getTimeList();
                        for (long i : timeList)
                            singleTimeDataArrayList.add(new SingleTimeData(i));

                        rvTimerItem.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        adapter = new TimeInfoDataAdatper(singleTimeDataArrayList);
                        rvTimerItem.setAdapter(adapter);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 아이템 설정이 offline이거나 현재 설정이 offline일때 출력되는 ui
    private void getOfflineUI(final String key) {
        SharedPreferences preferences = getSharedPreferences("TimerItem", 0);

        String JsonLoad = preferences.getString(key, null);
        TimerItem timerItem = new TimerItem(JsonLoad);

        if (timerItem.getType() == TimerItem.OFFLINE) {
            Log.d("Check", "Owner");
            btnEdit.setEnabled(true);
        }

        TimerData timerData = timerItem.getTimerData();

        tvTimerItemName.setText(timerItem.getTitle());
        tvTimerItemDescription.setText(timerItem.getDescribe());
        tvAuthorName.setText(timerItem.getAuthorName());
        tvAuthorEmail.setText(timerItem.getAuthorEmail());
        tvType.setText(String.valueOf(timerItem.getType()));
        tvRecursive.setText(String.valueOf(timerData.getTimeCnt()));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnEdit) {
            Toast.makeText(this, "아직 지원되지 않는 기능입니다.", Toast.LENGTH_SHORT).show();

//            Intent intent = new Intent(this, TimerEditorActivity.class);
//            intent.putExtra("mode", TimerEditorActivity.MODE_EDIT);
//            intent.putExtra("key", key);
//            startActivityForResult(intent, TIMER_EDIT);

        } else if (v.getId() == R.id.btnAddTimerItem) {
            SharedPreferences preferences = getSharedPreferences("TimerItem", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, timerItem.toString());
            if (editor.commit()) {
                FirebaseMessaging.getInstance().subscribeToTopic(key)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    new SweetAlertDialog(TimerItemInfoActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("추가되었습니다!")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    Intent intent = new Intent();
                                                    setResult(RESULT_OK, intent);
                                                    sDialog.dismiss();
                                                    finish();
                                                }
                                            })
                                            .show();
                                } else {
                                    new SweetAlertDialog(TimerItemInfoActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("문제가 발생했어요!")
                                            .show();
                                }
                            }
                        });
            } else {
                new SweetAlertDialog(TimerItemInfoActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("문제가 발생했어요!")
                        .show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO : 업데이트 시 기존 해당 아이템 정보를 가지고 있던 사용자에게 변경되었음을 알림
//        if (requestCode == TIMER_EDIT && resultCode == RESULT_OK) {
//            getUI(key);
//        }
    }
}
