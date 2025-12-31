package com.healthhearts.app.ui.support;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.healthhearts.app.R;
import com.healthhearts.app.model.PatientStory;

import java.util.ArrayList;
import java.util.List;

public class PatientStoryAdapter extends RecyclerView.Adapter<PatientStoryAdapter.VH> {

    private final boolean isArabic;
    private final OnOpenUrl onOpenUrl;
    private final List<PatientStory> items = new ArrayList<>();
    public PatientStoryAdapter(boolean isArabic, OnOpenUrl onOpenUrl) {
        this.isArabic = isArabic;
        this.onOpenUrl = onOpenUrl;
    }

    public void setItems(List<PatientStory> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PatientStory s = items.get(position);

        String title = isArabic ? s.titleArabic : s.titleEnglish;
        if (title == null || title.trim().isEmpty()) {
            title = "(No title)";
        }

        String type = (s.type == null) ? "text" : s.type.trim().toLowerCase();

        String subtitle;
        if ("video".equals(type)) {
            subtitle = (s.videoUrl == null) ? "" : s.videoUrl;
            h.imgType.setImageResource(android.R.drawable.ic_media_play);
        } else {
            String body = isArabic ? s.textArabic : s.textEnglish;
            if (body == null) body = "";
            body = body.trim();
            subtitle = body.length() > 60 ? body.substring(0, 60) + "…" : body;
            h.imgType.setImageResource(android.R.drawable.ic_menu_edit);
        }

        h.tvTitle.setText(title);
        h.tvSubtitle.setText(subtitle);

        final String finalTittle = title;

        h.itemView.setOnClickListener(v -> {
            if ("video".equals(type)) {
                if (onOpenUrl != null && s.videoUrl != null && !s.videoUrl.trim().isEmpty()) {
                    onOpenUrl.open(s.videoUrl);
                }
            } else {
                String body = isArabic ? s.textArabic : s.textEnglish;
                if (body == null) body = "";
                new MaterialAlertDialogBuilder(v.getContext())
                        .setTitle(finalTittle)
                        .setMessage(body)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnOpenUrl {
        void open(String url);
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgType;
        TextView tvTitle, tvSubtitle;

        VH(@NonNull View itemView) {
            super(itemView);
            imgType = itemView.findViewById(R.id.imgType);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }
}