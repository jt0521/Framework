package com.mobileframe.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.mobileframe.common.Constant;

import java.util.Map;

public class SharedPreferencesHelper {
    SharedPreferences mSp;
    SharedPreferences.Editor editor;
    private Context context;

    public SharedPreferencesHelper(Context c) {
        this(c, Constant.KEY_USER_INFO);
    }

    public SharedPreferencesHelper(Context c, String name) {
        context = c;
        mSp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesHelper newInstance(Context c) {
        return new SharedPreferencesHelper(c);
    }

    public void removeValue(String key) {
        editor = mSp.edit();
        editor.remove(key);
        editor.commit();
    }

    public void putValue(String key, String value) {
        editor = mSp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void putIntValue(String key, int value) {
        editor = mSp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putBooleanValue(String key, boolean value) {
        editor = mSp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putLongValue(String key, long value) {
        editor = mSp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public String getValue(String key) {
        return mSp.getString(key, "");
    }

    public String getValue(String key, String defaultStr) {
        return mSp.getString(key, defaultStr);
    }

    public int getIntValue(String key) {
        return mSp.getInt(key, 0);
    }

    public Long getLongValue(String key) {
        return mSp.getLong(key, 0);
    }

    public boolean getBooleanValue(String key) {
        return mSp.getBoolean(key, false);
    }

    public boolean getBooleanValueDefaultFalse(String key) {
        return mSp.getBoolean(key, false);
    }

    public Map<String, ?> getKeyandValue() {
        return mSp.getAll();
    }

    public void clear() {
        editor = mSp.edit();
        editor.clear();
        editor.commit();
    }
}
