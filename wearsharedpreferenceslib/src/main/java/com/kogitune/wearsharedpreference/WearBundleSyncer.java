package com.kogitune.wearsharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by takam on 2014/09/21.
 */
public class WearBundleSyncer extends WearSyncer {

    private WearGetCallBack mCallBack;
    private Bundle mBundle;
    private SharedPreferences mPreferences;

    public WearBundleSyncer(Context context) {
        super(context);
    }

    public interface WearGetCallBack {
        public void onGet();

        public void onFail(Exception e);
    }

    /**
     * Get text contents.
     *
     * @param bundle
     * @param callBack On get text called get.
     */
    public void get(final Bundle bundle, final WearGetCallBack callBack) {
        get(bundle, callBack, 10);
    }

    public void get(final Bundle bundle, final WearGetCallBack callBack, final int timeOutSeconds) {
        mCallBack = callBack;
        mBundle = bundle;
        super.get(bundle, timeOutSeconds);
    }

    @Override
    void callSuccess() {
        if (mCallBack == null) {
            return;
        }
        mCallBack.onGet();
        mCallBack = null;
    }

    @Override
    void callFail(Exception e) {
        if (mCallBack == null) {
            return;
        }
        mCallBack.onFail(e);
        mCallBack = null;
    }

}
