package com.healthhearts.app.ui.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.healthhearts.app.databinding.ActivityFirebaseConfigErrorBinding;

public class FirebaseConfigErrorActivity extends AppCompatActivity {

    private ActivityFirebaseConfigErrorBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFirebaseConfigErrorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnOpenGuide.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://firebase.google.com/docs/android/setup"));
            startActivity(i);
        });
    }
}
