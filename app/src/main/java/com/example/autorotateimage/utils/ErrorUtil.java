package com.example.autorotateimage.utils;

import android.util.Log;

import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class ErrorUtil {


    public static String onError(Throwable e) {
        String error = "";
        if (e instanceof HttpException) {
            ResponseBody responseBody = ((HttpException)e).response().errorBody();
//            error=getErrorMessage(responseBody);
            String err=getErrorMessage(responseBody);
            error = "Technical Problem !!!";               // response null & procedure err
        } else if (e instanceof SocketTimeoutException) {
            error = "Oops something went wrong Please Retry again !!";    // solve internet
        } else if (e instanceof IOException) {
            error = "No Internet Available Please Connect Internet !!";      // internet not
        }else if (e instanceof IllegalStateException) {
            error = "Conversion Error !!";                              // conversion err
        }else if (e instanceof JsonSyntaxException) {
            error = "Something Went Wrong API is not responding properly !!";
//            error = "Something Went Wrong Please contact to Technical Team !!";
        } else {
            error = "Please contact to Technical Support Team !!";
        }

        return error;

    }

    private static String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());

            Log.d("abc","cbd"+responseBody);
            return jsonObject.getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
