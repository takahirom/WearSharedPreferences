package com.kogitune.wearsharedpreference;

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
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by takam on 2014/09/07.
 */
public class WearListenerService extends WearableListenerService {

    private static final String TAG = "WearListenerService";
    private GoogleApiClient mGoogleApiClient;
    public static final String MESSAGE_EVENT_PATH = "MESSAGE_EVENT_PATH";
    public static final String MESSAGE_EVENT_DATA = "MESSAGE_EVENT_DATA";
    public static final String MESSAGE_EVENT_REQUEST_ID = "MESSAGE_EVENT_REQUEST_ID";
    public static final String MESSAGE_EVENT_SOURCE_NODE_ID = "MESSAGE_EVENT_SOURCE_NODE_ID";

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }
        if (!intent.hasExtra(MESSAGE_EVENT_PATH)) {
            return START_NOT_STICKY;
        }
        handleEvent(intent.getStringExtra(MESSAGE_EVENT_PATH), intent.getByteArrayExtra(MESSAGE_EVENT_DATA), intent.getIntExtra(MESSAGE_EVENT_REQUEST_ID, 0), intent.getStringExtra(MESSAGE_EVENT_SOURCE_NODE_ID));
        return START_NOT_STICKY;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        final Intent intent = new Intent();
        intent.setPackage(getPackageName());
        intent.putExtra(MESSAGE_EVENT_PATH, messageEvent.getPath());
        intent.putExtra(MESSAGE_EVENT_DATA, messageEvent.getData());
        intent.putExtra(MESSAGE_EVENT_REQUEST_ID, messageEvent.getRequestId());
        intent.putExtra(MESSAGE_EVENT_SOURCE_NODE_ID, messageEvent.getSourceNodeId());
        sendBroadcast(intent);
    }

    private void handleEvent(final String path, final byte[] data, final int requestId, final String sourceNodeId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                tellSavedPreferences(data, requestId);
            }
        }).start();

    }

    private void tellSavedPreferences(byte[] data, int requestId) {
        ConnectionResult connectionResult =
                mGoogleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }

        byte[] bundleBytes = data;
        final Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bundleBytes, 0, bundleBytes.length);
        parcel.setDataPosition(0);
        Bundle bundle = (Bundle) parcel.readBundle();
        parcel.recycle();

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
                        Log.d(TAG, "putDataItem status: "
                                + dataItemResult.getStatus().toString());
                    }
                });
    }


}
