package com.healthhearts.app.ui.section;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.healthhearts.app.R;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivityContentDetailBinding;
import com.healthhearts.app.model.ContentItem;
import com.healthhearts.app.ui.common.BaseMenuActivity;
import com.healthhearts.app.util.LocaleUtil;

public class ContentDetailActivity extends BaseMenuActivity {

    public static final String EXTRA_CONTENT_ID = "contentId";

    private ActivityContentDetailBinding contentDetailBinding;
    private String contentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentDetailBinding = ActivityContentDetailBinding.inflate(getLayoutInflater());
        setContentView(contentDetailBinding.getRoot());

        setSupportActionBar(contentDetailBinding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        contentDetailBinding.toolbar.setTitle(getString(R.string.title_content_details));

        contentId = getIntent().getStringExtra(EXTRA_CONTENT_ID);
        if (contentId == null) {
            finish();
            return;
        }

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
        FirestoreRepo.content().document(contentId).get()
                .addOnSuccessListener(this::bind)
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void bind(DocumentSnapshot d) {
        ContentItem item = d.toObject(ContentItem.class);
        if (item == null) return;

        boolean ar = LocaleUtil.isArabic(this);

        String type = item.type == null ? "text" : item.type;

        String title;
        String body;
        String urlToOpen = null;

        switch (type) {
            case "link":
                title = getString(R.string.type_link);
                body = ar ? safe(item.linkArabic) : safe(item.linkEnglish);
                urlToOpen = body;
                break;
            case "video":
                title = getString(R.string.type_video);
                body = ar ? safe(item.videoUrlArabic) : safe(item.videoUrlEnglish);
                urlToOpen = body;
                break;
            case "pdf":
                title = getString(R.string.type_pdf);
                body = ar ? safe(item.pdfUrlArabic) : safe(item.pdfUrlEnglish);
                urlToOpen = body;
                break;
            case "text":
            default:
                title = ar ? safe(item.arabicTitle) : safe(item.englishTitle);
                body = ar ? safe(item.arabicBody) : safe(item.englishBody);
                urlToOpen = safe(item.mediaUrl);
                break;
        }

        contentDetailBinding.tvTitle.setText(title.isEmpty() ? getString(R.string.title_content_details) : title);
        contentDetailBinding.tvBody.setText(body.isEmpty() ? "" : body);

        if (urlToOpen != null && !urlToOpen.trim().isEmpty()) {
            final String finalUrl = urlToOpen.trim();
            contentDetailBinding.btnOpen.setVisibility(View.VISIBLE);
            contentDetailBinding.btnOpen.setOnClickListener(v -> openUrl(finalUrl));
        } else {
            contentDetailBinding.btnOpen.setVisibility(View.GONE);
        }
    }

    private void openUrl(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
