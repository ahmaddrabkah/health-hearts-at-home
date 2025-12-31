package com.healthhearts.app.ui.track;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.healthhearts.app.R;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivityTrackYourChildBinding;
import com.healthhearts.app.model.BloodPressureEntry;
import com.healthhearts.app.model.FeedingEntry;
import com.healthhearts.app.model.OxygenEntry;
import com.healthhearts.app.model.WeightEntry;
import com.healthhearts.app.ui.common.BaseMenuActivity;
import com.healthhearts.app.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

public class TrackYourChildActivity extends BaseMenuActivity {

    private ActivityTrackYourChildBinding trackYourChildBinding;
    private TrackListAdapter adapter;

    private int tab = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trackYourChildBinding = ActivityTrackYourChildBinding.inflate(getLayoutInflater());
        setContentView(trackYourChildBinding.getRoot());

        setSupportActionBar(trackYourChildBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            trackYourChildBinding.toolbar.setTitle(getString(R.string.track_your_child));
        }

        adapter = new TrackListAdapter();
        trackYourChildBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        trackYourChildBinding.recycler.setAdapter(adapter);

        trackYourChildBinding.tabs.addTab(trackYourChildBinding.tabs.newTab().setText(getString(R.string.feeding)));
        trackYourChildBinding.tabs.addTab(trackYourChildBinding.tabs.newTab().setText(getString(R.string.oxygen)));
        trackYourChildBinding.tabs.addTab(trackYourChildBinding.tabs.newTab().setText(getString(R.string.weight)));
        trackYourChildBinding.tabs.addTab(trackYourChildBinding.tabs.newTab().setText(getString(R.string.blood_pressure)));

        trackYourChildBinding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab t) {
                tab = t.getPosition();
                load();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab t) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab t) {
            }
        });

        trackYourChildBinding.fabAdd.setOnClickListener(v -> openAdd());
    }

    @Override
    protected void onStart() {
        super.onStart();
        load();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAdd() {
        Intent i;
        if (tab == 0) i = new Intent(this, AddFeedingActivity.class);
        else if (tab == 1) i = new Intent(this, AddOxygenActivity.class);
        else if (tab == 2) i = new Intent(this, AddWeightActivity.class);
        else i = new Intent(this, AddBloodPressureActivity.class);
        startActivity(i);
    }

    private void load() {
        String uid = FirestoreRepo.uid();
        if (uid == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        trackYourChildBinding.tvEmpty.setVisibility(android.view.View.GONE);

        if (tab == 0) {
            FirestoreRepo.feedings()
                    .whereEqualTo("uid", uid)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .addOnSuccessListener(snap -> {
                        List<TrackListAdapter.Row> rows = new ArrayList<>();
                        for (DocumentSnapshot d : snap.getDocuments()) {
                            FeedingEntry e = d.toObject(FeedingEntry.class);
                            if (e == null) continue;
                            String top = DateTimeUtil.fmtDateTime(e.timestamp);
                            String bottom = e.amountMl + " ml • " + (e.method == null ? "" : e.method);
                            rows.add(new TrackListAdapter.Row(top, bottom));
                        }
                        adapter.setRows(rows);
                        trackYourChildBinding.tvEmpty.setVisibility(rows.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                        trackYourChildBinding.tvEmpty.setVisibility(android.view.View.VISIBLE);
                    });
        } else if (tab == 1) {
            FirestoreRepo.oxygen()
                    .whereEqualTo("uid", uid)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .addOnSuccessListener(snap -> {
                        List<TrackListAdapter.Row> rows = new ArrayList<>();
                        for (DocumentSnapshot d : snap.getDocuments()) {
                            OxygenEntry e = d.toObject(OxygenEntry.class);
                            if (e == null) continue;
                            String top = DateTimeUtil.fmtDateTime(e.timestamp);
                            String bottom = "SpO₂: " + e.spo2 + "%";
                            rows.add(new TrackListAdapter.Row(top, bottom));
                        }
                        adapter.setRows(rows);
                        trackYourChildBinding.tvEmpty.setVisibility(rows.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                        trackYourChildBinding.tvEmpty.setVisibility(android.view.View.VISIBLE);
                    });
        } else if (tab == 2) {
            FirestoreRepo.weights()
                    .whereEqualTo("uid", uid)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .addOnSuccessListener(snap -> {
                        List<TrackListAdapter.Row> rows = new ArrayList<>();
                        for (DocumentSnapshot d : snap.getDocuments()) {
                            WeightEntry e = d.toObject(WeightEntry.class);
                            if (e == null) continue;
                            String top = DateTimeUtil.fmtDateTime(e.timestamp);
                            String bottom = e.weightKg + " kg";
                            rows.add(new TrackListAdapter.Row(top, bottom));
                        }
                        adapter.setRows(rows);
                        trackYourChildBinding.tvEmpty.setVisibility(rows.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                        trackYourChildBinding.tvEmpty.setVisibility(android.view.View.VISIBLE);
                    });
        } else {
            FirestoreRepo.bloodPressure()
                    .whereEqualTo("uid", uid)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .addOnSuccessListener(snap -> {
                        List<TrackListAdapter.Row> rows = new ArrayList<>();
                        for (DocumentSnapshot d : snap.getDocuments()) {
                            BloodPressureEntry e = d.toObject(BloodPressureEntry.class);
                            if (e == null) continue;
                            String top = DateTimeUtil.fmtDateTime(e.timestamp);
                            String bottom = (e.value == null ? "" : e.value);
                            rows.add(new TrackListAdapter.Row(top, bottom));
                        }
                        adapter.setRows(rows);
                        trackYourChildBinding.tvEmpty.setVisibility(rows.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                        trackYourChildBinding.tvEmpty.setVisibility(android.view.View.VISIBLE);
                    });
        }
    }
}
