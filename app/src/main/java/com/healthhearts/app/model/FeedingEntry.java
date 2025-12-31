package com.healthhearts.app.model;

import com.google.firebase.firestore.DocumentId;

public class FeedingEntry {
    @DocumentId
    public String id;
    public String uid;
    public long timestamp;
    public int amountMl;
    public String method;
    public String notes;

    public FeedingEntry() {
    }
}
