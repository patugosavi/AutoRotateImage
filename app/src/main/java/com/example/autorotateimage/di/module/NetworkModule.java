package com.example.autorotateimage.di.module;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.autorotateimage.di.quilifier.ApplicationContext;
import com.example.autorotateimage.di.scop.ApplicationScope;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

@Module(includes = ContextModule.class)//ContextModule.class
public class NetworkModule {
    @Provides
    @ApplicationScope
    public HttpLoggingInterceptor loggingInterceptor(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                Timber.i(message);
            }
        });

        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }
    @Provides
    @ApplicationScope
    public Cache cache(File cacheFile){
        return new Cache(cacheFile,10*1000*1000);
    }

    @Provides
    @ApplicationScope
    public File cacheFile(@ApplicationContext Context context){
        return new File(context.getCacheDir(),"Http_cache");
    }
    @Provides
    @ApplicationScope
    public OkHttpClient okHttpClient(HttpLoggingInterceptor loggingInterceptor, Cache cache, @Named("auth") Interceptor interceptor, SharedPreferences preferences){
        OkHttpClient.Builder builder =  new OkHttpClient.Builder()
                .followRedirects(false)
                .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES);
//        if (preferences.getBoolean("auth",false))
        builder.addInterceptor(interceptor);
        if (preferences.getBoolean("cache",false))
            builder.cache(cache);

        return builder.build();
    }
    @Provides
    @Named("auth")
    @ApplicationScope
    public Interceptor authInterceptor(final SharedPreferences preferences){
        return chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("Accept","application/json")
                    .addHeader("Authorization","Bearer "+preferences.getString("token",""));


//            requestBuilder.url(original.url().toString()
//                    .replace(
//                            ApiInterface.LABTESTAPI,
//                            preferences.getString("LABTESTAPI" , "")
//                    )
////                    .replace(
////                            JsonPlaceholderApi.POST_STATUS_DUMMY_URL,
////                            mPreferences.getString("post_url" , "")
////                    )
//            );
//            original = requestBuilder.build();
//            return chain.proceed(original);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        };
    }

}
