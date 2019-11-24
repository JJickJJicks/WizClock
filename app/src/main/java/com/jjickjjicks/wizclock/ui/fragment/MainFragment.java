package com.jjickjjicks.wizclock.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.data.item.Member;

public class MainFragment extends Fragment {
    private TextView tvUserName, tvUserLevel, tvUserExp;
    private ProgressBar pbUserExp;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        // Firebase 로드
        tvUserName = root.findViewById(R.id.tvUserName);
        tvUserLevel = root.findViewById(R.id.tvUserLevel);
        tvUserExp = root.findViewById(R.id.tvUserExp);
        pbUserExp = root.findViewById(R.id.pbUserExp);

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

        return root;
    }
}
