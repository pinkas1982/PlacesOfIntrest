package com.myapps.pinkas.placesofintrest.fragmants;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.myapps.pinkas.placesofintrest.MainActivity;
import com.myapps.pinkas.placesofintrest.R;
import com.myapps.pinkas.placesofintrest.internet.SearchPlacesServices;
import com.myapps.pinkas.placesofintrest.places.Places;

/**
 * Created by pinkas on 5/2/2016.
 */
public class SearchPlaceFragment extends Fragment implements View.OnClickListener, LocationListener {

    private static final String TAG = "FSearch";
    boolean isGPSEnabled = false;
    boolean isNETWORKEnabled = false;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String provider;
    Criteria criteria;
    Location lastKnownLocation;
    Boolean permissionResult;
    Double Latitude;
    Double Longitude;
    Places myLocation;

    private static final String NEARBY = "nearBySearch";
    private static final String TEXTSEARCH = "TextSearch";
    private static final String CITY_DEF = "Defualt City - Tel Aviv";
    private static final String ADDRESS_DEF = "Defualt Address - center";
    private static final String LAT_DEF = "32.080";
    private static final String LON_DEF = "34.780";

    String url;
    Bundle bundle;

    EditText searchEdit;
    EditText cityEdit;
    Button search;
    CheckBox checkBox;
    EditText radiusEdit;
    TextView cityText;
    TextView addressText;
    TextView latLabel;
    TextView latText;
    TextView lonLabel;
    TextView lonText;
    TextView mycityLabel;
    TextView myAddressLabel;

    Button updbtn;
    String value;
    String query;
    String lat;
    String lon;
    String radius;
    String city;

    SearchPlaceFragmentListener myActivity;

    public interface SearchPlaceFragmentListener {
        void onSearchPresed();
    }

