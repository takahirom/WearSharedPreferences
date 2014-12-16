package com.kogitune.wearsharedpreference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by takam on 2014/09/07.
 */
public class WearListenerService extends WearableListenerService {

    private static final String TAG = "WearHttpListenerService";
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        ConnectionResult connectionResult =
                mGoogleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }
        Parcel parcel = Parcel.obtain();
        parcel.readByteArray(messageEvent.getData());
        Bundle bundle = parcel.readBundle();
        SharedPreferences wearSharedPreference = getSharedPreferences("WearSharedPreference", MODE_PRIVATE);

        // SAVE
        saveBundle(wearSharedPreference.edit(),bundle);

        // Create DataMap instance
        PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/datapath");
        DataMap dataMap = dataMapRequest.getDataMap();

        // set data
        dataMap.putLong("time", new Date().getTime());
        dataMap.putBoolean("reqId:" + messageEvent.getRequestId(), true);

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

    /**
     * Manually save a Bundle object to SharedPreferences.
     * @param ed
     * @param bundle
     */
    private void saveBundle(SharedPreferences.Editor ed, Bundle bundle) {
        Set<String> keySet = bundle.keySet();
        Iterator<String> it = keySet.iterator();

        while (it.hasNext()){
            String key = it.next();
            Object o = bundle.get(key);
            if (o == null){
                ed.remove(key);
            } else if (o instanceof Integer){
                ed.putInt(key, (Integer) o);
            } else if (o instanceof Long){
                ed.putLong(key, (Long) o);
            } else if (o instanceof Boolean){
                ed.putBoolean(key, (Boolean) o);
            } else if (o instanceof CharSequence){
                ed.putString(key, ((CharSequence) o).toString());
            } else if (o instanceof Bundle){
                saveBundle(ed, ((Bundle) o));
            }
        }

        ed.commit();
    }


}
