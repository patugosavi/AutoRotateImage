package com.example.autorotateimage.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Constants {



    public static final String senderId = "SRGDTG";
    public static final String smsapikey="4FpfixcOdfs0awbsyAh";

//    public static final String PAYTM_MERCHANT_ID = "TttiBs35113757886022"; //YOUR TEST MERCHANT ID
    public static final String PAYTM_MERCHANT_ID = "pLJiuP95348234743625"; //YOUR TEST MERCHANT ID

    //vmjIxF30888445391328

    public static final String UTL_PAYTM_CALLBACK = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

//    public static final String UTL_PAYTM_CALLBACK = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=order1";

    public static final String url_base = "http://www.your-domain/foldername/";

    public static final String imgpath = "https://zmedy.in/Visit/public/img/";



    public static boolean  isOnline(Context context) {
        ConnectivityManager conn=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =conn.getActiveNetworkInfo();
        return  (networkInfo!=null && networkInfo.isConnected());
    }

}
