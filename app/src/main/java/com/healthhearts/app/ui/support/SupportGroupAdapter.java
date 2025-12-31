package com.healthhearts.app.ui.support;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.healthhearts.app.R;
import com.healthhearts.app.model.SupportGroup;

import java.util.ArrayList;
import java.util.List;

public class SupportGroupAdapter extends RecyclerView.Adapter<SupportGroupAdapter.VH> {

    private final boolean isArabic;
    private final OnOpenUrl onOpenUrl;
    private final List<SupportGroup> items = new ArrayList<>();
    public SupportGroupAdapter(boolean isArabic, OnOpenUrl onOpenUrl) {
        this.isArabic = isArabic;
        this.onOpenUrl = onOpenUrl;
    }

    public void setItems(List<SupportGroup> data) {
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
        SupportGroup g = items.get(position);

        String title = isArabic ? g.nameArabic : g.nameEnglish;
        if (title == null || title.trim().isEmpty()) title = "(No title)";

        String sub = (g.url == null) ? "" : g.url;

        h.tvTitle.setText(title);
        h.tvSubtitle.setText(sub);

        h.imgType.setImageResource(android.R.drawable.ic_menu_share);

        h.itemView.setOnClickListener(v -> {
            if (onOpenUrl != null && g.url != null && !g.url.trim().isEmpty()) {
                onOpenUrl.open(g.url);
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