package com.healthhearts.app.ui.section;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.healthhearts.app.R;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivityContentEditBinding;
import com.healthhearts.app.model.ContentItem;
import com.healthhearts.app.ui.common.BaseMenuActivity;
import com.healthhearts.app.util.Prefs;

import java.util.HashMap;
import java.util.Map;

public class ContentEditActivity extends BaseMenuActivity {

    public static final String EXTRA_SECTION_ID = "sectionId";
    public static final String EXTRA_CONTENT_ID = "contentId";
    private final String[] TYPES = new String[]{"text", "link", "video", "pdf"};
    private ActivityContentEditBinding contentEditBinding;
    private int sectionId;
    private String contentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentEditBinding = ActivityContentEditBinding.inflate(getLayoutInflater());
        setContentView(contentEditBinding.getRoot());

        if (!Prefs.isAdmin(this)) {
            Toast.makeText(this, "Admin only", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        sectionId = getIntent().getIntExtra(EXTRA_SECTION_ID, 1);
        contentId = getIntent().getStringExtra(EXTRA_CONTENT_ID);

        setSupportActionBar(contentEditBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        contentEditBinding.toolbar.setTitle(contentId == null ? getString(R.string.title_add_content) : getString(R.string.title_edit_content));

        String[] displayTypes = new String[]{
                getString(R.string.type_text),
                getString(R.string.type_link),
                getString(R.string.type_video),
                getString(R.string.type_pdf)
        };
        contentEditBinding.spType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, displayTypes));
        contentEditBinding.spType.setSelection(0);
        contentEditBinding.spType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                showFieldsForType(TYPES[position]);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        contentEditBinding.btnSave.setOnClickListener(v -> save());

        showFieldsForType("text");

        if (contentId != null) {
            load();
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

    private void showFieldsForType(String type) {
        contentEditBinding.groupText.setVisibility("text".equals(type) ? View.VISIBLE : View.GONE);
        contentEditBinding.groupLink.setVisibility("link".equals(type) ? View.VISIBLE : View.GONE);
        contentEditBinding.groupVideo.setVisibility("video".equals(type) ? View.VISIBLE : View.GONE);
        contentEditBinding.groupPdf.setVisibility("pdf".equals(type) ? View.VISIBLE : View.GONE);
    }

    private void load() {
        FirestoreRepo.content().document(contentId).get()
                .addOnSuccessListener(this::bind)
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void bind(DocumentSnapshot d) {
        ContentItem item = d.toObject(ContentItem.class);
        if (item == null) return;

        String type = item.type == null ? "text" : item.type;
        int sel = 0;
        for (int i = 0; i < TYPES.length; i++) {
            if (TYPES[i].equalsIgnoreCase(type)) {
                sel = i;
                break;
            }
        }
        contentEditBinding.spType.setSelection(sel);
        showFieldsForType(TYPES[sel]);

        contentEditBinding.etTitleEn.setText(item.englishTitle);
        contentEditBinding.etTitleAr.setText(item.arabicTitle);
        contentEditBinding.etBodyEn.setText(item.englishBody);
        contentEditBinding.etBodyAr.setText(item.arabicBody);
        contentEditBinding.etMediaUrl.setText(item.mediaUrl);

        contentEditBinding.etLinkEn.setText(item.linkEnglish);
        contentEditBinding.etLinkAr.setText(item.linkArabic);

        contentEditBinding.etVideoUrlEn.setText(item.videoUrlEnglish);
        contentEditBinding.etVideoUrlAr.setText(item.videoUrlArabic);

        contentEditBinding.etPdfUrlEn.setText(item.pdfUrlEnglish);
        contentEditBinding.etPdfUrlAr.setText(item.pdfUrlArabic);
    }

    private void save() {
        int pos = contentEditBinding.spType.getSelectedItemPosition();
        String type = TYPES[Math.max(0, Math.min(pos, TYPES.length - 1))];

        long now = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        map.put("sectionId", sectionId);
        map.put("type", type);
        map.put("updatedAt", now);
        if (contentId == null) map.put("createdAt", now);

        map.put("englishBody", "");
        map.put("arabicBody", "");
        map.put("mediaUrl", "");
        map.put("linkEnglish", "");
        map.put("linkArabic", "");
        map.put("videoUrlEnglish", "");
        map.put("videoUrlArabic", "");
        map.put("pdfUrlEnglish", "");
        map.put("pdfUrlArabic", "");

        map.put("englishTitle", txt(contentEditBinding.etTitleEn));
        map.put("arabicTitle", txt(contentEditBinding.etTitleAr));

        if ("text".equals(type)) {
            map.put("englishBody", txt(contentEditBinding.etBodyEn));
            map.put("arabicBody", txt(contentEditBinding.etBodyAr));
            map.put("mediaUrl", txt(contentEditBinding.etMediaUrl));
        } else if ("link".equals(type)) {
            map.put("linkEnglish", txt(contentEditBinding.etLinkEn));
            map.put("linkArabic", txt(contentEditBinding.etLinkAr));
        } else if ("video".equals(type)) {
            map.put("videoUrlEnglish", txt(contentEditBinding.etVideoUrlEn));
            map.put("videoUrlArabic", txt(contentEditBinding.etVideoUrlAr));
        } else if ("pdf".equals(type)) {
            map.put("pdfUrlEnglish", txt(contentEditBinding.etPdfUrlEn));
            map.put("pdfUrlArabic", txt(contentEditBinding.etPdfUrlAr));
        }

        if (contentId == null) {
            FirestoreRepo.content().add(map)
                    .addOnSuccessListener(r -> {
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            FirestoreRepo.content().document(contentId).update(map)
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
