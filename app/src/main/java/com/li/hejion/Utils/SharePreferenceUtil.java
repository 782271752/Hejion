package com.li.hejion.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by li on 2014/7/26.
 */
public class SharePreferenceUtil {


    public final static String USERNAME="username";
    public final static String PASSWORD="password";
    public final static String SESSION_ID="se";
    public static void setPreference(Context context, String key,String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
        editor = null;
    }

    public static String getPreference(Context context,String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = preferences.getString(key, "");
        return value;
    }
}
