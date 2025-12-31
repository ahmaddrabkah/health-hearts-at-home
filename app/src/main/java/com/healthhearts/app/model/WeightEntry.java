package com.healthhearts.app.model;

import com.google.firebase.firestore.DocumentId;

public class WeightEntry {
    @DocumentId
    public String id;
    public String uid;
    public long timestamp;
    public double weightKg;
    public String notes;

    public WeightEntry() {
    }
}
