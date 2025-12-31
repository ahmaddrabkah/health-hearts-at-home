package com.healthhearts.app.ui.section;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.healthhearts.app.R;
import com.healthhearts.app.model.ContentItem;
import com.healthhearts.app.util.LocaleUtil;

import java.util.ArrayList;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.VH> {

    private final List<ContentItem> items = new ArrayList<>();
    private final Listener listener;

    public ContentAdapter(Listener listener) {
        this.listener = listener;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    public void setItems(List<ContentItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
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
        ContentItem item = items.get(position);
        boolean ar = LocaleUtil.isArabic();
        String type = item.type == null ? "text" : item.type;

        String title;
        String subtitle;
        int icon;

        switch (type) {
            case "link":
                title = safe(ar ? item.arabicTitle : item.englishTitle);
                subtitle = safe(ar ? item.linkArabic : item.linkEnglish);
                icon = android.R.drawable.ic_menu_share;
                break;
            case "video":
                title = safe(ar ? item.arabicTitle : item.englishTitle);
                subtitle = safe(ar ? item.videoUrlArabic : item.videoUrlEnglish);
                icon = android.R.drawable.ic_media_play;
                break;
            case "pdf":
                title = safe(ar ? item.arabicTitle : item.englishTitle);
                subtitle = safe(ar ? item.pdfUrlArabic : item.pdfUrlEnglish);
                icon = android.R.drawable.ic_menu_view;
                break;
            case "text":
            default:
                title = safe(ar ? item.arabicTitle : item.englishTitle);
                subtitle = safe(ar ? item.arabicBody : item.englishBody);
                icon = android.R.drawable.ic_menu_edit;
                break;
        }

        if (title.isEmpty()) title = h.itemView.getContext().getString(R.string.no_items);

        h.tvTitle.setText(title);
        h.tvSubtitle.setText(subtitle);
        h.imgType.setImageResource(icon);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface Listener {
        void onClick(ContentItem item);

        void onLongPress(ContentItem item);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvSubtitle;
        ImageView imgType;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            imgType = itemView.findViewById(R.id.imgType);
        }
    }
}
