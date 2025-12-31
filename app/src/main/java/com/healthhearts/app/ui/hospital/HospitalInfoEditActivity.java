package com.healthhearts.app.ui.hospital;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.SetOptions;
import com.healthhearts.app.R;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivityHospitalInfoEditBinding;
import com.healthhearts.app.ui.common.BaseMenuActivity;

import java.util.HashMap;
import java.util.Map;

public class HospitalInfoEditActivity extends BaseMenuActivity {

    private ActivityHospitalInfoEditBinding hospitalInfoEditBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hospitalInfoEditBinding = ActivityHospitalInfoEditBinding.inflate(getLayoutInflater());
        setContentView(hospitalInfoEditBinding.getRoot());

        setSupportActionBar(hospitalInfoEditBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        hospitalInfoEditBinding.toolbar.setNavigationOnClickListener(v -> finish());
        hospitalInfoEditBinding.toolbar.setTitle(getString(R.string.hospital_information));

        load();

        hospitalInfoEditBinding.btnSave.setOnClickListener(v -> save());
    }

    private void load() {
        FirestoreRepo.hospitalInfo().get().addOnSuccessListener(doc -> {
            if (!doc.exists()) return;
            hospitalInfoEditBinding.etWebsite.setText(doc.getString("websiteUrl"));
            hospitalInfoEditBinding.etWorkingHoursEn.setText(doc.getString("workingHoursEn"));
            hospitalInfoEditBinding.etWorkingHoursAr.setText(doc.getString("workingHoursAr"));
            hospitalInfoEditBinding.etMapQuery.setText(doc.getString("mapQuery"));
            hospitalInfoEditBinding.etShowersUrl.setText(doc.getString("showersUrl"));
        });
    }

    private void save() {
        hospitalInfoEditBinding.btnSave.setEnabled(false);

        Map<String, Object> m = new HashMap<>();
        m.put("websiteUrl", get(hospitalInfoEditBinding.etWebsite));
        m.put("workingHoursEn", get(hospitalInfoEditBinding.etWorkingHoursEn));
        m.put("workingHoursAr", get(hospitalInfoEditBinding.etWorkingHoursAr));
        m.put("mapQuery", get(hospitalInfoEditBinding.etMapQuery));
        m.put("showersUrl", get(hospitalInfoEditBinding.etShowersUrl));
        m.put("updatedAt", System.currentTimeMillis());

        FirestoreRepo.hospitalInfo()
                .set(m, SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    hospitalInfoEditBinding.btnSave.setEnabled(true);
                    Log.e("HospitalInfoEdit", "Save failed", e);
                    Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String get(EditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }
}
