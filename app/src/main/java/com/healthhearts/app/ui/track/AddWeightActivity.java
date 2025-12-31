package com.healthhearts.app.ui.track;

import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.healthhearts.app.R;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivityAddWeightBinding;
import com.healthhearts.app.model.WeightEntry;
import com.healthhearts.app.ui.common.BaseMenuActivity;
import com.healthhearts.app.util.DateTimeUtil;

public class AddWeightActivity extends BaseMenuActivity {

    private ActivityAddWeightBinding addWeightBinding;
    private long ts;
    private EditText etWeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addWeightBinding = ActivityAddWeightBinding.inflate(getLayoutInflater());
        setContentView(addWeightBinding.getRoot());

        setSupportActionBar(addWeightBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            addWeightBinding.toolbar.setTitle(getString(R.string.weight));
        }

        TextInputLayout til = new TextInputLayout(this);
        til.setHint(getString(R.string.weight) + " (kg)");
        etWeight = new EditText(this);
        etWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        til.addView(etWeight);
        addWeightBinding.containerExtra.addView(til);

        ts = System.currentTimeMillis();
        updateDateTimeFields();

        addWeightBinding.etDate.setOnClickListener(v -> pickDateTime());
        addWeightBinding.etTime.setOnClickListener(v -> pickDateTime());
        addWeightBinding.btnSave.setOnClickListener(v -> save());
    }

    private void pickDateTime() {
        DateTimeUtil.pickDateTime(this, ts, picked -> {
            ts = picked;
            updateDateTimeFields();
        });
    }

    private void updateDateTimeFields() {
        addWeightBinding.etDate.setText(DateTimeUtil.fmtDate(ts));
        addWeightBinding.etTime.setText(DateTimeUtil.fmtTime(ts));
    }

    private void save() {
        String uid = FirestoreRepo.uid();
        if (uid == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        double w = 0;
        try {
            w = Double.parseDouble(etWeight.getText().toString().trim());
        } catch (Exception ignored) {
        }

        WeightEntry e = new WeightEntry();
        e.uid = uid;
        e.timestamp = ts;
        e.weightKg = w;
        e.notes = addWeightBinding.etNotes.getText() == null ? "" : addWeightBinding.etNotes.getText().toString().trim();

        FirestoreRepo.weights().add(e)
                .addOnSuccessListener(r -> {
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(err -> Toast.makeText(this, err.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
