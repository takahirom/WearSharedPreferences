package com.kogitune.wearsharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by takam on 2014/12/21.
 */
public class WearSharedPreference {

    public interface OnSyncListener {
        public void onSuccess();

        public void onFail(Exception e);
    }
    public static final String WEAR_SHARED_PREFERENCE_NAME = "WearSharedPreference";

    private final Context mContext;
    private SharedPreferences mPreferences;
    private final Bundle mBundle;
    private final WearGetText mWearGetText;

    public WearSharedPreference(Context context) {
        mContext = context;
        mBundle = new Bundle();
        mWearGetText = new WearGetText(context);
    }

    private void updatePreference() {
        mPreferences = mContext.getSharedPreferences(WEAR_SHARED_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS);
    }

    public void put(String key, int val) {
        mBundle.putInt(key, val);
    }

    public void put(String key, String val) {
        mBundle.putString(key, val);
    }

    public void put(String key, boolean val) {
        mBundle.putBoolean(key, val);
    }

    public void put(String key, float val) {
        mBundle.putFloat(key, val);
    }


    public int get(String key, int defaultInt) {
        updatePreference();
        return mPreferences.getInt(key, defaultInt);
    }

    public String get(String key, String defaultInt) {
        updatePreference();
        return mPreferences.getString(key, defaultInt);
    }

    public boolean get(String key, boolean defaultInt) {
        updatePreference();
        return mPreferences.getBoolean(key, defaultInt);
    }

    public float get(String key, float defaultInt) {
        updatePreference();
        return mPreferences.getFloat(key, defaultInt);
    }

    public void sync(final OnSyncListener syncListener) {
        mWearGetText.get(mBundle, new WearGetText.WearGetCallBack() {
            @Override
            public void onGet() {
                new SharedPreferenceUtil(mPreferences).saveBundle(mBundle);
                syncListener.onSuccess();
            }

            @Override
            public void onFail(Exception e) {
                syncListener.onFail(e);
            }
        });
    }
}
