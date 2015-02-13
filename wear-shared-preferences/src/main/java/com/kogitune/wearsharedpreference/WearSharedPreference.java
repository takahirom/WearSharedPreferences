package com.kogitune.wearsharedpreference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.kogitune.wearablelistenerservicebroadcaster.WearListenerService;

/**
 * Created by takam on 2014/12/21.
 */
public class WearSharedPreference {


    public interface OnSyncListener {
        public void onSuccess();

        public void onFail(Exception e);
    }

    public interface OnPreferenceChangeListener {
        public void onPreferenceChange(WearSharedPreference preference, String key, Bundle bundle);
    }

    private String TAG = "WearSharedPreference";
    public static final String WEAR_SHARED_PREFERENCE_NAME = "WearSharedPreference";

    private final Context mContext;
    private SharedPreferences mPreferences;
    private final Bundle mBundle;
    private final WearBundleSyncer mWearBundleSyncer;
    private OnPreferenceChangeListener mPreferenceChangeListener;

    public WearSharedPreference(Context context) {
        mContext = context;
        mBundle = new Bundle();
        mWearBundleSyncer = new WearBundleSyncer(context);
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
        updatePreference();
        mWearBundleSyncer.get(mBundle, new WearBundleSyncer.WearGetCallBack() {
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

    public void registerOnPreferenceChangeListener(final OnPreferenceChangeListener preferenceChangeListener) {
        updatePreference();
        mPreferenceChangeListener = preferenceChangeListener;
        final String receiverAction = WearListenerService.ACTION_WEAR_LISTENER_RECEIVER;
        final IntentFilter intentFilter = new IntentFilter(receiverAction);
        // for after save preferences
        intentFilter.setPriority(0);
        mContext.getApplicationContext().registerReceiver(mWearBroadcastReceiver, intentFilter);
    }

    public void unregisterOnPreferenceChangeListener(){
        mContext.getApplicationContext().unregisterReceiver(mWearBroadcastReceiver);
    }

    BroadcastReceiver mWearBroadcastReceiver =  new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            final String  path = intent.getStringExtra(WearListenerService.MESSAGE_EVENT_PATH_KEY);
            if (!TextUtils.equals(PreferencesSaveService.MESSAGE_EVENT_PATH, path)) {
                return;
            }

            final byte[] data = intent.getByteArrayExtra(WearListenerService.MESSAGE_EVENT_DATA_KEY);
            if (data == null) {
                return;
            }
            final Bundle bundle = PreferencesSaveService.convertToBundle(data);
            for (String key : bundle.keySet()) {
                mPreferenceChangeListener.onPreferenceChange(WearSharedPreference.this, key, bundle);
            }
        }
    };


}
