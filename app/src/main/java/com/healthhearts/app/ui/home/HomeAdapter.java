package com.healthhearts.app.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.healthhearts.app.R;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private final List<HomeSection> sections;
    private final OnSectionClickListener listener;

    public HomeAdapter(List<HomeSection> sections, OnSectionClickListener listener) {
        this.sections = sections;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeSection section = sections.get(position);
        holder.titleText.setText(section.getTitle());
        holder.iconView.setImageResource(section.getIconRes());
        holder.backgroundContainer.setBackgroundResource(section.getBackgroundDrawableRes());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSectionClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public interface OnSectionClickListener {
        void onSectionClicked(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView titleText;
        final ImageView iconView;
        final View backgroundContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.txtTitle);
            iconView = itemView.findViewById(R.id.icon);
            backgroundContainer = itemView.findViewById(R.id.bgContainer);
        }
    }
}
