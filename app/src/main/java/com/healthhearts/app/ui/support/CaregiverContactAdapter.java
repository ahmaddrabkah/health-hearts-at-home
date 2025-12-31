package com.healthhearts.app.ui.support;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.healthhearts.app.R;
import com.healthhearts.app.model.CaregiverContact;

import java.util.ArrayList;
import java.util.List;

public class CaregiverContactAdapter extends RecyclerView.Adapter<CaregiverContactAdapter.VH> {

    private final boolean isArabic;
    private final OnOpenUrl onOpenUrl;
    private final OnOpenEmail onOpenEmail;
    private final OnOpenPhone onOpenPhone;
    private final List<CaregiverContact> items = new ArrayList<>();

    public CaregiverContactAdapter(boolean isArabic, OnOpenUrl onOpenUrl, OnOpenEmail onOpenEmail, OnOpenPhone onOpenPhone) {
        this.isArabic = isArabic;
        this.onOpenUrl = onOpenUrl;
        this.onOpenEmail = onOpenEmail;
        this.onOpenPhone = onOpenPhone;
    }

    public void setItems(List<CaregiverContact> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        CaregiverContact c = items.get(position);

        String title = isArabic ? c.nameArabic : c.nameEnglish;
        if (title == null || title.trim().isEmpty()) title = "(No title)";
        h.tvTitle.setText(title);

        String website = c.website == null ? "" : c.website.trim();
        String email = c.email == null ? "" : c.email.trim();
        String phone = c.phone == null ? "" : c.phone.trim();

        String value;
        String kind;
        if (!phone.isEmpty()) {
            value = phone;
            kind = "phone";
            h.imgIcon.setImageResource(android.R.drawable.ic_menu_call);
        } else if (!email.isEmpty()) {
            value = email;
            kind = "email";
            h.imgIcon.setImageResource(android.R.drawable.ic_dialog_email);
        } else {
            value = website;
            kind = "website";
            h.imgIcon.setImageResource(android.R.drawable.ic_menu_view);
        }

        h.tvValue.setText(value);

        h.itemView.setOnClickListener(v -> {
            if ("phone".equals(kind) && onOpenPhone != null) onOpenPhone.open(value);
            else if ("email".equals(kind) && onOpenEmail != null) onOpenEmail.open(value);
            else if ("website".equals(kind) && onOpenUrl != null) onOpenUrl.open(value);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnOpenUrl {
        void open(String url);
    }

    public interface OnOpenEmail {
        void open(String email);
    }

    public interface OnOpenPhone {
        void open(String phone);
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvTitle, tvValue;

        VH(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvValue = itemView.findViewById(R.id.tvValue);
        }
    }
}