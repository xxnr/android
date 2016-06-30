package com.ksfc.newfarmer.utils;

import android.os.Bundle;

/**
 * Created by CAI on 2016/6/30.
 */
public class BundleUtils {

    public static Bundle Put(String key1, Object object1) {
        Bundle bundle = new Bundle();
        putBundle(bundle, key1, object1);
        return bundle;
    }

    public static Bundle Put(String key1, Object object1, String key2, Object object2) {
        Bundle bundle = new Bundle();
        putBundle(bundle, key1, object1);
        putBundle(bundle, key2, object2);
        return bundle;

    }

    public static Bundle Put(String key1, Object object1, String key2, Object object2, String key3, Object object3) {
        Bundle bundle = new Bundle();
        putBundle(bundle, key1, object1);
        putBundle(bundle, key2, object2);
        putBundle(bundle, key3, object3);
        return bundle;
    }

    private static void putBundle(Bundle bundle, String key, Object object) {

        if (object instanceof String) {
            bundle.putString(key, (String) object);
        } else if (object instanceof Integer) {
            bundle.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            bundle.putBoolean(key, (Boolean) object);
        }
    }

}
