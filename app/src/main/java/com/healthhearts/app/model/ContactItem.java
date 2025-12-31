package com.healthhearts.app.model;

import com.google.firebase.firestore.DocumentId;

public class ContactItem {
    @DocumentId
    public String id;
    public String type;
    public String englishTitle;
    public String arabicTitle;
    public String kind;
    public String value;

    public long updatedAt;
    public long createdAt;

    public ContactItem() {
    }
}