    public static SearchPlaceFragment newInstance() {
        SearchPlaceFragment searchPlaceFragment = new SearchPlaceFragment();
        return searchPlaceFragment;
    }


    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "On Attach");
        super.onAttach(activity);


        try

        {
            permissionResult = checkPermission();
            locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
            myActivity = (SearchPlaceFragmentListener) getActivity();
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_LOW);
            provider = locationManager.getBestProvider(criteria, true);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNETWORKEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isGPSEnabled) {
                Log.d("GPS", "Enable");
            } else if (isNETWORKEnabled) {
                Log.d("NETWORK", "Enable");
            } else {
                Toast.makeText(getActivity(), "GPS & NETWORK not enable location null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (myLocation == null) {
            myLocation = new Places(CITY_DEF, ADDRESS_DEF, LAT_DEF, LON_DEF);
            getActivity().setTitle("My Location:" + myLocation.getPlaceName());
        } else {
            getActivity().setTitle("My Location:" + myLocation.getPlaceName());
        }
    }

    public void init(View v) {
        searchEdit = (EditText) v.findViewById(R.id.placeEditText);
        cityEdit = (EditText) v.findViewById(R.id.cityEditText);
        search = (Button) v.findViewById(R.id.button);
        search.setOnClickListener(this);
        checkBox = (CheckBox) v.findViewById(R.id.checkBox);
        radiusEdit = (EditText) v.findViewById(R.id.radiusEditText);
        cityText = (TextView) v.findViewById(R.id.cityText);
        mycityLabel = (TextView)v.findViewById(R.id.myCityLabel);
        addressText = (TextView) v.findViewById(R.id.addressTextView);
        myAddressLabel = (TextView)v.findViewById(R.id.placeEditText);
        latLabel = (TextView) v.findViewById(R.id.latLabel);
        latText = (TextView) v.findViewById(R.id.latText);
        lonLabel = (TextView) v.findViewById(R.id.lonLabel);
        lonText = (TextView) v.findViewById(R.id.lonText);
        updbtn = (Button)v.findViewById(R.id.updbtn);
        updbtn.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "on creat view");
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        init(v);
        locationService();
        Log.e(TAG, myLocation.toString());
        String city = myLocation.getPlaceName();
        String address = myLocation.getPlaceAddress();
        lat = myLocation.getLocation();
        cityText.setText(city);
        addressText.setText(address);

        return v;
    }




    @Override
    public void onClick(View v) {

        if (updbtn.isPressed()) {
            permissionResult = checkPermission();
            lastKnownLocation = locationManager.getLastKnownLocation(provider);
            if (lastKnownLocation != null) {
                Latitude = lastKnownLocation.getLatitude();
                Longitude = lastKnownLocation.getLongitude();
                myLocation.setLat(Latitude.toString());
                myLocation.setLon(Longitude.toString());
                lat = myLocation.getLat();
                lon = myLocation.getLon();
                latText.setText(lat);
                lonText.setText(lon);

            } else {
                Toast.makeText(getActivity(), "no update available, cant found location", Toast.LENGTH_LONG).show();}
            }else{
                Boolean cbResult = checkBox.isChecked();
                if (cbResult) {
                    value = searchEdit.getText().toString();
                    query = value.replace(" ", "+");
                    lat = latText.getText().toString();
                    lon = lonText.getText().toString();
                    radius = radiusEdit.getText().toString();
                    if (radius.equals("")) {
                        if (MainActivity.radiusType) {
                            radius = "1000";
                        } else {
                            radius = "1609";
                        }
                    } else {
                        float radiusFloat = Float.parseFloat(radius);
                        int radiusInt = (int) radiusFloat;
                        if (MainActivity.radiusType) {
                            radiusInt = radiusInt * 1000;
                            radius = String.valueOf(radiusInt);
                        } else {
                            radiusInt = radiusInt * 1609;
                            radius = String.valueOf(radiusInt);
                        }
                    }
                    Log.e("checkBox.isChecked", radius);
                    myActivity.onSearchPresed();
                    Intent intent = new Intent(getActivity(), SearchPlacesServices.class);
                    intent.putExtra("action", NEARBY);
                    intent.putExtra("query", query);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lon", lon);
                    intent.putExtra("radius", radius);
                    getActivity().startService(intent);

                } else {
                    value = searchEdit.getText().toString();
                    value = value.replace(" ", "+");
                    city = cityEdit.getText().toString();
                    city = city.replace(" ", "+");
                    myActivity.onSearchPresed();
                    Intent intent = new Intent(getActivity(), SearchPlacesServices.class);
                    intent.putExtra("action", NEARBY);
                    intent.putExtra("query", query);
                    intent.putExtra("lat", "none");
                    intent.putExtra("lon", "none");
                    intent.putExtra("radius", "none");
                    getActivity().startService(intent);

                }
            }

            {

            }

        }



    private void locationService() {
        Log.e("locationService", "start");
        permissionResult = checkPermission();
        Log.e("locationService", permissionResult.toString());
         lastKnownLocation = locationManager.getLastKnownLocation(provider);
        if(null != lastKnownLocation){
            Log.e("location:",lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude());
            Latitude = lastKnownLocation.getLatitude();
            Longitude  = lastKnownLocation.getLongitude();
            myLocation.setPlaceName("default");
            myLocation.setPlaceAddress("default");
            myLocation.setLat(Latitude.toString());
            myLocation.setLon(Longitude.toString());
        }else{
            Log.e("location:","no known location found");
        }

    }
    public Boolean checkPermission(){
        if(Build.VERSION.SDK_INT >=23 &&
                getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
        return true;
        }else{
            return false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onResume() {
        super.onResume();

        permissionResult = checkPermission();
        locationManager.requestLocationUpdates(provider, 1000, 0, (android.location.LocationListener) this);
        Log.e("location:", "strating update listener");
    }

    @Override
    public void onPause() {
        super.onPause();
        permissionResult = checkPermission();
        locationManager.removeUpdates((android.location.LocationListener) this);
        Log.e("location:", "stoping update listener");
    }

    @Override
    public void onLocationChanged(Location location) {
        Latitude = location.getLatitude();
        Longitude = location.getLongitude();
        myLocation.setLat(Latitude.toString());
        myLocation.setLon(Longitude.toString());
        lat = myLocation.getLat();
        lon = myLocation.getLon();
        latText.setText(lat);
        lonText.setText(lon);
        Log.e("location change: ", location.getLatitude() + "," + location.getLongitude() );

    }
}


