package com.example.autorotateimage.network_service;



import com.example.autorotateimage.uploadimage.ImageResponse;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {


    String B_URL = "Visit/api/";


    //upload search engine image on our server
    @Multipart
    @POST(B_URL + "upload/search/engine/images")
    Observable<ImageResponse> uploadimage(@Part MultipartBody.Part body);
}
