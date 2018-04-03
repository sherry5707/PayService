package com.ragentek.service.payservice.utils;

/**
 * Created by RGK on 17/10/25.
 */
public class LogUtil {
    private final static String TAG = "PayService";
    private final static boolean DEBUG = true;
    private final static int NOLOG = -1;

    public static int d(String message) {
        return d(TAG, message);
    }

    public static int d(String TAG, String message) {
        if (!DEBUG) return NOLOG;
        return android.util.Log.d(TAG, message);
    }

    public static int i(String message) {
        return i(TAG, message);
    }

    public static int i(String tag, String message) {
        return android.util.Log.i(tag, message);
    }

    public static int v(String message) {
        return v(TAG, message);
    }

    public static int v(String tag, String message) {
        return android.util.Log.v(tag, message);
    }

    public static int e(String message) {
        return e(TAG, message);
    }

    public static int e(String tag, String message) {
        return android.util.Log.e(tag, message);
    }
}
