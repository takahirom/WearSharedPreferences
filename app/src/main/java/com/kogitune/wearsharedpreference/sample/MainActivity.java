package com.kogitune.wearsharedpreference.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kogitune.wearsharedpreference.WearSharedPreference;


public class
        MainActivity extends Activity implements WearSharedPreference.OnPreferenceChangeListener {

    String TAG = "MainActivity";
    private WearSharedPreference mWearSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWearSharedPreference = new WearSharedPreference(MainActivity.this);
        mWearSharedPreference.registerOnPreferenceChangeListener(this);
        setupIterateButton();
        setupEditText();
    }


    private void setupIterateButton() {
        final String incrementPreferenceKey = getString(R.string.key_preference_increment);
        final Button iterateButton = (Button) findViewById(R.id.button_iteration);
        iterateButton.setText("i:" + mWearSharedPreference.get(incrementPreferenceKey, 0));
        iterateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int i = mWearSharedPreference.get(incrementPreferenceKey, 0);
                final int plusI = i + 1;
                mWearSharedPreference.put(incrementPreferenceKey, plusI);
                mWearSharedPreference.sync(new WearSharedPreference.OnSyncListener() {
                    @Override
                    public void onSuccess() {
                        iterateButton.setText("i:" + plusI);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPreferenceChange(WearSharedPreference preference, String key, Bundle bundle) {
        Toast.makeText(this, "PreferenceChanged:" + key, Toast.LENGTH_LONG).show();
    }
}
