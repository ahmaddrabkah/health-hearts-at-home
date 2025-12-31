package com.healthhearts.app.ui.track;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.healthhearts.app.R;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivityAddBloodPressureBinding;
import com.healthhearts.app.model.BloodPressureEntry;
import com.healthhearts.app.ui.common.BaseMenuActivity;

public class AddBloodPressureActivity extends BaseMenuActivity {

    private ActivityAddBloodPressureBinding addBloodPressureBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addBloodPressureBinding = ActivityAddBloodPressureBinding.inflate(getLayoutInflater());
        setContentView(addBloodPressureBinding.getRoot());

        setSupportActionBar(addBloodPressureBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            addBloodPressureBinding.toolbar.setTitle(getString(R.string.blood_pressure));
        }

        addBloodPressureBinding.btnSave.setOnClickListener(v -> save());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        String uid = FirestoreRepo.uid();
        if (uid == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String systolicStr = addBloodPressureBinding.etSystolic.getText() == null ? "" : addBloodPressureBinding.etSystolic.getText().toString().trim();
        String diastolicStr = addBloodPressureBinding.etDiastolic.getText() == null ? "" : addBloodPressureBinding.etDiastolic.getText().toString().trim();
        String note = addBloodPressureBinding.etNote.getText() == null ? "" : addBloodPressureBinding.etNote.getText().toString().trim();

        if (systolicStr.isEmpty() || diastolicStr.isEmpty()) {
            Toast.makeText(this, getString(R.string.required), Toast.LENGTH_SHORT).show();
            return;
        }

        int sys, dia;
        try {
            sys = Integer.parseInt(systolicStr);
            dia = Integer.parseInt(diastolicStr);
        } catch (Exception ex) {
            Toast.makeText(this, getString(R.string.invalid_number), Toast.LENGTH_SHORT).show();
            return;
        }

        BloodPressureEntry bloodPressureEntry = new BloodPressureEntry();
        bloodPressureEntry.uid = uid;
        bloodPressureEntry.timestamp = System.currentTimeMillis();
        bloodPressureEntry.value = sys + "/" + dia + " mmHg";
        if (!note.isEmpty()) bloodPressureEntry.value = bloodPressureEntry.value + " • " + note;

        FirestoreRepo.bloodPressure().add(bloodPressureEntry)
                .addOnSuccessListener(r -> finish())
                .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show());
    }
}
