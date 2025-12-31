package com.healthhearts.app.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.healthhearts.app.R;
import com.healthhearts.app.ui.auth.LoginActivity;
import com.healthhearts.app.util.LocaleUtil;

public abstract class BaseMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LocaleUtil.applySavedLocale(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        setupLanguageToggle(menu);
        return true;
    }

    private void setupLanguageToggle(Menu menu) {
        MenuItem languageItem = menu.findItem(R.id.action_language);
        if (languageItem == null) return;

        View actionView = languageItem.getActionView();
        if (actionView == null) return;

        MaterialButtonToggleGroup group = actionView.findViewById(R.id.languageToggleGroup);
        MaterialButton btnEn = actionView.findViewById(R.id.btnLangEn);
        MaterialButton btnAr = actionView.findViewById(R.id.btnLangAr);

        if (group == null || btnEn == null || btnAr == null) return;

        boolean isArabic = LocaleUtil.isArabic(this);
        group.check(isArabic ? R.id.btnLangAr : R.id.btnLangEn);

        final boolean[] changingLanguage = {false};

        group.addOnButtonCheckedListener((toggleGroup, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (changingLanguage[0]) return;

            String target = null;
            if (checkedId == R.id.btnLangEn) target = "en";
            else if (checkedId == R.id.btnLangAr) target = "ar";
            if (target == null) return;

            boolean isArNow = LocaleUtil.isArabic(this);
            if (("ar".equals(target) && isArNow) || ("en".equals(target) && !isArNow)) return;

            changingLanguage[0] = true;
            group.setEnabled(false);

            LocaleUtil.setAppLanguage(this, target);

            group.post(this::recreate);
        });

        actionView.setOnClickListener(v -> {
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            onLogoutClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onLogoutClicked() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
