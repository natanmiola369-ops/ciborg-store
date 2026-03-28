package com.ciborg.store.models;

public class Build {
    private String id;
    private String appId;
    private String zipUrl;
    private String apkUrl;
    private String status;
    private String githubRepo;
    private String githubRunId;
    private String logs;
    private String errorMessage;
    private String version;

    public Build() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getZipUrl() { return zipUrl; }
    public void setZipUrl(String zipUrl) { this.zipUrl = zipUrl; }

    public String getApkUrl() { return apkUrl; }
    public void setApkUrl(String apkUrl) { this.apkUrl = apkUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGithubRepo() { return githubRepo; }
    public void setGithubRepo(String githubRepo) { this.githubRepo = githubRepo; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
}
