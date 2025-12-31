package com.healthhearts.app.ui.track;

import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.healthhearts.app.R;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivityAddFeedingBinding;
import com.healthhearts.app.model.FeedingEntry;
import com.healthhearts.app.ui.common.BaseMenuActivity;
import com.healthhearts.app.util.DateTimeUtil;

public class AddFeedingActivity extends BaseMenuActivity {

    private ActivityAddFeedingBinding addFeedingBinding;
    private long ts;

    private EditText etAmount;
    private Spinner spMethod;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFeedingBinding = ActivityAddFeedingBinding.inflate(getLayoutInflater());
        setContentView(addFeedingBinding.getRoot());

        setSupportActionBar(addFeedingBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            addFeedingBinding.toolbar.setTitle(getString(R.string.feeding));
        }

        TextInputLayout tilAmount = new TextInputLayout(this);
        tilAmount.setHint(getString(R.string.amount_ml));
        etAmount = new EditText(this);
        etAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        tilAmount.addView(etAmount);
        addFeedingBinding.containerExtra.addView(tilAmount);

        spMethod = new Spinner(this);
        spMethod.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{getString(R.string.breast), getString(R.string.bottle)}));
        addFeedingBinding.containerExtra.addView(spMethod);

        ts = System.currentTimeMillis();
        updateDateTimeFields();

        addFeedingBinding.etDate.setOnClickListener(v -> pickDateTime());
        addFeedingBinding.etTime.setOnClickListener(v -> pickDateTime());

        addFeedingBinding.btnSave.setOnClickListener(v -> save());
    }

    private void pickDateTime() {
        DateTimeUtil.pickDateTime(this, ts, picked -> {
            ts = picked;
            updateDateTimeFields();
        });
    }

    private void updateDateTimeFields() {
        addFeedingBinding.etDate.setText(DateTimeUtil.fmtDate(ts));
        addFeedingBinding.etTime.setText(DateTimeUtil.fmtTime(ts));
    }

    private void save() {
        String uid = FirestoreRepo.uid();
        if (uid == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        int amount = 0;
        try {
            amount = Integer.parseInt(etAmount.getText().toString().trim());
        } catch (Exception ignored) {
        }

        String method = spMethod.getSelectedItemPosition() == 0 ? "breast" : "bottle";
        String notes = addFeedingBinding.etNotes.getText() == null ? "" : addFeedingBinding.etNotes.getText().toString().trim();

        FeedingEntry e = new FeedingEntry();
        e.uid = uid;
        e.timestamp = ts;
        e.amountMl = amount;
        e.method = method;
        e.notes = notes;

        FirestoreRepo.feedings().add(e)
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
