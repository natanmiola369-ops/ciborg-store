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

public class BuildAdapter extends RecyclerView.Adapter<BuildAdapter.ViewHolder> {

    private final List<JSONObject> items;

    public BuildAdapter(List<JSONObject> items) { this.items = items; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_build, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject item = items.get(position);
        holder.tvName.setText(item.optString("name", "App"));
        String status = item.optString("status", "draft");
        String icon = status.equals("published") ? "✅" : status.equals("building") ? "⚙️" : status.equals("failed") ? "❌" : "📝";
        holder.tvStatus.setText(icon + " " + status);
        holder.tvVersion.setText("v" + item.optString("version", "1.0"));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus, tvVersion;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_build_name);
            tvStatus = v.findViewById(R.id.tv_build_status);
            tvVersion = v.findViewById(R.id.tv_build_version);
        }
    }
}
