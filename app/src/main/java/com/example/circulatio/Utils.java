package com.example.circulatio;

import android.app.ActivityManager;
import android.content.Context;

import java.util.Map;

public final class Utils {

    private static Utils mUtils;
    private static Map<String, String> loadedConfig;

    private Utils() {
    }

    public static synchronized Utils getInstance() {
        if (mUtils == null) {
            mUtils = new Utils();
        }
        return mUtils;
    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
