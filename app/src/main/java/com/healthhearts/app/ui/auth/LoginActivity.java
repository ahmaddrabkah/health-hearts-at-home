package com.healthhearts.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivityLoginBinding;
import com.healthhearts.app.ui.home.HomeActivity;
import com.healthhearts.app.util.Prefs;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding loginBinding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        loginBinding.btnLogin.setOnClickListener(v -> doLogin());
        loginBinding.btnGoSignup.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }

    private void setLoading(boolean loading) {
        loginBinding.progress.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
        loginBinding.btnLogin.setEnabled(!loading);
        loginBinding.btnGoSignup.setEnabled(!loading);
    }

    private void doLogin() {
        String email = loginBinding.etEmail.getText() == null ? "" : loginBinding.etEmail.getText().toString().trim();
        String pass = loginBinding.etPassword.getText() == null ? "" : loginBinding.etPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(res -> fetchRoleAndGoHome())
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void fetchRoleAndGoHome() {
        FirebaseUser u = firebaseAuth.getCurrentUser();
        if (u == null) {
            setLoading(false);
            return;
        }
        FirestoreRepo.userDoc(u.getUid()).get()
                .addOnSuccessListener(this::onUserDoc)
                .addOnFailureListener(e -> {
                    Prefs.setRole(this, "user");
                    goHome();
                });
    }

    private void onUserDoc(DocumentSnapshot snap) {
        String role = "user";
        if (snap != null && snap.exists()) {
            Object r = snap.get("role");
            if (r != null) role = String.valueOf(r);
        } else {
            FirebaseUser u = firebaseAuth.getCurrentUser();
            if (u != null) {
                FirestoreRepo.ensureUserDoc(u.getUid(), u.getDisplayName(), u.getEmail());
            }
        }
        Prefs.setRole(this, role);
        goHome();
    }

    private void goHome() {
        setLoading(false);
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
