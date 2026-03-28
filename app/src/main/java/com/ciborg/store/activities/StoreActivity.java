package com.ciborg.store.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ciborg.store.R;
import com.ciborg.store.api.ApiClient;
import com.ciborg.store.adapters.AppAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;
    private AppAdapter adapter;
    private List<JSONObject> appList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("🏪 Ciborg Store");
        }

        recyclerView = findViewById(R.id.recycler_apps);
        progressBar = findViewById(R.id.progress_bar);
        emptyText = findViewById(R.id.empty_text);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new AppAdapter(appList, app -> {
            Intent intent = new Intent(this, AppDetailActivity.class);
            intent.putExtra("app_json", app.toString());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadApps();
    }

    private void loadApps() {
        progressBar.setVisibility(View.VISIBLE);
        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... voids) {
                try {
                    return ApiClient.getApps();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONArray result) {
                progressBar.setVisibility(View.GONE);
                if (result == null || result.length() == 0) {
                    emptyText.setVisibility(View.VISIBLE);
                    return;
                }
                appList.clear();
                for (int i = 0; i < result.length(); i++) {
                    try { appList.add(result.getJSONObject(i)); } catch (Exception ignored) {}
                }
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }
}
