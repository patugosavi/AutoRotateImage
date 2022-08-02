package com.example.autorotateimage.uploadimage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageModel {

    @SerializedName("error")
    @Expose
    private  boolean  error;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("message")
    @Expose
    private String message;

    public ImageModel(boolean error, String url, String name) {
        this.error = error;
        this.url = url;
        this.name = name;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "error=" + error +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
