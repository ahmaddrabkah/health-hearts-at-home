package com.healthhearts.app.ui.section;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.healthhearts.app.R;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivitySectionListBinding;
import com.healthhearts.app.model.ContentItem;
import com.healthhearts.app.ui.common.BaseMenuActivity;
import com.healthhearts.app.ui.home.HomeActivity;
import com.healthhearts.app.util.LocaleUtil;
import com.healthhearts.app.util.Prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SectionListActivity extends BaseMenuActivity {

    public static final String EXTRA_SECTION_ID = "sectionId";
    private final List<ContentItem> all = new ArrayList<>();
    private ActivitySectionListBinding sectionListBinding;
    private int sectionId;
    private ContentAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sectionListBinding = ActivitySectionListBinding.inflate(getLayoutInflater());
        setContentView(sectionListBinding.getRoot());

        sectionId = getIntent().getIntExtra(EXTRA_SECTION_ID, 1);

        setSupportActionBar(sectionListBinding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sectionListBinding.toolbar.setTitle(titleForSection(sectionId));

        adapter = new ContentAdapter(new ContentAdapter.Listener() {
            @Override
            public void onClick(ContentItem item) {
                openDetail(item.id);
            }

            @Override
            public void onLongPress(ContentItem item) {
                if (Prefs.isAdmin(SectionListActivity.this)) showAdminActions(item);
            }
        });

        sectionListBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        sectionListBinding.recycler.setAdapter(adapter);

        sectionListBinding.fabAdd.setOnClickListener(v -> {
            if (!Prefs.isAdmin(this)) return;
            Intent i = new Intent(this, ContentEditActivity.class);
            i.putExtra(ContentEditActivity.EXTRA_SECTION_ID, sectionId);
            startActivity(i);
        });

        sectionListBinding.fabAdd.setVisibility(Prefs.isAdmin(this) ? android.view.View.VISIBLE : android.view.View.GONE);

        sectionListBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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

    private void load() {
        FirestoreRepo.content()
                .whereEqualTo("sectionId", sectionId)
                .get()
                .addOnSuccessListener(snap -> {
                    all.clear();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        ContentItem it = d.toObject(ContentItem.class);
                        if (it != null) {
                            it.id = d.getId();
                            all.add(it);
                        }
                    }
                    all.sort((a, b) -> Long.compare(b.updatedAt, a.updatedAt));
                    adapter.setItems(all);
                    sectionListBinding.tvEmpty.setVisibility(all.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    sectionListBinding.tvEmpty.setVisibility(android.view.View.VISIBLE);
                });
    }

    private void filter(String q) {
        if (q == null) q = "";
        String qq = q.toLowerCase(Locale.ROOT).trim();
        if (qq.isEmpty()) {
            adapter.setItems(all);
            return;
        }
        boolean ar = LocaleUtil.isArabic();
        List<ContentItem> out = new ArrayList<>();
        for (ContentItem it : all) {
            String t = ar ? it.arabicTitle : it.englishTitle;
            String body = ar ? it.arabicBody : it.englishBody;
            String url = "";
            if ("link".equalsIgnoreCase(it.type)) {
                url = ar ? it.linkArabic : it.linkEnglish;
            } else if ("video".equalsIgnoreCase(it.type)) {
                url = ar ? it.videoUrlArabic : it.videoUrlEnglish;
            } else if ("pdf".equalsIgnoreCase(it.type)) {
                url = ar ? it.pdfUrlArabic : it.pdfUrlEnglish;
            } else {
                url = it.mediaUrl;
            }
            String joined = ((t == null ? "" : t) + " " + (body == null ? "" : body) + " " + (url == null ? "" : url)).toLowerCase(Locale.ROOT);
            if (joined.contains(qq)) out.add(it);
        }
        adapter.setItems(out);
    }

    private void openDetail(String id) {
        Intent i = new Intent(this, ContentDetailActivity.class);
        i.putExtra(ContentDetailActivity.EXTRA_CONTENT_ID, id);
        startActivity(i);
    }

    private void showAdminActions(ContentItem item) {
        boolean ar = LocaleUtil.isArabic();
        String type = item.type == null ? "text" : item.type;
        String title;
        if ("text".equalsIgnoreCase(type)) {
            title = ar ? item.arabicTitle : item.englishTitle;
        } else if ("link".equalsIgnoreCase(type)) {
            title = getString(R.string.type_link);
        } else if ("video".equalsIgnoreCase(type)) {
            title = getString(R.string.type_video);
        } else if ("pdf".equalsIgnoreCase(type)) {
            title = getString(R.string.type_pdf);
        } else {
            title = "Item";
        }

        new AlertDialog.Builder(this)
                .setTitle(title == null || title.trim().isEmpty() ? "Item" : title)
                .setItems(new CharSequence[]{getString(R.string.edit), getString(R.string.delete)}, (d, which) -> {
                    if (which == 0) {
                        Intent i = new Intent(this, ContentEditActivity.class);
                        i.putExtra(ContentEditActivity.EXTRA_SECTION_ID, sectionId);
                        i.putExtra(ContentEditActivity.EXTRA_CONTENT_ID, item.id);
                        startActivity(i);
                    } else {
                        confirmDelete(item);
                    }
                })
                .show();
    }

    private void confirmDelete(ContentItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage("Delete this item?")
                .setPositiveButton(R.string.delete, (d, w) -> doDelete(item.id))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void doDelete(String id) {
        FirestoreRepo.content().document(id).delete()
                .addOnSuccessListener(v -> load())
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private String titleForSection(int id) {
        switch (id) {
            case HomeActivity.SEC_GENERAL:
                return getString(R.string.general_childcare_info);
            case HomeActivity.SEC_TUTORIALS:
                return getString(R.string.tutorials_child_care);
            case HomeActivity.SEC_SPIRITUAL:
                return getString(R.string.spiritual_needs);
            case HomeActivity.SEC_HOSPITAL:
                return getString(R.string.hospital_information);
            case HomeActivity.SEC_SUPPORT:
                return getString(R.string.caregiver_support);
            case HomeActivity.SEC_ABOUT:
                return getString(R.string.about_chd);
            default:
                return getString(R.string.app_name);
        }
    }
}
