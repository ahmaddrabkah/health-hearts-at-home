package com.healthhearts.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.healthhearts.app.data.FirestoreRepo;
import com.healthhearts.app.databinding.ActivitySignupBinding;
import com.healthhearts.app.ui.home.HomeActivity;
import com.healthhearts.app.util.Prefs;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding signupBinding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(signupBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        signupBinding.btnSignup.setOnClickListener(v -> doSignup());
        signupBinding.btnGoLogin.setOnClickListener(v -> finish());
    }

    private void setLoading(boolean loading) {
        signupBinding.progress.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
        signupBinding.btnSignup.setEnabled(!loading);
        signupBinding.btnGoLogin.setEnabled(!loading);
    }

    private void doSignup() {
        String name = signupBinding.etName.getText() == null ? "" : signupBinding.etName.getText().toString().trim();
        String email = signupBinding.etEmail.getText() == null ? "" : signupBinding.etEmail.getText().toString().trim();
        String pass = signupBinding.etPassword.getText() == null ? "" : signupBinding.etPassword.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Enter name, email, password", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(res -> {
                    FirebaseUser u = firebaseAuth.getCurrentUser();
                    if (u == null) {
                        setLoading(false);
                        return;
                    }
                    FirestoreRepo.ensureUserDoc(u.getUid(), name, email)
                            .addOnSuccessListener(v -> {
                                Prefs.setRole(this, "user");
                                goHome();
                            })
                            .addOnFailureListener(e -> {
                                Prefs.setRole(this, "user");
                                goHome();
                            });
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void goHome() {
        setLoading(false);
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
