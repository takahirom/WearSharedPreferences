package com.kogitune.wearsharedpreference;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;

/**
 * Created by takam on 2014/09/21.
 */
public class WearGetText extends WearGet {

    private WearGetCallBack mCallBack;

    public WearGetText(Context context) {
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
        super.get(bundle, timeOutSeconds);
    }

    @Override
    void callSuccess() {
        if (mCallBack == null) {
            return;
        }
        Parcel parcel = Parcel.obtain();
        ;

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
