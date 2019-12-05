package com.jjickjjicks.wizclock.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.data.adapter.BestTimerAdapter;
import com.jjickjjicks.wizclock.data.adapter.BestUserAdapter;
import com.jjickjjicks.wizclock.data.item.Member;
import com.jjickjjicks.wizclock.data.item.TimerItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainFragment extends Fragment {
    private TextView tvUserName, tvUserLevel, tvUserExp;
    private ProgressBar pbUserExp;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ArrayList<Member> memberList = new ArrayList<>();
    private ArrayList<TimerItem> timerList = new ArrayList<>();
    private ArrayList<String> keyList = new ArrayList<>();
    private RecyclerView bestUserList, bestTimerList;
    private BestUserAdapter userAdapter;
    private BestTimerAdapter timerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        // Firebase 로드
        tvUserName = root.findViewById(R.id.tvUserName);
        tvUserLevel = root.findViewById(R.id.tvUserLevel);
        tvUserExp = root.findViewById(R.id.tvUserExp);
        pbUserExp = root.findViewById(R.id.pbUserExp);

        bestUserList = root.findViewById(R.id.bestUserList);
        bestTimerList = root.findViewById(R.id.bestTimerList);
        bestUserList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bestTimerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        tvUserName.setText("반갑습니다! " + user.getDisplayName());

        final String userKey = user.getEmail().replace(".", "_");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(userKey)) {
                        Member member = snapshot.getValue(Member.class);
                        tvUserLevel.setText("Lv." + member.getLevel());
                        tvUserExp.setText(member.getExperience() + "/10");
                        pbUserExp.setProgress(member.getExperience() * 10);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getTimerRank();
        getMemberRank();

        return root;
    }

    private void getTimerRank() {
        timerList.clear();
        keyList.clear();

        Log.d("test", "start");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("timer");
        Query userSearch = databaseReference.orderByChild("cnt").limitToLast(5);
        ValueEventListener searchListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, Object> map = new HashMap<>((Map) snapshot.getValue());
                    TimerItem timerItem = new TimerItem(map);
                    Log.d("test", timerItem.getTitle());
                    timerList.add(timerItem);
                    keyList.add(snapshot.getKey());
                }
                ArrayList<TimerItem> timerReverseList = new ArrayList<>();
                for (int i = timerList.size() - 1; i >= 0; i--)
                    timerReverseList.add(timerList.get(i));

                ArrayList<String> keyReverseList = new ArrayList<>();
                for (int i = keyList.size() - 1; i >= 0; i--)
                    keyReverseList.add(keyList.get(i));

                timerAdapter = new BestTimerAdapter(timerReverseList, keyReverseList, getContext());
                bestTimerList.setAdapter(timerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        userSearch.addListenerForSingleValueEvent(searchListener);
        Log.d("test", "end");
    }

    private void getMemberRank() {
        memberList.clear();

        Log.d("test", "start");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query userSearch = databaseReference.orderByChild("experience").limitToLast(5);
        ValueEventListener searchListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Member member = snapshot.getValue(Member.class);
                    Log.d("test", member.getName());
                    memberList.add(member);
                }
                ArrayList<Member> memberReverseList = new ArrayList<>();
                for (int i = memberList.size() - 1; i >= 0; i--)
                    memberReverseList.add(memberList.get(i));

                userAdapter = new BestUserAdapter(memberReverseList, getContext());
                bestUserList.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        userSearch.addListenerForSingleValueEvent(searchListener);
        Log.d("test", "end");
    }
}
