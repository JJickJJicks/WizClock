package com.jjickjjicks.wizclock.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjickjjicks.wizclock.R;
import com.jjickjjicks.wizclock.data.adapter.TimerSearchAdapter;
import com.jjickjjicks.wizclock.data.item.TimerItem;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class SearchFragment extends Fragment {
    private SearchBox searchBar;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private ArrayList<TimerItem> timerItemList = new ArrayList<>();
    private ArrayList<String> keyList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TimerSearchAdapter adapter;
    private SweetAlertDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = root.findViewById(R.id.listViewTimeItem);
        searchBar = root.findViewById(R.id.searchBar);
        searchBar.setLogoText("Search");
        searchBar.setMenuListener(new SearchBox.MenuListener() {
            @Override
            public void onMenuClick() {
                Toast.makeText(getContext(), "분류는 아직 지원되지 않습니다.", Toast.LENGTH_LONG).show();
            }
        });

        StartProgress();
        updateUI();
        StopProgress();

        searchBar.setSearchListener(new SearchBox.SearchListener() {
            @Override
            public void onSearchOpened() {

            }

            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchClosed() {

            }

            @Override
            public void onSearchTermChanged(String s) {
                if (s.equals(""))
                    updateUI();
                else
                    updateUI(s);
            }

            @Override
            public void onSearch(String s) {
                if (s.equals(""))
                    updateUI();
                else
                    updateUI(s);
            }

            @Override
            public void onResultClick(SearchResult searchResult) {

            }
        });

        return root;
    }

    private void updateUI() {
        timerItemList.clear();
        keyList.clear();
        databaseReference = database.getReference("timer");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HashMap<String, Object> map = new HashMap<>((Map) snapshot.getValue());
                        timerItemList.add(new TimerItem(map));
                        keyList.add(snapshot.getKey());
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new TimerSearchAdapter(timerItemList, keyList, getContext());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUI(String search) {
        timerItemList.clear();
        keyList.clear();
        databaseReference = database.getReference("timer");
        Query timerSearch = databaseReference.orderByChild("title").startAt(search).endAt(search + "\uf8ff");
        ValueEventListener searchListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HashMap<String, Object> map = new HashMap<>((Map) snapshot.getValue());
                        timerItemList.add(new TimerItem(map));
                        keyList.add(snapshot.getKey());
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new TimerSearchAdapter(timerItemList, keyList, getContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        timerSearch.addListenerForSingleValueEvent(searchListener);
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
