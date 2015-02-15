WearSharedPreferences
=====================

Android Wear and Phone App sync SharedPreferences

[![Build Status](https://travis-ci.org/takahirom/WearSharedPreferences.svg?branch=0.0.1-alpha)](https://travis-ci.org/takahirom/WearSharedPreferences)

## Usage
In Mobile and Wear module build.gradle:  
**You must be implemented in both the Mobile and Wear**

```
dependencies {
    ...
    compile 'com.kogitune:wear-shared-preferences:0.0.2'
}
```

## Example

### Key string definition

In root project build.gralde, you can define strings.xml resource value both of mobile and wear project.
```
allprojects {
    afterEvaluate {
        project->
        if(!project.hasProperty("android")){
            return;
        }
        project.android.defaultConfig.with {
            resValue "string", "key_preference_photo_url", "key_preference_photo_url"
            resValue "string", "key_preference_photo_title", "key_preference_photo_title"
        }
    }
}
```

### Code

#### Saving and syncing SharedPreference value.

```
final WearSharedPreference preference = new WearSharedPreference(context);
preference.put(getString(R.string.key_preference_photo_url), photoUrl);
preference.put(getString(R.string.key_preference_photo_title), title);
preference.sync(new WearSharedPreference.OnSyncListener() {
    @Override
    public void onSuccess() {
    }

    @Override
    public void onFail(Exception e) {
    }
});
```

#### Getting SharedPreferences value.

```
String beforePhotoUrl = new WearSharedPreference(this).get(getString(R.string.key_preference_photo_url), "");
```

#### Knowing change event of SharedPreferences value.

If you want to know change event of preferences by wear and phone, You can use 'registerOnPreferenceChangeListener'.

```
    @Override
    protected void onStart() {
        super.onStart();
        wearPref.registerOnPreferenceChangeListener(new WearSharedPreference.OnPreferenceChangeListener() {
            @Override
            public void onPreferenceChange(WearSharedPreference preference, String key, Bundle bundle) {
                if (!TextUtils.equals(getString(R.string.key_preference_photo_url), key)) {
                    return;
                }
                final String photoUrl = bundle.getString(key);
                // You can use changed photoUrl.
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        wearPref.unregisterOnPreferenceChangeListener();
    }
```


## Suggestion

WearSharedPreferences can use in [WearHttp](https://github.com/takahirom/WearHttp) and simultaneous.


## License

This project is released under the Apache License, Version 2.0.

* [The Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
