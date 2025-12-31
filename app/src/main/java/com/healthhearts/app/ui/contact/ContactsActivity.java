package com.healthhearts.app.ui.contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.healthhearts.app.R;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivityContactsBinding;
import com.healthhearts.app.model.ContactItem;
import com.healthhearts.app.model.ProviderContact;
import com.healthhearts.app.ui.common.BaseMenuActivity;
import com.healthhearts.app.util.LocaleUtil;
import com.healthhearts.app.util.Prefs;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends BaseMenuActivity {

    private ActivityContactsBinding b;
    private ContactRowAdapter adapter;
    private int tab = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            b.toolbar.setTitle(getString(R.string.contacts_support));
        }

        adapter = new ContactRowAdapter(new ContactRowAdapter.Listener() {
            @Override
            public void onClick(ContactRowAdapter.Row r) {
                handleClick(r);
            }

            @Override
            public void onLongPress(ContactRowAdapter.Row r) {
                handleLongPress(r);
            }
        });

        b.recycler.setLayoutManager(new LinearLayoutManager(this));
        b.recycler.setAdapter(adapter);

        b.tabs.addTab(b.tabs.newTab().setText(getString(R.string.hospital_contacts)));
        b.tabs.addTab(b.tabs.newTab().setText(getString(R.string.local_contacts)));
        b.tabs.addTab(b.tabs.newTab().setText(getString(R.string.my_providers)));

        b.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

        b.fabAdd.setOnClickListener(v -> openAdd());

        updateFabVisibility();
    }

    @Override
    protected void onStart() {
        super.onStart();
        load();
    }

    private void updateFabVisibility() {
        if (tab == 2) {
            b.fabAdd.setVisibility(android.view.View.VISIBLE);
        } else {
            b.fabAdd.setVisibility(Prefs.isAdmin(this) ? android.view.View.VISIBLE : android.view.View.GONE);
        }
    }

    private void openAdd() {
        if (tab == 2) {
            startActivity(new Intent(this, ProviderEditActivity.class));
            return;
        }
        if (!Prefs.isAdmin(this)) return;
        Intent i = new Intent(this, ContactEditActivity.class);
        i.putExtra(ContactEditActivity.EXTRA_DEFAULT_TYPE, tab == 0 ? "hospital" : "local");
        startActivity(i);
    }

    private void load() {
        updateFabVisibility();

        b.tvEmpty.setVisibility(android.view.View.GONE);
        if (tab == 2) {
            loadProviders();
        } else {
            loadContacts(tab == 0 ? "hospital" : "local");
        }
    }

    private void loadContacts(String type) {
        FirestoreRepo.contacts()
                .whereEqualTo("type", type)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    List<ContactRowAdapter.Row> rows = new ArrayList<>();
                    boolean ar = LocaleUtil.isArabic();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        ContactItem it = d.toObject(ContactItem.class);
                        if (it == null) continue;
                        ContactRowAdapter.Row r = new ContactRowAdapter.Row();
                        r.id = it.id;
                        r.source = "contacts";
                        r.kind = it.kind;
                        r.title = ar ? it.arabicTitle : it.englishTitle;
                        if (r.title == null || r.title.trim().isEmpty()) r.title = "(No title)";
                        r.value = it.value;
                        rows.add(r);
                    }
                    adapter.setRows(rows);
                    b.tvEmpty.setVisibility(rows.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    b.tvEmpty.setVisibility(android.view.View.VISIBLE);
                });
    }

    private void loadProviders() {
        String uid = FirestoreRepo.uid();
        if (uid == null) {
            adapter.setRows(new ArrayList<>());
            b.tvEmpty.setVisibility(android.view.View.VISIBLE);
            return;
        }
        FirestoreRepo.providers()
                .whereEqualTo("uid", uid)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    List<ContactRowAdapter.Row> rows = new ArrayList<>();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        ProviderContact providerContact = d.toObject(ProviderContact.class);
                        if (providerContact == null) continue;
                        ContactRowAdapter.Row r = new ContactRowAdapter.Row();
                        r.id = providerContact.id;
                        r.source = "providers";
                        r.kind = "provider";
                        r.title = providerContact.name == null ? "" : providerContact.name;
                        String displayValue = (providerContact.phone == null ? "" : providerContact.phone);
                        if (displayValue.trim().isEmpty())
                            displayValue = (providerContact.email == null ? "" : providerContact.email);
                        if (displayValue.trim().isEmpty())
                            displayValue = (providerContact.website == null ? "" : providerContact.website);
                        if (displayValue.trim().isEmpty())
                            displayValue = (providerContact.address == null ? "" : providerContact.address);
                        r.value = displayValue;
                        rows.add(r);
                    }
                    adapter.setRows(rows);
                    b.tvEmpty.setVisibility(rows.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    b.tvEmpty.setVisibility(android.view.View.VISIBLE);
                });
    }

    private void handleClick(ContactRowAdapter.Row r) {
        if (r == null) return;
        if ("providers".equals(r.source)) {
            Intent i = new Intent(this, ProviderEditActivity.class);
            i.putExtra(ProviderEditActivity.EXTRA_PROVIDER_ID, r.id);
            startActivity(i);
            return;
        }

        String kind = r.kind == null ? "" : r.kind;
        String val = r.value == null ? "" : r.value.trim();
        if (val.isEmpty()) return;

        try {
            if (kind.equalsIgnoreCase("phone")) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + val)));
            } else if (kind.equalsIgnoreCase("email")) {
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + val)));
            } else if (kind.equalsIgnoreCase("map")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(val))));
            } else if (kind.equalsIgnoreCase("website") || kind.equalsIgnoreCase("link")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(val)));
            } else {
                Toast.makeText(this, val, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Can't open", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLongPress(ContactRowAdapter.Row r) {
        if (r == null) return;

        if ("contacts".equals(r.source)) {
            if (!Prefs.isAdmin(this)) return;
            new AlertDialog.Builder(this)
                    .setTitle(r.title)
                    .setItems(new CharSequence[]{getString(R.string.edit), getString(R.string.delete)}, (d, which) -> {
                        if (which == 0) {
                            Intent i = new Intent(this, ContactEditActivity.class);
                            i.putExtra(ContactEditActivity.EXTRA_CONTACT_ID, r.id);
                            startActivity(i);
                        } else {
                            confirmDelete("contacts", r.id);
                        }
                    }).show();
        } else {
            // provider row (user-owned)
            new AlertDialog.Builder(this)
                    .setTitle(r.title)
                    .setItems(new CharSequence[]{getString(R.string.edit), getString(R.string.delete)}, (d, which) -> {
                        if (which == 0) {
                            Intent i = new Intent(this, ProviderEditActivity.class);
                            i.putExtra(ProviderEditActivity.EXTRA_PROVIDER_ID, r.id);
                            startActivity(i);
                        } else {
                            confirmDelete("providers", r.id);
                        }
                    }).show();
        }
    }

    private void confirmDelete(String source, String id) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage("Delete this item?")
                .setPositiveButton(R.string.delete, (d, w) -> doDelete(source, id))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void doDelete(String source, String id) {
        if ("contacts".equals(source)) {
            FirestoreRepo.contacts().document(id).delete()
                    .addOnSuccessListener(v -> load())
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            FirestoreRepo.providers().document(id).delete()
                    .addOnSuccessListener(v -> load())
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
        }
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
