package com.healthhearts.app.ui.contact;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivityProviderEditBinding;
import com.healthhearts.app.model.ProviderContact;
import com.healthhearts.app.ui.common.BaseMenuActivity;

import java.util.HashMap;
import java.util.Map;

public class ProviderEditActivity extends BaseMenuActivity {

    public static final String EXTRA_PROVIDER_ID = "providerId";

    private ActivityProviderEditBinding b;
    private String providerId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityProviderEditBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        providerId = getIntent().getStringExtra(EXTRA_PROVIDER_ID);
        b.toolbar.setTitle(providerId == null ? getString(com.healthhearts.app.R.string.add_provider) : getString(com.healthhearts.app.R.string.edit_provider));

        b.btnSave.setOnClickListener(v -> save());

        if (providerId != null) load();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void load() {
        FirestoreRepo.providers().document(providerId).get()
                .addOnSuccessListener(this::bind)
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void bind(DocumentSnapshot d) {
        ProviderContact providerContact = d.toObject(ProviderContact.class);
        if (providerContact == null) return;
        b.etName.setText(providerContact.name);
        b.etPhone.setText(providerContact.phone);
        b.etEmail.setText(providerContact.email);
        b.etWebsite.setText(providerContact.website);
        b.etAddress.setText(providerContact.address);
    }

    private void save() {
        String uid = FirestoreRepo.uid();
        if (uid == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        long now = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("name", txt(b.etName));
        map.put("phone", txt(b.etPhone));
        map.put("email", txt(b.etEmail));
        map.put("website", txt(b.etWebsite));
        map.put("address", txt(b.etAddress));
        map.put("updatedAt", now);
        if (providerId == null) map.put("createdAt", now);

        if (providerId == null) {
            FirestoreRepo.providers().add(map)
                    .addOnSuccessListener(r -> {
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            FirestoreRepo.providers().document(providerId).update(map)
                    .addOnSuccessListener(r -> {
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private String txt(android.widget.EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
