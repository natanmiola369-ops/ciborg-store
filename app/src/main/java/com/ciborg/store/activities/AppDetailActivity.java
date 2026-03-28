package com.ciborg.store.activities;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.ciborg.store.R;
import org.json.JSONObject;

public class AppDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        String appJson = getIntent().getStringExtra("app_json");
        if (appJson == null) { finish(); return; }

        try {
            JSONObject app = new JSONObject(appJson);

            TextView tvName = findViewById(R.id.tv_name);
            TextView tvDeveloper = findViewById(R.id.tv_developer);
            TextView tvVersion = findViewById(R.id.tv_version);
            TextView tvCategory = findViewById(R.id.tv_category);
            TextView tvDownloads = findViewById(R.id.tv_downloads);
            TextView tvDescription = findViewById(R.id.tv_description);
            TextView tvPackage = findViewById(R.id.tv_package);
            Button btnDownload = findViewById(R.id.btn_download);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(app.optString("name", "App"));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            tvName.setText(app.optString("name", "App"));
            tvDeveloper.setText(app.optString("developer", "Desconhecido"));
            tvVersion.setText("v" + app.optString("version", "1.0"));
            tvCategory.setText(app.optString("category", "Other"));
            tvDownloads.setText(app.optInt("downloads", 0) + " downloads");
            tvDescription.setText(app.optString("description", "Sem descrição."));
            tvPackage.setText(app.optString("package_name", ""));

            String apkUrl = app.optString("apk_url", "");
            String status = app.optString("status", "");

            if ("published".equals(status) && !apkUrl.isEmpty()) {
                btnDownload.setEnabled(true);
                btnDownload.setText("⬇️ Baixar APK");
                btnDownload.setOnClickListener(v -> downloadApk(apkUrl, app.optString("name", "app")));
            } else if ("building".equals(status)) {
                btnDownload.setEnabled(false);
                btnDownload.setText("⚙️ Compilando...");
            } else {
                btnDownload.setEnabled(false);
                btnDownload.setText("🔒 Indisponível");
            }

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao carregar app", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void downloadApk(String url, String appName) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(appName + ".apk");
            request.setDescription("Baixando APK...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appName + ".apk");
            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm != null) {
                dm.enqueue(request);
                Toast.makeText(this, "Download iniciado!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro no download: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
