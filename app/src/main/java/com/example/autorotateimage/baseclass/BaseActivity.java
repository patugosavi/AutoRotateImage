package com.example.autorotateimage.baseclass;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autorotateimage.di.component.ApplicationComponent;
import com.example.autorotateimage.utils.BaseApp;


public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG="BaseActivity";
    private Menu menu;
    private boolean exit;
    private boolean isChildActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies(BaseApp.get(this),BaseApp.getComponent());
    }

    protected abstract void injectDependencies(BaseApp baseApp, ApplicationComponent component);

    @Override
    public void finish() {
        super.finish();
    }


    public boolean isChildActivity() {
        return isChildActivity;
    }

    public void setChildActivity(boolean childActivity) {
        isChildActivity = childActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        if (!isChildActivity){
            if (exit) {
                finish(); // finish activity
                finishAffinity();
            } else {

                Toast.makeText(this, "Press again to Exit.", Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);
            }
        }
        else {
            super.onBackPressed();
        }
    }


}
