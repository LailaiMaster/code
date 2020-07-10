package com.dongnao.player;

import android.util.Log;

/**
 * @author Ztiany
 * Email: ztiany3@gmail.com
 * Date : 2020-05-15 16:24
 */
public class LogUtil {

    private static final String TAG = "FFmpeg";

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }

}
