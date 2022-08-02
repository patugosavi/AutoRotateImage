package com.example.autorotateimage.di.component;


import android.content.SharedPreferences;


import com.example.autorotateimage.MainActivity;
import com.example.autorotateimage.di.module.RetrofitModule;
import com.example.autorotateimage.di.module.SharedPrefModule;
import com.example.autorotateimage.di.scop.ApplicationScope;
import com.example.autorotateimage.network_service.ApiInterface;

import dagger.Component;


@ApplicationScope
@Component(modules = {RetrofitModule.class, SharedPrefModule.class})
public interface ApplicationComponent {

    ApiInterface getNetworkService();

    SharedPreferences sharedPrefences();

      void inject(MainActivity mainActivity);


}

