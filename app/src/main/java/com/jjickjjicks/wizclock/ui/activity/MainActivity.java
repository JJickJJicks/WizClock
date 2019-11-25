package com.jjickjjicks.wizclock.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.jjickjjicks.wizclock.AccessSettings;
import com.jjickjjicks.wizclock.BottomNavigationBehavior;
import com.jjickjjicks.wizclock.DarkModePrefManager;
import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.ui.fragment.MainFragment;
import com.jjickjjicks.wizclock.ui.fragment.ProfileFragment;
import com.jjickjjicks.wizclock.ui.fragment.SearchFragment;
import com.jjickjjicks.wizclock.ui.fragment.SettingsFragment;
import com.jjickjjicks.wizclock.ui.fragment.TimerFragment;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private FragmentTransaction ft;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;

            switch (item.getItemId()) {
                case R.id.navigationTimer:
                    fragment = new TimerFragment();
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_fragment_layout, fragment);
                    ft.commit();
                    return true;
                case R.id.navigationSearch:
                    fragment = new SearchFragment();
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_fragment_layout, fragment);
                    ft.commit();
                    return true;
                case R.id.navigationHome:
                    fragment = new MainFragment();
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_fragment_layout, fragment);
                    ft.commit();
                    return true;
                case R.id.navigationMyProfile:
                    fragment = new ProfileFragment();
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_fragment_layout, fragment);
                    ft.commit();
                    return true;
                case R.id.navigationSetting:
                    fragment = new SettingsFragment();
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_fragment_layout, fragment);
                    ft.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fabric.with(this, new Crashlytics());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        Fragment fragment;

        //온라인일 경우 전체 사용 가능
        if (((AccessSettings) this.getApplication()).getAccessMode() == AccessSettings.ONLINE_ACCESS) {
            fragment = new MainFragment();
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_fragment_layout, fragment);
            ft.commit();

            bottomNavigationView.setSelectedItemId(R.id.navigationHome);
        } else { // 오프라인일 경우 timer fragment만 사용 가능
            fragment = new TimerFragment();
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_fragment_layout, fragment);
            ft.commit();

            bottomNavigationView.setSelectedItemId(R.id.navigationTimer);
            bottomNavigationView.setEnabled(false);
            bottomNavigationView.setFocusable(false);
            bottomNavigationView.setClickable(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bottomNavigationView.setContextClickable(false);
            }
            bottomNavigationView.setOnClickListener(null);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    // 추후 search menu로 활용할 예정
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_dark_mode) {
            //code for setting dark mode
            //true for dark mode, false for day mode, currently toggling on each click
            DarkModePrefManager darkModePrefManager = new DarkModePrefManager(this);
            darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            recreate();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
