package com.healthhearts.app.ui.contact;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.healthhearts.app.databinding.ItemContactRowBinding;

import java.util.ArrayList;
import java.util.List;

public class ContactRowAdapter extends RecyclerView.Adapter<ContactRowAdapter.VH> {

    private final List<Row> rows = new ArrayList<>();
    private final Listener listener;

    public ContactRowAdapter(Listener l) {
        listener = l;
    }

    public void setRows(List<Row> list) {
        rows.clear();
        if (list != null) rows.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContactRowBinding binding = ItemContactRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH viewHolder, int position) {
        Row row = rows.get(position);
        viewHolder.binding.tvTitle.setText(row.title == null ? "" : row.title);
        viewHolder.binding.tvValue.setText(row.value == null ? "" : row.value);

        viewHolder.binding.getRoot().setOnClickListener(v -> listener.onClick(row));
        viewHolder.binding.getRoot().setOnLongClickListener(v -> {
            listener.onLongPress(row);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    public interface Listener {
        void onClick(Row row);

        void onLongPress(Row row);
    }

    public static class Row {
        public String id;
        public String title;
        public String value;
        public String kind;
        public String source;
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemContactRowBinding binding;

        VH(ItemContactRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
