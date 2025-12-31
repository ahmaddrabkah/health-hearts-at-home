package com.healthhearts.app.model;

import com.google.firebase.firestore.DocumentId;

public class ProviderContact {
    @DocumentId
    public String id;
    public String uid;
    public String name;
    public String phone;
    public String email;
    public String website;
    public String address;
    public String notes;

    public ProviderContact() {
    }
}
