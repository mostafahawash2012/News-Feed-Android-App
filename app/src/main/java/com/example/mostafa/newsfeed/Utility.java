package com.example.mostafa.newsfeed;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by mostafa on 3/5/17.
 */

public class Utility {

    public static String getLocation(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.key_location),
                        context.getString(R.string.defaultValue_setting_location)).toLowerCase();
    }
}
