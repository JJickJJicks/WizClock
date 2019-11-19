package com.jjickjjicks.wizclock.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.travijuu.numberpicker.library.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;


public class TimerAddActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference databaseReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private long key = 0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_add);

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

        btnAddTimerData.setOnClickListener(this);
        btnRegistTimerItem.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAddTimerData) {
            MyTimePickerDialog mTimePicker = new MyTimePickerDialog(this, new MyTimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute, int seconds) {
                    SingleTimeData singleTimeData = new SingleTimeData(hourOfDay, minute, seconds);

                    singleTimeDataArrayList.add(singleTimeData);
                    timerData.addTime(singleTimeData.getMiliSecond());

                    recyclerTimerData.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    adapter = new SingleTimeDataAdapter(singleTimeDataArrayList);
                    recyclerTimerData.setAdapter(adapter);

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
                RegisterTimerData(title, description, type, timeCnt, timeList);
                Toast.makeText(this, "미구현", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "모든 정보를 빠짐없이 입력해주세요", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void RegisterTimerData(final String title, final String description, final int type, final int timeCnt, final ArrayList<Long> timeList) {
        databaseReference = FirebaseDatabase.getInstance().getReference("timer");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    key = dataSnapshot.getChildrenCount();
                    Log.d("JsonSaveCheck 2", String.valueOf(key));
                }
                TimerItem item = new TimerItem(key + 1, title, description, user.getEmail(), user.getDisplayName(), type, new TimerData(timeCnt, timeList));
                databaseReference.child(String.valueOf(key + 1)).setValue(item.toMap());

                SharedPreferences preferences = getSharedPreferences("TimerItem", 0);
                SharedPreferences.Editor editor = preferences.edit();

                ArrayList<String> keyList = new ArrayList<>();
                String Json = preferences.getString("key", null);
                if (Json != null) {
                    Log.d("JsonSaveCheck 3", Json);
                    try {
                        JSONArray jsonArray = new JSONArray(Json);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            keyList.add(jsonArray.optString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("JsonSaveCheck 2-1", keyList.toString());

                keyList.add(String.valueOf(key + 1));
                JSONArray jsonArray = new JSONArray();
                for (String i : keyList) {
                    jsonArray.put(i);
                }

                String keyJson = null;
                if (!keyList.isEmpty())
                    keyJson = jsonArray.toString();

                Log.d("JsonSaveCheck 1", keyJson);

                editor.putString("key", keyJson);
                editor.putString(String.valueOf(key + 1), item.toString());
                editor.apply();
                Log.d("JsonSaveCheck", item.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
