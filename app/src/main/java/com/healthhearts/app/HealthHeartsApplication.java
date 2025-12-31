package com.healthhearts.app;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * Initializes Firebase if google-services.json is correctly configured.
 * If it's not configured yet, the app will show a friendly setup screen
 * instead of crashing with "Default FirebaseApp is not initialized".
 */
public class HealthHeartsApplication extends Application {

    private static boolean firebaseConfigured = false;

    public static boolean isFirebaseConfigured() {
        return firebaseConfigured;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseConfigured = (FirebaseApp.initializeApp(this) != null);
    }
}
