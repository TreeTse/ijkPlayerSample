package tv.danmaku.ijk.media.example.application;

import android.app.Application;
import android.content.Context;

public class BaseApp extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }
}

