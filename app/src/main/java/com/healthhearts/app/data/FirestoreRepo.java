package com.healthhearts.app.data;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public final class FirestoreRepo {
    public static final String DOC_MAIN = "main";
    public static final String COL_USERS = "users";
    public static final String COL_SECTIONS = "sections";
    public static final String COL_CONTENT = "content";
    public static final String COL_HOSPITAL_INFO = "hospitalInfo";
    public static final String COL_CAREGIVER_SUPPORT = "caregiverSupport";
    public static final String COL_CAREGIVER_SUPPORT_GROUPS = "caregiverSupportGroups";
    public static final String COL_CAREGIVER_CONTACTS = "caregiverContacts";
    public static final String COL_CAREGIVER_PATIENT_STORIES = "caregiverPatientStories";
    public static final String COL_SPIRITUAL = "spiritual";
    public static final String COL_SPIRITUAL_DEVOTIONALS = "spiritualDevotionals";
    public static final String COL_SPIRITUAL_RESOURCES = "spiritualResources";
    public static final String COL_FEEDINGS = "feedings";
    public static final String COL_OXYGEN = "oxygen";
    public static final String COL_WEIGHTS = "weights";
    public static final String COL_BLOOD_PRESSURE = "bloodPressure";
    public static final String COL_CONTACTS = "contacts";
    public static final String COL_PROVIDERS = "providers";

    private FirestoreRepo() {
    }

    public static FirebaseFirestore db() {
        return FirebaseFirestore.getInstance();
    }

    public static FirebaseUser user() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String uid() {
        FirebaseUser u = user();
        return u == null ? null : u.getUid();
    }

    public static DocumentReference userDoc(@NonNull String uid) {
        return db().collection(COL_USERS).document(uid);
    }

    public static Task<Void> ensureUserDoc(String uid, String name, String email) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name == null ? "" : name);
        map.put("email", email == null ? "" : email);
        map.put("role", "user");
        map.put("createdAt", System.currentTimeMillis());
        return userDoc(uid).set(map);
    }

    public static CollectionReference content() {
        return db().collection(COL_CONTENT);
    }

    public static CollectionReference content(String sectionId) {
        return db().collection(COL_SECTIONS)
                .document(sectionId)
                .collection(COL_CONTENT);
    }

    public static DocumentReference hospitalInfo() {
        return db().collection(COL_HOSPITAL_INFO).document(DOC_MAIN);
    }

    public static DocumentReference caregiverRoot() {
        return db().collection(COL_CAREGIVER_SUPPORT).document(DOC_MAIN);
    }

    public static CollectionReference caregiverSupportGroups() {
        return caregiverRoot().collection(COL_CAREGIVER_SUPPORT_GROUPS);
    }

    public static CollectionReference caregiverContacts() {
        return caregiverRoot().collection(COL_CAREGIVER_CONTACTS);
    }

    public static CollectionReference caregiverStories() {
        return caregiverRoot().collection(COL_CAREGIVER_PATIENT_STORIES);
    }

    public static DocumentReference spiritualRoot() {
        return db().collection(COL_SPIRITUAL).document(DOC_MAIN);
    }

    public static CollectionReference spiritualDevotionals() {
        return spiritualRoot().collection(COL_SPIRITUAL_DEVOTIONALS);
    }

    public static CollectionReference spiritualResources() {
        return spiritualRoot().collection(COL_SPIRITUAL_RESOURCES);
    }

    public static CollectionReference feedings() {
        return db().collection(COL_FEEDINGS);
    }

    public static CollectionReference oxygen() {
        return db().collection(COL_OXYGEN);
    }

    public static CollectionReference weights() {
        return db().collection(COL_WEIGHTS);
    }

    public static CollectionReference bloodPressure() {
        return db().collection(COL_BLOOD_PRESSURE);
    }

    public static CollectionReference contacts() {
        return db().collection(COL_CONTACTS);
    }

    public static CollectionReference providers() {
        return db().collection(COL_PROVIDERS);
    }
}
