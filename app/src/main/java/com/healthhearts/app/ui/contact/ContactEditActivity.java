package com.healthhearts.app.ui.contact;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.healthhearts.app.R;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivityContactEditBinding;
import com.healthhearts.app.model.ContactItem;
import com.healthhearts.app.ui.common.BaseMenuActivity;
import com.healthhearts.app.util.Prefs;

import java.util.HashMap;
import java.util.Map;

public class ContactEditActivity extends BaseMenuActivity {

    public static final String EXTRA_CONTACT_ID = "contactId";
    public static final String EXTRA_DEFAULT_TYPE = "defaultType";
    private final String[] TYPES = new String[]{"hospital", "local"};
    private final String[] KINDS = new String[]{"phone", "email", "website", "map", "text"};
    private ActivityContactEditBinding b;
    private String contactId;
    private String[] TYPE_LABELS;
    private String[] KIND_LABELS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityContactEditBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        if (!Prefs.isAdmin(this)) {
            Toast.makeText(this, "Admin only", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        TYPE_LABELS = new String[]{getString(R.string.tab_hospital), getString(R.string.tab_local)};
        KIND_LABELS = new String[]{
                getString(R.string.kind_phone),
                getString(R.string.kind_email),
                getString(R.string.kind_website),
                getString(R.string.kind_map),
                getString(R.string.kind_text)
        };

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contactId = getIntent().getStringExtra(EXTRA_CONTACT_ID);
        b.toolbar.setTitle(contactId == null ? getString(R.string.title_add_contact) : getString(R.string.title_edit_contact));

        b.spType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, TYPE_LABELS));
        b.spKind.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, KIND_LABELS));

        String defType = getIntent().getStringExtra(EXTRA_DEFAULT_TYPE);
        if (defType != null) {
            b.spType.setSelection(defType.equals("local") ? 1 : 0);
        }

        b.btnSave.setOnClickListener(v -> save());

        if (contactId != null) load();
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
        FirestoreRepo.contacts().document(contactId).get()
                .addOnSuccessListener(this::bind)
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void bind(DocumentSnapshot d) {
        ContactItem contactItem = d.toObject(ContactItem.class);
        if (contactItem == null) return;

        b.etTitleEn.setText(contactItem.englishTitle);
        b.etTitleAr.setText(contactItem.arabicTitle);
        b.etValue.setText(contactItem.value);

        b.spType.setSelection("local".equals(contactItem.type) ? 1 : 0);

        int idx = 0;
        for (int i = 0; i < KINDS.length; i++) {
            if (KINDS[i].equalsIgnoreCase(contactItem.kind)) {
                idx = i;
                break;
            }
        }
        b.spKind.setSelection(idx);
    }

    private void save() {
        String type = TYPES[b.spType.getSelectedItemPosition()];
        String kind = KINDS[b.spKind.getSelectedItemPosition()];
        String englishTitle = txt(b.etTitleEn);
        String arabicTitle = txt(b.etTitleAr);
        String value = txt(b.etValue);

        long now = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("kind", kind);
        map.put("englishTitle", englishTitle);
        map.put("arabicTitle", arabicTitle);
        map.put("value", value);
        map.put("updatedAt", now);
        if (contactId == null) map.put("createdAt", now);

        if (contactId == null) {
            FirestoreRepo.contacts().add(map)
                    .addOnSuccessListener(r -> {
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            FirestoreRepo.contacts().document(contactId).update(map)
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
