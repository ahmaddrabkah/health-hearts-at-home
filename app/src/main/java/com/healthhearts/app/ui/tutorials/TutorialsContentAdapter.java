package com.healthhearts.app.ui.tutorials;

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

public class TutorialsContentAdapter extends RecyclerView.Adapter<TutorialsContentAdapter.VH> {

    private final List<ContentItem> items = new ArrayList<>();
    private final ClickListener click;
    private final LongListener longPress;
    public TutorialsContentAdapter(ClickListener click, LongListener longPress) {
        this.click = click;
        this.longPress = longPress;
    }

    public void setItems(List<ContentItem> list) {
        items.clear();
        if (list != null) items.addAll(list);
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

        boolean isAr = LocaleUtil.isArabic(h.itemView.getContext());

        String title = isAr ? safe(item.arabicTitle) : safe(item.englishTitle);
        if (title.isEmpty()) title = isAr ? "بدون عنوان" : "(No title)";

        String type = safe(item.type).toLowerCase().trim();
        String subtitle = buildSubtitle(isAr, item, type);

        h.tvTitle.setText(title);
        h.tvSubtitle.setText(subtitle);

        if ("video".equals(type)) h.imgType.setImageResource(android.R.drawable.ic_media_play);
        else if ("pdf".equals(type)) h.imgType.setImageResource(android.R.drawable.ic_menu_view);
        else if ("link".equals(type)) h.imgType.setImageResource(android.R.drawable.ic_menu_share);
        else h.imgType.setImageResource(android.R.drawable.ic_menu_edit);

        h.itemView.setOnClickListener(v -> {
            if (click != null && item.id != null) click.onClick(item.id);
        });

        h.itemView.setOnLongClickListener(v -> {
            if (longPress != null) longPress.onLongPress(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String buildSubtitle(boolean isAr, ContentItem item, String type) {
        if ("video".equals(type)) {
            return safe(isAr ? item.videoUrlArabic : item.videoUrlEnglish);
        }
        if ("pdf".equals(type)) {
            return safe(isAr ? item.pdfUrlArabic : item.pdfUrlEnglish);
        }
        if ("link".equals(type)) {
            return safe(isAr ? item.linkArabic : item.linkEnglish);
        }

        String body = safe(isAr ? item.arabicBody : item.englishBody);
        if (body.length() > 60) body = body.substring(0, 60) + "…";
        return body;
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    public interface ClickListener {
        void onClick(String contentId);
    }

    public interface LongListener {
        void onLongPress(ContentItem item);
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
