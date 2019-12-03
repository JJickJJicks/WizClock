package com.jjickjjicks.wizclock.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.ui.activity.LoginActivity;

import java.util.List;


public class SettingsFragment extends Fragment {
    private FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView usernameTextView, useremailTextView, btnLogout;
    private ImageView profileCircleImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        usernameTextView = root.findViewById(R.id.usernameTextView);
        useremailTextView = root.findViewById(R.id.useremailTextView);
        profileCircleImageView = root.findViewById(R.id.profileCircleImageView);
        btnLogout = root.findViewById(R.id.btnLogout);

        // Firebase 로드
        user = FirebaseAuth.getInstance().getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        usernameTextView.setText(user.getDisplayName());
        useremailTextView.setText(user.getEmail());
        if (user.getPhotoUrl() == null)
            Glide.with(this).load(R.drawable.blank_profile_image).apply(RequestOptions.circleCropTransform()).into(profileCircleImageView);
        else
            Glide.with(this).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(profileCircleImageView);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revokeAccess();
            }
        });

        return root;
    }

    private void revokeAccess() {
        List<? extends UserInfo> data = user.getProviderData();
        String providerID = data.get(data.size() - 1).getProviderId();
        Log.d("ProviderID", providerID);

        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        if (providerID.equals("google.com")) {// Google revoke access
            mGoogleSignInClient.revokeAccess().addOnCompleteListener(getActivity(),
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // 초기 로그인 화면으로 돌아감
                                Intent i = new Intent(getContext(), LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            } else {
                                Toast.makeText(getContext(), "로그아웃에 실패했습니다!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Intent i = new Intent(getContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

    }
}
