package com.ciborg.store.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ciborg.store.R;
import com.ciborg.store.api.ApiClient;
import com.ciborg.store.adapters.BuildAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private static final int PICK_ZIP = 100;
    private RecyclerView recyclerBuilds;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private Button btnPickZip, btnBuild;
    private EditText etAppName, etPackage, etVersion, etDeveloper, etDescription;
    private Spinner spinnerCategory;
    private Uri selectedZipUri;
    private List<JSONObject> buildList = new ArrayList<>();
    private BuildAdapter buildAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("⚙️ Dashboard");
        }

        etAppName = findViewById(R.id.et_app_name);
        etPackage = findViewById(R.id.et_package);
        etVersion = findViewById(R.id.et_version);
        etDeveloper = findViewById(R.id.et_developer);
        etDescription = findViewById(R.id.et_description);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnPickZip = findViewById(R.id.btn_pick_zip);
        btnBuild = findViewById(R.id.btn_build);
        progressBar = findViewById(R.id.progress_bar);
        tvStatus = findViewById(R.id.tv_status);
        recyclerBuilds = findViewById(R.id.recycler_builds);

        String[] categories = {"Games", "Productivity", "Social", "Entertainment", "Tools", "Education", "Other"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        recyclerBuilds.setLayoutManager(new LinearLayoutManager(this));
        buildAdapter = new BuildAdapter(buildList);
        recyclerBuilds.setAdapter(buildAdapter);

        btnPickZip.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/zip");
            startActivityForResult(intent, PICK_ZIP);
        });

        btnBuild.setOnClickListener(v -> startBuild());

        loadBuilds();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_ZIP && resultCode == Activity.RESULT_OK && data != null) {
            selectedZipUri = data.getData();
            String name = getFileName(selectedZipUri);
            btnPickZip.setText("📦 " + (name != null ? name : "ZIP selecionado"));
        }
    }

    private String getFileName(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            String name = idx >= 0 ? cursor.getString(idx) : null;
            cursor.close();
            return name;
        }
        return null;
    }

    private void startBuild() {
        String name = etAppName.getText().toString().trim();
        if (name.isEmpty()) { Toast.makeText(this, "Nome do app é obrigatório", Toast.LENGTH_SHORT).show(); return; }
        if (selectedZipUri == null) { Toast.makeText(this, "Selecione o ZIP do projeto", Toast.LENGTH_SHORT).show(); return; }

        progressBar.setVisibility(View.VISIBLE);
        btnBuild.setEnabled(false);
        tvStatus.setText("⏳ Criando app...");

        String appName = name;
        String pkg = etPackage.getText().toString().trim();
        String version = etVersion.getText().toString().trim().isEmpty() ? "1.0.0" : etVersion.getText().toString().trim();
        String developer = etDeveloper.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // 1. Copiar ZIP pra arquivo temp
                    publishProgress("📦 Copiando ZIP...");
                    InputStream is = getContentResolver().openInputStream(selectedZipUri);
                    File tempFile = new File(getCacheDir(), "upload.zip");
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = is.read(buf)) != -1) fos.write(buf, 0, len);
                    fos.close(); is.close();

                    // 2. Criar App
                    publishProgress("🆕 Criando app na plataforma...");
                    JSONObject appData = new JSONObject();
                    appData.put("name", appName);
                    appData.put("package_name", pkg);
                    appData.put("version", version);
                    appData.put("developer", developer);
                    appData.put("description", description);
                    appData.put("category", category);
                    appData.put("status", "draft");
                    appData.put("downloads", 0);
                    JSONObject createdApp = ApiClient.createApp(appData);
                    String appId = createdApp.getString("id");

                    // 3. Upload ZIP
                    publishProgress("☁️ Fazendo upload do ZIP...");
                    String zipUrl = ApiClient.uploadZip(tempFile);
                    if (zipUrl == null || zipUrl.isEmpty()) return "Erro no upload do ZIP";

                    // 4. Criar Build
                    publishProgress("⚙️ Criando build...");
                    JSONObject buildData = new JSONObject();
                    buildData.put("app_id", appId);
                    buildData.put("zip_url", zipUrl);
                    buildData.put("status", "pending");
                    buildData.put("version", version);
                    JSONObject createdBuild = ApiClient.createBuild(buildData);
                    String buildId = createdBuild.getString("id");

                    // 5. Disparar build
                    publishProgress("🚀 Disparando GitHub Actions...");
                    JSONObject result = ApiClient.triggerBuild(buildId, appId, zipUrl);
                    if (result.has("error")) return "Erro: " + result.getString("error");

                    return "✅ Build iniciado! Repo: " + result.optString("repo");

                } catch (Exception e) {
                    return "Erro: " + e.getMessage();
                }
            }

            @Override
            protected void onProgressUpdate(String... values) {
                tvStatus.setText(values[0]);
            }

            @Override
            protected void onPostExecute(String result) {
                progressBar.setVisibility(View.GONE);
                btnBuild.setEnabled(true);
                tvStatus.setText(result);
                loadBuilds();
            }
        }.execute();
    }

    private void loadBuilds() {
        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... voids) {
                try { return ApiClient.getAllApps(); } catch (Exception e) { return null; }
            }
            @Override
            protected void onPostExecute(JSONArray result) {
                if (result == null) return;
                buildList.clear();
                for (int i = 0; i < result.length(); i++) {
                    try { buildList.add(result.getJSONObject(i)); } catch (Exception ignored) {}
                }
                buildAdapter.notifyDataSetChanged();
            }
        }.execute();
    }
}
