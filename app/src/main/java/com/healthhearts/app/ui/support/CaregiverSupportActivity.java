package com.healthhearts.app.ui.support;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.healthhearts.app.R;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivityCaregiverSupportBinding;
import com.healthhearts.app.model.CaregiverContact;
import com.healthhearts.app.model.PatientStory;
import com.healthhearts.app.model.SupportGroup;
import com.healthhearts.app.ui.common.BaseMenuActivity;
import com.healthhearts.app.util.LocaleUtil;
import com.healthhearts.app.util.Prefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaregiverSupportActivity extends BaseMenuActivity {

    private ActivityCaregiverSupportBinding caregiverSupportBinding;

    private SupportGroupAdapter groupAdapter;
    private CaregiverContactAdapter contactAdapter;
    private PatientStoryAdapter storyAdapter;

    private boolean isAdmin = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        caregiverSupportBinding = ActivityCaregiverSupportBinding.inflate(getLayoutInflater());
        setContentView(caregiverSupportBinding.getRoot());

        setSupportActionBar(caregiverSupportBinding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        caregiverSupportBinding.toolbar.setTitle(getString(R.string.caregiver_support));

        boolean isArabic = LocaleUtil.isArabic(this);

        groupAdapter = new SupportGroupAdapter(isArabic, this::openUrlSafe);
        contactAdapter = new CaregiverContactAdapter(isArabic, this::openUrlSafe, this::openEmailSafe, this::openPhoneSafe);
        storyAdapter = new PatientStoryAdapter(isArabic, this::openUrlSafe);

        caregiverSupportBinding.rvGroups.setLayoutManager(new LinearLayoutManager(this));
        caregiverSupportBinding.rvGroups.setAdapter(groupAdapter);

        caregiverSupportBinding.rvContacts.setLayoutManager(new LinearLayoutManager(this));
        caregiverSupportBinding.rvContacts.setAdapter(contactAdapter);

        caregiverSupportBinding.rvStories.setLayoutManager(new LinearLayoutManager(this));
        caregiverSupportBinding.rvStories.setAdapter(storyAdapter);

        caregiverSupportBinding.fabAdd.setOnClickListener(v -> showAddChooser());

        isAdmin = Prefs.isAdmin(this);
        caregiverSupportBinding.fabAdd.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadSupportGroups();
        loadContacts();
        loadStories();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddChooser() {
        if (!isAdmin) return;

        CharSequence[] items = new CharSequence[]{
                getString(R.string.add_support_group),
                getString(R.string.add_contact),
                getString(R.string.add_story)
        };

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.add)
                .setItems(items, (dialog, which) -> {
                    if (which == 0) showAddSupportGroupDialog();
                    else if (which == 1) showAddContactDialog();
                    else showAddStoryDialog();
                })
                .show();
    }

    private void loadSupportGroups() {
        FirestoreRepo.caregiverSupportGroups()
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    List<SupportGroup> out = new ArrayList<>();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        SupportGroup g = d.toObject(SupportGroup.class);
                        if (g != null) {
                            g.id = d.getId();
                            out.add(g);
                        }
                    }
                    groupAdapter.setItems(out);
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void loadContacts() {
        FirestoreRepo.caregiverContacts()
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    List<CaregiverContact> out = new ArrayList<>();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        CaregiverContact c = d.toObject(CaregiverContact.class);
                        if (c != null) {
                            c.id = d.getId();
                            out.add(c);
                        }
                    }
                    contactAdapter.setItems(out);
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void loadStories() {
        FirestoreRepo.caregiverStories()
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    List<PatientStory> out = new ArrayList<>();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        PatientStory s = d.toObject(PatientStory.class);
                        if (s != null) {
                            s.id = d.getId();
                            out.add(s);
                        }
                    }
                    storyAdapter.setItems(out);
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void showAddSupportGroupDialog() {
        if (!isAdmin) return;

        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_support_group, null, false);

        com.google.android.material.textfield.TextInputEditText etNameEn =
                v.findViewById(R.id.etNameEn);
        com.google.android.material.textfield.TextInputEditText etNameAr =
                v.findViewById(R.id.etNameAr);
        com.google.android.material.textfield.TextInputEditText etUrl =
                v.findViewById(R.id.etUrl);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.add_support_group)
                .setView(v)
                .setPositiveButton(R.string.save, (d, which) -> {
                    String nameEn = get(etNameEn);
                    String nameAr = get(etNameAr);
                    String url = get(etUrl);

                    if (url.isEmpty()) {
                        Toast.makeText(this, R.string.url_required, Toast.LENGTH_LONG).show();
                        return;
                    }

                    Map<String, Object> m = new HashMap<>();
                    m.put("nameEnglish", nameEn);
                    m.put("nameArabic", nameAr);
                    m.put("url", url);
                    long now = System.currentTimeMillis();
                    m.put("createdAt", now);
                    m.put("updatedAt", now);

                    FirestoreRepo.caregiverSupportGroups().add(m)
                            .addOnSuccessListener(r -> loadSupportGroups())
                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showAddContactDialog() {
        if (!isAdmin) return;

        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_caregiver_contact, null, false);

        com.google.android.material.textfield.TextInputEditText etNameEn =
                v.findViewById(R.id.etNameEn);
        com.google.android.material.textfield.TextInputEditText etNameAr =
                v.findViewById(R.id.etNameAr);
        com.google.android.material.textfield.TextInputEditText etWebsite =
                v.findViewById(R.id.etWebsite);
        com.google.android.material.textfield.TextInputEditText etEmail =
                v.findViewById(R.id.etEmail);
        com.google.android.material.textfield.TextInputEditText etPhone =
                v.findViewById(R.id.etPhone);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.add_contact)
                .setView(v)
                .setPositiveButton(R.string.save, (d, which) -> {
                    String nameEn = get(etNameEn);
                    String nameAr = get(etNameAr);
                    String website = get(etWebsite);
                    String email = get(etEmail);
                    String phone = get(etPhone);

                    if (website.isEmpty() && email.isEmpty() && phone.isEmpty()) {
                        Toast.makeText(this, R.string.contact_method_required, Toast.LENGTH_LONG).show();
                        return;
                    }

                    Map<String, Object> m = new HashMap<>();
                    m.put("nameEnglish", nameEn);
                    m.put("nameArabic", nameAr);
                    m.put("website", website);
                    m.put("email", email);
                    m.put("phone", phone);
                    long now = System.currentTimeMillis();
                    m.put("createdAt", now);
                    m.put("updatedAt", now);

                    FirestoreRepo.caregiverContacts().add(m)
                            .addOnSuccessListener(r -> loadContacts())
                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showAddStoryDialog() {
        if (!isAdmin) return;

        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_patient_story, null, false);

        MaterialAutoCompleteTextView ddType = v.findViewById(R.id.ddType);

        com.google.android.material.textfield.TextInputLayout tilTextEn = v.findViewById(R.id.tilTextEn);
        com.google.android.material.textfield.TextInputLayout tilTextAr = v.findViewById(R.id.tilTextAr);
        com.google.android.material.textfield.TextInputLayout tilVideo = v.findViewById(R.id.tilVideo);

        com.google.android.material.textfield.TextInputEditText etTitleEn = v.findViewById(R.id.etTitleEn);
        com.google.android.material.textfield.TextInputEditText etTitleAr = v.findViewById(R.id.etTitleAr);
        com.google.android.material.textfield.TextInputEditText etTextEn = v.findViewById(R.id.etTextEn);
        com.google.android.material.textfield.TextInputEditText etTextAr = v.findViewById(R.id.etTextAr);
        com.google.android.material.textfield.TextInputEditText etVideoUrl = v.findViewById(R.id.etVideoUrl);

        String[] types = new String[]{"text", "video"};
        ddType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, types));
        ddType.setText("text", false);

        Runnable update = () -> {
            String t = ddType.getText() == null ? "text" : ddType.getText().toString().trim().toLowerCase();
            boolean isVideo = "video".equals(t);
            tilTextEn.setVisibility(isVideo ? View.GONE : View.VISIBLE);
            tilTextAr.setVisibility(isVideo ? View.GONE : View.VISIBLE);
            tilVideo.setVisibility(isVideo ? View.VISIBLE : View.GONE);
        };
        update.run();
        ddType.setOnItemClickListener((p, vv, pos, id) -> update.run());

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.add_story)
                .setView(v)
                .setPositiveButton(R.string.save, (d, which) -> {
                    String type = ddType.getText() == null ? "text" : ddType.getText().toString().trim().toLowerCase();

                    String titleEn = get(etTitleEn);
                    String titleAr = get(etTitleAr);
                    String textEn = get(etTextEn);
                    String textAr = get(etTextAr);
                    String videoUrl = get(etVideoUrl);

                    if ("video".equals(type) && videoUrl.isEmpty()) {
                        Toast.makeText(this, R.string.video_required, Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!"video".equals(type) && textEn.isEmpty() && textAr.isEmpty()) {
                        Toast.makeText(this, R.string.story_text_required, Toast.LENGTH_LONG).show();
                        return;
                    }

                    Map<String, Object> m = new HashMap<>();
                    m.put("type", type);
                    m.put("titleEnglish", titleEn);
                    m.put("titleArabic", titleAr);
                    m.put("textEnglish", "video".equals(type) ? "" : textEn);
                    m.put("textArabic", "video".equals(type) ? "" : textAr);
                    m.put("videoUrl", "video".equals(type) ? videoUrl : "");
                    long now = System.currentTimeMillis();
                    m.put("createdAt", now);
                    m.put("updatedAt", now);

                    FirestoreRepo.caregiverStories().add(m)
                            .addOnSuccessListener(r -> loadStories())
                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private String get(com.google.android.material.textfield.TextInputEditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }

    private void openUrlSafe(String url) {
        if (url == null) return;
        String u = url.trim();
        if (u.isEmpty()) return;
        if (!u.startsWith("http://") && !u.startsWith("https://")) u = "https://" + u;

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(u)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "No app found to open link", Toast.LENGTH_SHORT).show();
        }
    }

    private void openEmailSafe(String email) {
        if (email == null) return;
        String e = email.trim();
        if (e.isEmpty()) return;

        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:" + e));
        try {
            startActivity(i);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPhoneSafe(String phone) {
        if (phone == null) return;
        String p = phone.trim();
        if (p.isEmpty()) return;

        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse("tel:" + p));
        try {
            startActivity(i);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "No dialer app found", Toast.LENGTH_SHORT).show();
        }
    }
}
