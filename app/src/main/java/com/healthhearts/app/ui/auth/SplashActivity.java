package com.healthhearts.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.healthhearts.app.HealthHeartsApplication;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivitySplashBinding;
import com.healthhearts.app.ui.home.HomeActivity;
import com.healthhearts.app.util.Prefs;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding splashBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(com.healthhearts.app.R.style.Theme_HealthHearts);
        super.onCreate(savedInstanceState);
        splashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(splashBinding.getRoot());

        new Handler(Looper.getMainLooper()).postDelayed(this::route, 700);
    }

    private void route() {
        if (!HealthHeartsApplication.isFirebaseConfigured()) {
            startActivity(new Intent(this, FirebaseConfigErrorActivity.class));
            finish();
            return;
        }

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        FirestoreRepo.userDoc(u.getUid()).get()
                .addOnSuccessListener(this::onUserDoc)
                .addOnFailureListener(e -> {
                    Prefs.setRole(this, "user");
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                });
    }

    private void onUserDoc(DocumentSnapshot snap) {
        String role = "user";
        if (snap != null && snap.exists()) {
            Object r = snap.get("role");
            if (r != null) role = String.valueOf(r);
        } else {
            FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
            if (u != null) {
                FirestoreRepo.ensureUserDoc(u.getUid(), u.getDisplayName(), u.getEmail());
            }
        }
        Prefs.setRole(this, role);
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
