package com.yaar.shortvideoapp.SimpleClasses;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.yaar.shortvideoapp.LanguageUtils;
import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;


/**
 * Created by AQEEL on 3/18/2019.
 */

public class MainAppClass extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        FirebaseApp.initializeApp(this);
        LanguageUtils.setLocale(this);
        LanguageUtils.updateConfig(this, getBaseContext().getResources().getConfiguration());

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LanguageUtils.updateConfig(this, newConfig);

    }


    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        MainAppClass app = (MainAppClass) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)
                .maxCacheFilesCount(20)
                .build();
    }

}
