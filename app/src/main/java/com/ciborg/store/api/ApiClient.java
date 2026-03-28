package com.ciborg.store.api;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiClient {

    private static final String BASE_URL = "https://ciborg-38c32630.base44.app";
    private static final String APP_ID = "69c5ff3252a19cba38c32630";
    private static final String TAG = "ApiClient";

    public static JSONArray getApps() throws Exception {
        URL url = new URL(BASE_URL + "/api/entities/App?filter={\"status\":\"published\"}");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("x-app-id", APP_ID);
        conn.setRequestProperty("Content-Type", "application/json");
        return new JSONArray(readResponse(conn));
    }

    public static JSONArray getAllApps() throws Exception {
        URL url = new URL(BASE_URL + "/api/entities/App");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("x-app-id", APP_ID);
        conn.setRequestProperty("Content-Type", "application/json");
        return new JSONArray(readResponse(conn));
    }

    public static JSONObject createApp(JSONObject data) throws Exception {
        URL url = new URL(BASE_URL + "/api/entities/App");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("x-app-id", APP_ID);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(data.toString().getBytes(StandardCharsets.UTF_8));
        }
        return new JSONObject(readResponse(conn));
    }

    public static JSONObject createBuild(JSONObject data) throws Exception {
        URL url = new URL(BASE_URL + "/api/entities/Build");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("x-app-id", APP_ID);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(data.toString().getBytes(StandardCharsets.UTF_8));
        }
        return new JSONObject(readResponse(conn));
    }

    public static JSONObject triggerBuild(String buildId, String appId, String zipUrl) throws Exception {
        URL url = new URL(BASE_URL + "/functions/triggerBuild");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("x-app-id", APP_ID);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        JSONObject body = new JSONObject();
        body.put("build_id", buildId);
        body.put("app_id", appId);
        body.put("zip_url", zipUrl);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }
        return new JSONObject(readResponse(conn));
    }

    public static JSONObject checkBuild(String buildId) throws Exception {
        URL url = new URL(BASE_URL + "/functions/checkBuild");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("x-app-id", APP_ID);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        JSONObject body = new JSONObject();
        body.put("build_id", buildId);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }
        return new JSONObject(readResponse(conn));
    }

    public static String uploadZip(File file) throws Exception {
        String boundary = "----CiborgBoundary" + System.currentTimeMillis();
        URL url = new URL(BASE_URL + "/api/upload");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("x-app-id", APP_ID);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setDoOutput(true);

        try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
            dos.writeBytes("--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n");
            dos.writeBytes("Content-Type: application/zip\r\n\r\n");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
            fis.close();
            dos.writeBytes("\r\n--" + boundary + "--\r\n");
        }

        JSONObject response = new JSONObject(readResponse(conn));
        return response.optString("url");
    }

    private static String readResponse(HttpURLConnection conn) throws Exception {
        InputStream is;
        try {
            is = conn.getInputStream();
        } catch (IOException e) {
            is = conn.getErrorStream();
        }
        if (is == null) return "{}";
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();
        return sb.toString();
    }
}
