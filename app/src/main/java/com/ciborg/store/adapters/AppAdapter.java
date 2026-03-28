package com.ciborg.store.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ciborg.store.R;
import org.json.JSONObject;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    public interface OnAppClick { void onClick(JSONObject app); }

    private final List<JSONObject> apps;
    private final OnAppClick listener;

    public AppAdapter(List<JSONObject> apps, OnAppClick listener) {
        this.apps = apps;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject app = apps.get(position);
        holder.tvName.setText(app.optString("name", "App"));
        holder.tvDeveloper.setText(app.optString("developer", "Desconhecido"));
        holder.tvCategory.setText(app.optString("category", ""));
        holder.tvVersion.setText("v" + app.optString("version", "1.0"));
        holder.itemView.setOnClickListener(v -> listener.onClick(app));
    }

    @Override
    public int getItemCount() { return apps.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDeveloper, tvCategory, tvVersion;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_app_name);
            tvDeveloper = v.findViewById(R.id.tv_app_developer);
            tvCategory = v.findViewById(R.id.tv_app_category);
            tvVersion = v.findViewById(R.id.tv_app_version);
        }
    }
}
