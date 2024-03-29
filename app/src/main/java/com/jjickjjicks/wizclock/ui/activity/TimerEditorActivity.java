package com.jjickjjicks.wizclock.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;
import com.ikovac.timepickerwithseconds.TimePicker;
import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.data.adapter.SingleTimeDataAdapter;
import com.jjickjjicks.wizclock.data.item.SingleTimeData;
import com.jjickjjicks.wizclock.data.item.TimerData;
import com.jjickjjicks.wizclock.data.item.TimerItem;
import com.jjickjjicks.wizclock.service.AccessSettings;
import com.travijuu.numberpicker.library.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.fabric.sdk.android.Fabric;


public class TimerEditorActivity extends AppCompatActivity implements View.OnClickListener {
    final public static int MODE_ADD = 0, MODE_EDIT = 1;

    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private EditText etTimerItemTitle, etTimerItemDescription;
    private TextView tvAddItemWarning;
    private Spinner spTimerItemType;
    private RecyclerView recyclerTimerData;
    private Button btnAddTimerData;
    private NumberPicker npTimerDataCount;
    private CircularProgressButton btnRegistTimerItem;
    private TimerData timerData = new TimerData();
    private ArrayList<SingleTimeData> singleTimeDataArrayList = new ArrayList<>();
    private SingleTimeDataAdapter adapter = new SingleTimeDataAdapter();
    TimerItem timerItem = new TimerItem();
    private int mode = MODE_ADD;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_editor);

        Fabric.with(this, new Crashlytics());

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", MODE_ADD);

        tvAddItemWarning = findViewById(R.id.tvAddItemWarning);
        if (singleTimeDataArrayList.size() != 0)
            tvAddItemWarning.setVisibility(View.INVISIBLE);
        else
            tvAddItemWarning.setVisibility(View.VISIBLE);

        etTimerItemTitle = findViewById(R.id.etTimerItemTitle);
        etTimerItemDescription = findViewById(R.id.etTimerItemDescription);
        spTimerItemType = findViewById(R.id.spTimerItemType);
        recyclerTimerData = findViewById(R.id.recyclerTimerData);
        btnAddTimerData = findViewById(R.id.btnAddTimerData);
        npTimerDataCount = findViewById(R.id.npTimerDataCount);
        btnRegistTimerItem = findViewById(R.id.btnRegistTimerItem);

        setUI();

        btnAddTimerData.setOnClickListener(this);
        btnRegistTimerItem.setOnClickListener(this);
    }

    public void setUI() {
        switch (mode) {
            case MODE_EDIT:
                singleTimeDataArrayList.clear();

                key = getIntent().getStringExtra("key");
                timerItem = new TimerItem(getIntent().getStringExtra("item"));
                TimerData timerData = timerItem.getTimerData();
                etTimerItemTitle.setText(timerItem.getTitle());
                etTimerItemDescription.setText(timerItem.getDescribe());
                spTimerItemType.setSelection(timerItem.getType());

                npTimerDataCount.setValue(timerData.getTimeCnt());
                ArrayList<Long> timeList = timerData.getTimeList();
                for (long i : timeList)
                    singleTimeDataArrayList.add(new SingleTimeData(i));

                recyclerTimerData.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new SingleTimeDataAdapter(singleTimeDataArrayList);
                recyclerTimerData.setAdapter(adapter);

                btnRegistTimerItem.setText("수정하기");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAddTimerData) {
            MyTimePickerDialog mTimePicker = new MyTimePickerDialog(this, new MyTimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute, int seconds) {
                    SingleTimeData singleTimeData = new SingleTimeData(hourOfDay, minute, seconds);

                    if (singleTimeData.getMiliSecond() == 0) {
                        new SweetAlertDialog(TimerEditorActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("시간을 선택해주세요!")
                                .show();
                    } else {
                        singleTimeDataArrayList.add(singleTimeData);
                        timerData.addTime(singleTimeData.getMiliSecond());

                        recyclerTimerData.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        adapter = new SingleTimeDataAdapter(singleTimeDataArrayList);
                        recyclerTimerData.setAdapter(adapter);
                    }
                    if (singleTimeDataArrayList.size() != 0)
                        tvAddItemWarning.setVisibility(View.INVISIBLE);
                    else
                        tvAddItemWarning.setVisibility(View.VISIBLE);
                }
            }, 0, 0, 0, true);
            mTimePicker.show();
        } else if (view.getId() == R.id.btnRegistTimerItem) {
            String title = etTimerItemTitle.getText().toString();
            String description = etTimerItemDescription.getText().toString();
            if (!title.equals("") && !description.equals("") && adapter.getItemCount() != 0) {
                int type = spTimerItemType.getSelectedItemPosition();
                int timeCnt = npTimerDataCount.getValue();
                ArrayList<Long> timeList = adapter.toArrayList();

                TimerData timerData = new TimerData(timeCnt, timeList);
                timerItem.setTitle(title);
                timerItem.setDescribe(description);
                timerItem.setTimerData(timerData);
                timerItem.setType(type);

                if (mode == MODE_EDIT) {
                    if (timerItem.getOnlineCheck() == TimerItem.ONLINE)
                        RefreshTimerDataOnline(key);
                    else
                        RefreshTimerDataOffline(key);
                } else
                    RegisterTimerDataOffline(title, description, type, timeCnt, timeList);
            } else {
                new SweetAlertDialog(TimerEditorActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("무언가 빠졌어요!")
                        .show();
            }
        }
    }


    private void RegisterTimerDataOffline(final String title, final String description, final int type, final int timeCnt, final ArrayList<Long> timeList) {
        final SharedPreferences preferences = getSharedPreferences("TimerItem", 0);
        SharedPreferences.Editor editor = preferences.edit();

        TimerItem item = new TimerItem(TimerItem.OFFLINE, title, description, "Offline Info", "Offline Info", type, new TimerData(timeCnt, timeList));

        final String key = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREAN).format(Calendar.getInstance().getTime());
        editor.putString(key, item.toString());
        if (editor.commit()) {
            if (((AccessSettings) this.getApplication()).getAccessMode() == AccessSettings.ONLINE_ACCESS) {
                new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("추가되었습니다!")
                        .setContentText("다른 사람들과 공유하시겠어요?")
                        .setConfirmText("네!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                SharedPreferences.Editor remover = getSharedPreferences("TimerItem", 0).edit();
                                remover.remove(key);
                                if (remover.commit()) {
                                    RegisterTimerDataOnline(title, description, type, timeCnt, timeList);
                                    sDialog.dismiss();
                                }
                            }
                        })
                        .setCancelText("아니요")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
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
                new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("추가되었습니다!")
                        .setConfirmText("네!")
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
            }
        } else {
            new SweetAlertDialog(TimerEditorActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("문제가 발생했어요!")
                    .show();
        }
    }

    private void RegisterTimerDataOnline(final String title, final String description, final int type, final int timeCnt, final ArrayList<Long> timeList) {
        databaseReference = FirebaseDatabase.getInstance().getReference("timer");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final TimerItem item = new TimerItem(TimerItem.ONLINE, title, description, user.getDisplayName(), user.getEmail(), type, new TimerData(timeCnt, timeList));
                databaseReference.push().setValue(item.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            final String key = databaseReference.getKey();
                            SharedPreferences preferences = getSharedPreferences("TimerItem", 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(key, item.toString());
                            if (editor.commit()) {
                                new SweetAlertDialog(TimerEditorActivity.this, SweetAlertDialog.SUCCESS_TYPE)
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
                                new SweetAlertDialog(TimerEditorActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("문제가 발생했어요!")
                                        .show();
                            }
                        } else {
                            new SweetAlertDialog(TimerEditorActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("문제가 발생했어요!")
                                    .show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void RefreshTimerDataOffline(final String key) {
        SharedPreferences.Editor remover = getSharedPreferences("TimerItem", 0).edit();
        remover.remove(key);
        if (remover.commit()) {
            SharedPreferences.Editor editor = getSharedPreferences("TimerItem", 0).edit();
            editor.putString(key, timerItem.toString());
            if (editor.commit()) {
                new SweetAlertDialog(TimerEditorActivity.this, SweetAlertDialog.SUCCESS_TYPE)
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
                new SweetAlertDialog(TimerEditorActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("문제가 발생했어요!")
                        .show();
            }
        } else {
            new SweetAlertDialog(TimerEditorActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("문제가 발생했어요!")
                    .show();
        }
    }

    private void RefreshTimerDataOnline(final String key) {
        databaseReference = FirebaseDatabase.getInstance().getReference("timer");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseReference.child(key).setValue(timerItem.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            SharedPreferences preferences = getSharedPreferences("TimerItem", 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(key, timerItem.toString());
                            if (editor.commit()) {
                                new SweetAlertDialog(TimerEditorActivity.this, SweetAlertDialog.SUCCESS_TYPE)
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
                                new SweetAlertDialog(TimerEditorActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("문제가 발생했어요!")
                                        .show();
                            }
                        } else {
                            new SweetAlertDialog(TimerEditorActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("문제가 발생했어요!")
                                    .show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
