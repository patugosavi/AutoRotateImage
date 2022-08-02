package com.example.autorotateimage;

import android.util.Log;

import com.example.autorotateimage.network_service.ApiInterface;
import com.example.autorotateimage.uploadimage.ImageResponse;
import com.example.autorotateimage.utils.ErrorUtil;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadImagePresenter {
    private static final String TAG = "UploadImagePresenter";
    private UploadImageView uploadImageView;
    private ApiInterface apiInterface;
    private Disposable disposable;

    public UploadImagePresenter(UploadImageView uploadImageView, ApiInterface apiInterface) {
        this.uploadImageView = uploadImageView;
        this.apiInterface = apiInterface;
    }

    public void dispose(){
        if (disposable!=null)
            disposable.dispose();
    }

    public void uploadimage(File imageFile) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("img", imageFile.getName(), requestFile);
        Log.d("", "uploadProofImage: " + body);

        Observable<ImageResponse> observable = apiInterface.uploadimage(body);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ImageResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(ImageResponse imageResponse) {
//                        loginView.hideProgressBar();
                        uploadImageView.uploadimage(imageResponse.getResponse());
                    }

                    @Override
                    public void onError(Throwable e) {
                        uploadImageView.onFailure(ErrorUtil.onError(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
