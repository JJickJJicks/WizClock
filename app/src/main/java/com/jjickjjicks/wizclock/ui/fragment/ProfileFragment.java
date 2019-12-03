package com.jjickjjicks.wizclock.ui.fragment;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.data.item.Member;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ProfileFragment extends Fragment {
    private ImageView imageViewHeaderProfile;
    private TextView textViewUserName, textViewUserPhoneNumber, textViewUserEmail, textViewHeaderUserName, textViewHeaderUserEmail, textViewHeaderUserExp, textViewHeaderUserLevel;
    private ProgressBar progressBarHeaderUserExp;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private SweetAlertDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        textViewUserName = root.findViewById(R.id.textViewUserName);
        textViewUserPhoneNumber = root.findViewById(R.id.textViewUserPhoneNumber);
        textViewUserEmail = root.findViewById(R.id.textViewUserEmail);
        textViewHeaderUserName = root.findViewById(R.id.textViewHeaderUserName);
        textViewHeaderUserEmail = root.findViewById(R.id.textViewHeaderUserEmail);
        imageViewHeaderProfile = root.findViewById(R.id.imageViewHeaderProfile);
        textViewHeaderUserExp = root.findViewById(R.id.textViewHeaderUserExp);
        textViewHeaderUserLevel = root.findViewById(R.id.textViewHeaderUserLevel);
        progressBarHeaderUserExp = root.findViewById(R.id.progressBarHeaderUserExp);

        StartProgress();
        final String userKey = user.getEmail().replace(".", "_");
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Member member = dataSnapshot.getValue(Member.class);

                textViewHeaderUserName.setText(user.getDisplayName());
                textViewHeaderUserEmail.setText(user.getEmail());

                textViewHeaderUserLevel.setText("Lv." + member.getLevel());
                textViewHeaderUserExp.setText(member.getExperience() + "/10");
                progressBarHeaderUserExp.setProgress(member.getExperience() * 10);

                Uri photo = user.getPhotoUrl();

                if (photo == null)
                    Glide.with(getContext()).load(R.drawable.blank_profile_image).apply(RequestOptions.circleCropTransform()).into(imageViewHeaderProfile);
                else {
                    if (!photo.toString().equals(member.getPhotoUrl())) {
                        member.setPhotoUrl(photo.toString());
                        databaseReference.child(userKey).setValue(member.toMap());
                    }
                    Glide.with(getContext()).load(photo).apply(RequestOptions.circleCropTransform()).into(imageViewHeaderProfile);
                }

                textViewUserName.setText(member.getName());
                Log.d("test", member.getPhoneNumber());
                textViewUserPhoneNumber.setText(!(member.getPhoneNumber() == null) && (!(member.getPhoneNumber().equals(""))) ? String.valueOf(user.getPhoneNumber()) : "등록되지 않았습니다.");
                textViewUserEmail.setText(member.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        StopProgress();

        return root;
    }

    private void StartProgress() {
        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void StopProgress() {
        pDialog.dismiss();
    }
}
