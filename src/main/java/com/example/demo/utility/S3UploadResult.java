package com.example.demo.utility;

public class S3UploadResult {
    private String key;
    private String url;

    public S3UploadResult() {}

    public S3UploadResult(String key, String url) {
        this.key = key;
        this.url = url;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
