package com.kogitune.wearsharedpreference.sample;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kogitune.wearsharedpreference.WearSharedPreference;


public class MainActivity extends Activity implements WearSharedPreference.OnPreferenceChangeListener {

    String TAG = "MainActivity";
    private WearSharedPreference mWearSharedPreference;
    private Button mIterateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIterateButton = (Button) findViewById(R.id.button_iteration);
        mWearSharedPreference = new WearSharedPreference(MainActivity.this);
        setupIterateButton();
        setupEditText();
    }


    private void setupIterateButton() {
        final String incrementPreferenceKey = getString(R.string.key_preference_increment);
        mIterateButton.setText("i:" + mWearSharedPreference.get(incrementPreferenceKey, 0));
        mIterateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int i = mWearSharedPreference.get(incrementPreferenceKey, 0);
                final int plusI = i + 1;
                mWearSharedPreference.put(incrementPreferenceKey, plusI);
                mWearSharedPreference.sync(new WearSharedPreference.OnSyncListener() {
                    @Override
                    public void onSuccess() {
                        mIterateButton.setText("i:" + plusI);
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.d(TAG, "e.message():" + e.getMessage());
                    }
                });
            }
        });
    }

    private void setupEditText() {

        final String editTextPreferenceKey = getString(R.string.key_preference_text_edit);
        final EditText editText = (EditText) findViewById(R.id.edit_text);
        final Button editSyncButton = (Button) findViewById(R.id.button_edit_sync);
        editText.setText(mWearSharedPreference.get(editTextPreferenceKey, "empty"));
        editSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String s = editText.getText().toString();
                mWearSharedPreference.put(editTextPreferenceKey, s);
                mWearSharedPreference.sync(new WearSharedPreference.OnSyncListener() {
                    @Override
                    public void onSuccess() {
                        editText.setText(s);
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.d(TAG, "e.message():" + e.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public void onPreferenceChange(WearSharedPreference preference, String key, Bundle bundle) {
        Toast.makeText(this, "PreferenceChanged:" + key, Toast.LENGTH_LONG).show();
        final String keyInclement = getString(R.string.key_preference_increment);
        if (TextUtils.equals(keyInclement, key)) {
            mIterateButton.setText("i:" + bundle.getInt(keyInclement, 0));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mWearSharedPreference.registerOnPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWearSharedPreference.unregisterOnPreferenceChangeListener();
    }
}
