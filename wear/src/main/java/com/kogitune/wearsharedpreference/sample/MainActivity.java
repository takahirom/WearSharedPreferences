package com.kogitune.wearsharedpreference.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kogitune.wearsharedpreference.WearSharedPreference;


public class MainActivity extends Activity {

    int i = 0;
    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView text = (TextView) findViewById(R.id.text);
        final WearSharedPreference wearSharedPreference = new WearSharedPreference(MainActivity.this);
        i = wearSharedPreference.get("int_text", 0);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wearSharedPreference.put("int_text", ++i);
                wearSharedPreference.sync(new WearSharedPreference.OnSyncListener() {
                    @Override
                    public void onSuccess() {
                        text.setText("i:" + i);
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
}
