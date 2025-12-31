package com.healthhearts.app.model;

import com.google.firebase.firestore.DocumentId;

public class OxygenEntry {
    @DocumentId
    public String id;
    public String uid;
    public long timestamp;
    public int spo2;
    public String notes;

    public OxygenEntry() {
    }
}
