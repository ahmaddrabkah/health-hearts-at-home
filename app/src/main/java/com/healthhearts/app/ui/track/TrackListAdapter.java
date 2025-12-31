package com.healthhearts.app.ui.track;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.healthhearts.app.databinding.ItemTrackRowBinding;

import java.util.ArrayList;
import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.VH> {

    private final List<Row> rows = new ArrayList<>();

    public void setRows(List<Row> list) {
        rows.clear();
        if (list != null) rows.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrackRowBinding b = ItemTrackRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Row r = rows.get(position);
        h.b.tvTop.setText(r.top);
        h.b.tvBottom.setText(r.bottom);
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    public static class Row {
        public final String top;
        public final String bottom;

        public Row(String t, String b) {
            top = t;
            bottom = b;
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemTrackRowBinding b;

        VH(ItemTrackRowBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
