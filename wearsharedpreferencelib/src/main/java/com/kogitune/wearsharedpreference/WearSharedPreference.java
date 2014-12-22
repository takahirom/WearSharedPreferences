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

    private final SharedPreferences mPreferences;
    private final Bundle mBundle;
    private final WearGetText mWearGetText;

    public WearSharedPreference(Context context) {
        mPreferences = context.getSharedPreferences("WearSharedPreference", Context.MODE_PRIVATE);
        mBundle = new Bundle();
        mWearGetText = new WearGetText(context);
    }

    public void put(String key, int i) {
        mBundle.putInt(key, i);
    }

    public int getInt(String key, int defaultInt) {
        return mPreferences.getInt(key, defaultInt);
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
