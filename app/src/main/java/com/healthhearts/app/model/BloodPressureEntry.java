package com.healthhearts.app.model;

import com.google.firebase.firestore.DocumentId;

public class BloodPressureEntry {
    @DocumentId
    public String id;
    public String uid;
    public long timestamp;
    public String value;
    public String notes;

    public BloodPressureEntry() {
    }
}
