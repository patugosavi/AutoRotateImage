package com.example.autorotateimage.uploadimage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageResponse {

    @SerializedName("Response")
    @Expose
    private ImageModel response;

    public ImageModel  getResponse(){
        return  response;
    }

    public void setResponse(ImageModel response) {
        this.response = response;
    }
}
