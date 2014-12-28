package com.kogitune.wearsharedpreference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by takam on 2014/12/21.
 */
public class SharedPreferenceUtil {
    private static final String TAG = "SharedPreferenceUtil";
    private final SharedPreferences mPreferences;

    public SharedPreferenceUtil(SharedPreferences preferences) {
        mPreferences = preferences;
    }


    /**
     * Manually save a Bundle object to SharedPreferences.
     *
     * @param bundle
     */
    public void saveBundle(Bundle bundle) {
        SharedPreferences.Editor ed = mPreferences.edit();
        Set<String> keySet = bundle.keySet();
        Iterator<String> it = keySet.iterator();

        while (it.hasNext()) {
            String key = it.next();
            Object o = bundle.get(key);
            Log.d(TAG, "key:'" + key + "' value:" + o);
            if (o == null) {
                ed.remove(key);
            } else if (o instanceof Integer) {
                ed.putInt(key, (Integer) o);
            } else if (o instanceof Long) {
                ed.putLong(key, (Long) o);
            } else if (o instanceof Boolean) {
                ed.putBoolean(key, (Boolean) o);
            } else if (o instanceof CharSequence) {
                ed.putString(key, ((CharSequence) o).toString());
            } else if (o instanceof Bundle) {
                saveBundle(((Bundle) o));
            }
        }

        ed.commit();
    }
}
