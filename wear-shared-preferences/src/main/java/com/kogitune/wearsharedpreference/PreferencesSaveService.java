package com.kogitune.wearsharedpreference;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.kogitune.wearablelistenerservicebroadcaster.WearListenerService;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by takam on 2014/09/07.
 */
public class PreferencesSaveService extends IntentService {

    private static final String TAG = "WearListenerService";
    private GoogleApiClient mGoogleApiClient;

    public static final String MESSAGE_EVENT_PATH = "/preferences/sync";

    public PreferencesSaveService() {
        super("PreferencesSaveService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        handleEvent(
                intent.getStringExtra(WearListenerService.MESSAGE_EVENT_PATH_KEY),
                intent.getByteArrayExtra(WearListenerService.MESSAGE_EVENT_DATA_KEY),
                intent.getIntExtra(WearListenerService.MESSAGE_EVENT_REQUEST_ID_KEY, 0),
                intent.getStringExtra(WearListenerService.MESSAGE_EVENT_SOURCE_NODE_ID_KEY));
    }

    private void handleEvent(final String path, final byte[] data, final int requestId, final String sourceNodeId) {
        if (!MESSAGE_EVENT_PATH.equals(path)) {
            return;
        }
        tellSavedPreferences(data, requestId);
    }

    private void tellSavedPreferences(byte[] data, int requestId) {

        ConnectionResult connectionResult =
                mGoogleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }

        Bundle bundle = convertToBundle(data);

        SharedPreferences wearSharedPreference = getSharedPreferences(WearSharedPreference.WEAR_SHARED_PREFERENCE_NAME, MODE_MULTI_PROCESS);

        // SAVE
        new SharedPreferenceUtil(wearSharedPreference).saveBundle(bundle);

        // Create DataMap instance
        PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/datapath");
        DataMap dataMap = dataMapRequest.getDataMap();

        // set data
        dataMap.putLong("time", new Date().getTime());
        dataMap.putBoolean("reqId:" + requestId, true);

        // refresh data
        final PutDataRequest request = dataMapRequest.asPutDataRequest();

        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "putDataItem status: "
                                    + dataItemResult.getStatus().toString());
                        }
                    }
                });
    }

    public static Bundle convertToBundle(byte[] data) {
        byte[] bundleBytes = data;
        final Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bundleBytes, 0, bundleBytes.length);
        parcel.setDataPosition(0);
        Bundle bundle = (Bundle) parcel.readBundle();
        parcel.recycle();
        return bundle;
    }


}
