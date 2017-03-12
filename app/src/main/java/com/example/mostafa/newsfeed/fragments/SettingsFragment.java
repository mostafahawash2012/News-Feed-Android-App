package com.example.mostafa.newsfeed.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import com.example.mostafa.newsfeed.R;
import com.example.mostafa.newsfeed.activities.MainActivity;
import com.example.mostafa.newsfeed.activities.SettingsActivity;
import com.example.mostafa.newsfeed.sync.NewsSyncAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.R.attr.value;

/**
 * Created by mostafa on 3/2/17.
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener,GoogleApiClient.ConnectionCallbacks ,
GoogleApiClient.OnConnectionFailedListener,LocationListener{

    String LOG_TAG = SettingsFragment.class.getSimpleName();
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    Preference mLocation;
    String mLovationValue;
    Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mContext = getActivity();

        //set up google api
        Log.e(LOG_TAG, "setting the api onCreate");
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

//        mLovationValue = PreferenceManager.getDefaultSharedPreferences(getActivity())
//                .getString(getString(R.string.key_location),"nooo value");
//        findPreference(getString(R.string.key_location)).setSummary(mLovationValue);

        mLocation= findPreference(getString(R.string.key_location));
        mLocation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(!mGoogleApiClient.isConnected()) {
                    Log.e(LOG_TAG, "locatio preference is clicked .. connecting the api");
                    mGoogleApiClient.connect();// connect api
                }else{
                    Log.e(LOG_TAG, "locatio preference is clicked .. api is connected already");
                    settingsRequest();
                }
                return true;
            }
        });
        bindPreferenceSummaryToValue(mLocation);

    }
    private void bindPreferenceSummaryToValue(Preference preference){
        //set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);
        String key = preference.getKey();
        String value= PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(key,"");
        if(key.equals(getString(R.string.key_location))){
            preference.setSummary(value);
        }

    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.key_location))){
            NewsSyncAdapter.syncImmediately(getActivity());
            Log.e(LOG_TAG, " syncImmediately from onshredchaged ");
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.e("onPreferencechanged", " onPreferenceChanged is called ");
        String stringValue = newValue.toString();
        if(preference.getKey().equals(getString(R.string.key_location))){
            preference.setSummary(stringValue);
        }


        return true;
    }


    @Override
    public void onStart() {//connects our google api
        super.onStart();
       // mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {// disconnects api
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {//called when google api connects successfully
        Log.e(LOG_TAG, "OnConnected is called");
        //create a location request called mLocationRequest
        mLocationRequest = LocationRequest.create();
        //set its priority to high
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);//PRIORITY_HIGH_ACCURACY gives you the finest possible location
        //set its interval - set it to update every second(1000ms)
        mLocationRequest.setInterval(1000);
        settingsRequest();
    }
    private void requestLocationUpdates(){
        Log.e(LOG_TAG,"RequestLocationUpdates is called");
        if (PermissionChecker.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            Log.e(LOG_TAG,"Check Permissions");
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {// called when it stops after being connected previously
        Log.e(LOG_TAG , "onConnectionSuspended is called");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {// when it fails to connect
        Log.e(LOG_TAG , "onConnectionFailed is called");
    }

    @Override
    public void onLocationChanged(Location location) {// where you get your location object and fetch it
        Log.e(LOG_TAG ,"OnLOcationChanged called");
        Log.e(LOG_TAG ,"Location : " + location.toString());

        //-- getting the country name form the location object
        Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(),location.getLongitude(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0)
        {
            String countryName=addresses.get(0).getCountryName();
            Log.e(LOG_TAG,"Update the sharedPreferences with the location "+countryName);
            PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                    .putString(getString(R.string.key_location),countryName).apply();
        }
    }
    public void settingsRequest(){
        Log.e(LOG_TAG,"settingRequest");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest( mLocationRequest);
        builder.setAlwaysShow(true);//Whether or not location is required by the calling app in order to continue.This changes the wording/appearance of the dialog accordingly.

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()){
                    //when location services are initially off, it always hits RESOLUTION_REQUIRED.
                    // Then once I accept and enable location, it hits SUCCESS, and entirely skips onActivityResult()
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.e(LOG_TAG,"Location Success .. no need to prompt user to open it");
                        requestLocationUpdates();
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.e(LOG_TAG,"Location Required! .. request dialog is fired ");
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult() that belongs to SettingsActivity and pass the response back to
                            // onActivityResult() that belongs to this fragment .
                            status.startResolutionForResult(getActivity(),REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//called from the Settings activity
        Log.e(LOG_TAG,"OnActivityResult");
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(LOG_TAG,"Activity Result is ok .. requestLocationUpdates is fired");
                        requestLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(LOG_TAG,"Activity Result is canceled ..call settingRequest to fire a dialog again!");

                        settingsRequest();
                        break;
                }
                break;
        }
    }
}
