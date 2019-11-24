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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.data.item.TimerData;
import com.jjickjjicks.wizclock.data.item.TimerItem;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TimerItemInfoActivity extends AppCompatActivity implements View.OnClickListener {
    final static public int ONLINE_MODE = 0, OFFLINE_MODE = 1;
    final static private int TIMER_EDIT = 10;

    private TextView tvTimerItemName, tvTimerItemDescription, tvAuthorName, tvAuthorEmail, tvType, tvRecursive;
    private Button btnEdit, btnAddTimerItem;
    private RecyclerView rvTimerItem;
    private FirebaseUser user;
    private TimerItem timerItem;
    private String key;
    private int onlineCheck = 0, mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_item_info);
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
        onlineCheck = intent.getIntExtra("onlineCheck", 0);

        getUI(key);
    }

    private void checkOwn(final String key) {
        SharedPreferences preferences = getSharedPreferences("TimerItem", 0);
        HashMap<String, Object> data = new HashMap<>(preferences.getAll());
        if (data.containsKey(key))
            btnAddTimerItem.setEnabled(false);
        else
            btnAddTimerItem.setEnabled(true);
    }

    private void getUI(final String key) {
        checkOwn(key);
        if (mode == ONLINE_MODE)
            getOnlineUI(key);
        else
            getOfflineUI(key);
    }

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
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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
                new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
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
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
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
