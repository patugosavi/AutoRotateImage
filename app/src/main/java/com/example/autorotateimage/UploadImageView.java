package com.example.autorotateimage;

import com.example.autorotateimage.uploadimage.ImageModel;

public interface UploadImageView {

    void showProgressBar();
    void hideProgressBar();
    void onFailure(String s);


    void uploadimage(ImageModel response);
}
