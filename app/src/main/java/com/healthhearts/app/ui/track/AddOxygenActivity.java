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
import com.healthhearts.app.databinding.ActivityAddOxygenBinding;
import com.healthhearts.app.model.OxygenEntry;
import com.healthhearts.app.ui.common.BaseMenuActivity;
import com.healthhearts.app.util.DateTimeUtil;

public class AddOxygenActivity extends BaseMenuActivity {

    private ActivityAddOxygenBinding addOxygenBinding;
    private long ts;
    private EditText etSpo2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addOxygenBinding = ActivityAddOxygenBinding.inflate(getLayoutInflater());
        setContentView(addOxygenBinding.getRoot());

        setSupportActionBar(addOxygenBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            addOxygenBinding.toolbar.setTitle(getString(R.string.oxygen));
        }

        TextInputLayout til = new TextInputLayout(this);
        til.setHint(getString(R.string.spo2));
        etSpo2 = new EditText(this);
        etSpo2.setInputType(InputType.TYPE_CLASS_NUMBER);
        til.addView(etSpo2);
        addOxygenBinding.containerExtra.addView(til);

        ts = System.currentTimeMillis();
        updateDateTimeFields();

        addOxygenBinding.etDate.setOnClickListener(v -> pickDateTime());
        addOxygenBinding.etTime.setOnClickListener(v -> pickDateTime());
        addOxygenBinding.btnSave.setOnClickListener(v -> save());
    }

    private void pickDateTime() {
        DateTimeUtil.pickDateTime(this, ts, picked -> {
            ts = picked;
            updateDateTimeFields();
        });
    }

    private void updateDateTimeFields() {
        addOxygenBinding.etDate.setText(DateTimeUtil.fmtDate(ts));
        addOxygenBinding.etTime.setText(DateTimeUtil.fmtTime(ts));
    }

    private void save() {
        String uid = FirestoreRepo.uid();
        if (uid == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        int spo2 = 0;
        try {
            spo2 = Integer.parseInt(etSpo2.getText().toString().trim());
        } catch (Exception ignored) {
        }

        OxygenEntry e = new OxygenEntry();
        e.uid = uid;
        e.timestamp = ts;
        e.spo2 = spo2;
        e.notes = addOxygenBinding.etNotes.getText() == null ? "" : addOxygenBinding.etNotes.getText().toString().trim();

        FirestoreRepo.oxygen().add(e)
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
