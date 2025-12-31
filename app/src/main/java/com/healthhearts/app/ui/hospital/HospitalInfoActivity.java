package com.healthhearts.app.ui.hospital;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.healthhearts.app.R;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivityHospitalInfoBinding;
import com.healthhearts.app.ui.common.BaseMenuActivity;
import com.healthhearts.app.util.LocaleUtil;
import com.healthhearts.app.util.Prefs;

public class HospitalInfoActivity extends BaseMenuActivity {

    private ActivityHospitalInfoBinding hospitalInfoBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hospitalInfoBinding = ActivityHospitalInfoBinding.inflate(getLayoutInflater());
        setContentView(hospitalInfoBinding.getRoot());

        setSupportActionBar(hospitalInfoBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        hospitalInfoBinding.toolbar.setNavigationOnClickListener(v -> finish());
        hospitalInfoBinding.toolbar.setTitle(getString(R.string.hospital_information));

        hospitalInfoBinding.btnEdit.setVisibility(Prefs.isAdmin(this) ? View.VISIBLE : View.GONE);
        hospitalInfoBinding.btnEdit.setOnClickListener(v ->
                startActivity(new Intent(this, HospitalInfoEditActivity.class)));

        load();
        bindButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        FirestoreRepo.hospitalInfo().get().addOnSuccessListener(doc -> {
            if (!doc.exists()) return;

            boolean ar = LocaleUtil.isArabic(this);

            String website = doc.getString("websiteUrl");
            String showers = doc.getString("showersUrl");
            String mapQuery = doc.getString("mapQuery");
            String hours = ar ? doc.getString("workingHoursAr") : doc.getString("workingHoursEn");

            hospitalInfoBinding.tvWorkingHours.setText(hours == null ? "—" : hours);
            hospitalInfoBinding.tvWebsite.setText(website == null ? "—" : website);
            hospitalInfoBinding.tvShowers.setText(showers == null ? "—" : showers);

            hospitalInfoBinding.btnOpenWebsite.setTag(website);
            hospitalInfoBinding.btnOpenShowers.setTag(showers);
            hospitalInfoBinding.btnOpenMap.setTag(mapQuery);
        });
    }

    private void bindButtons() {
        hospitalInfoBinding.btnOpenWebsite.setOnClickListener(v -> openUrl((String) v.getTag()));
        hospitalInfoBinding.btnOpenShowers.setOnClickListener(v -> openUrl((String) v.getTag()));
        hospitalInfoBinding.btnOpenMap.setOnClickListener(v -> openMap((String) v.getTag()));
    }

    private void openUrl(String url) {
        if (url == null || url.trim().isEmpty()) return;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }

    private void openMap(String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }

        value = value.trim();

        if (value.startsWith("http://") || value.startsWith("https://")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(value)));
            return;
        }

        if (value.startsWith("geo:")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(value)));
            return;
        }

        Uri uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(value));
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

}
