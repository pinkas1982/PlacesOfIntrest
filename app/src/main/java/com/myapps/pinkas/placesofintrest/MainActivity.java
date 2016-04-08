package com.myapps.pinkas.placesofintrest;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.myapps.pinkas.placesofintrest.fragmants.MyMapFragment;
import com.myapps.pinkas.placesofintrest.internet.SearchPlacesNearBy;
import com.myapps.pinkas.placesofintrest.internet.SearchPlacesServices;
import com.myapps.pinkas.placesofintrest.places.Places;
import com.myapps.pinkas.placesofintrest.tabs.SlidingTabsLayout;
import com.myapps.pinkas.placesofintrest.utils.PlacesFragmentListenerr;

//import com.myapps.pinkas.placesofintrest.utils.ClickListener;


public class MainActivity extends ActionBarActivity implements View.OnClickListener, PlacesFragmentListenerr,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    FragmentManager fm;
    MyMapFragment mapFragment;
    public static ProgressDialog progress;
    private PlacesAdapter placesAdapter;
    Button goBtn;
    CheckBox nearBycB;
    EditText mySearch;
    Places places;
    private Cursor cursor;
    private GoogleApiClient mGoogleApiClient;
    public static Location currentLocation;
    private LocationRequest mLocationRequest;
    public static double currentLatitude;
    public static double currentLongitude;
    GoogleMap mMap;
    String provider;
    private Toolbar toolbar;
    private ViewPager mPager;
    private SlidingTabsLayout mTabs;
    RecyclerView rv;
    AdapterView.AdapterContextMenuInfo palcesInfo;
    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
        registerForContextMenu(rv);
        buildGoogleApiClient();

        AnalyticsApplication application = (AnalyticsApplication) getApplication();// google analytics
        mTracker = application.getDefaultTracker();

        mySearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchPlacesByText();
                    return true;
                }
                return false;
            }
        });

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }


  /*  @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }*/

    private void initComponent() {
        goBtn = (Button) findViewById(R.id.placeButton);
        goBtn.setOnClickListener(this);
        mySearch = (EditText) findViewById(R.id.placeEditText);
        placesAdapter = new PlacesAdapter(cursor, this);
        nearBycB = (CheckBox) findViewById(R.id.nearBycheckBox);
        nearBycB.setOnClickListener(this);
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (SlidingTabsLayout) findViewById(R.id.tabs);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTabs.setViewPager(mPager);
        rv =  (RecyclerView)findViewById(R.id.placesRecyclerView);
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return true;
    }

    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    //this is the onClick for the search button and for the search checkbox
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.placeButton:
                searchPlacesByText();
                break;
            case R.id.nearBycheckBox:
                boolean checked = ((CheckBox) v).isChecked();
                if (checked)
                    Toast.makeText(this, "checked", Toast.LENGTH_LONG).show();
                searchPlacesNearBy();
                break;
        }
    }

    private void searchPlacesNearBy() {
        String latFromGps = String.valueOf(currentLatitude);
        String langFromGps = String.valueOf(currentLongitude);
        String locationfromGps = latFromGps + "," + langFromGps;
        Intent intet = new Intent(this, SearchPlacesNearBy.class);
        intet.putExtra("searchNearBy", locationfromGps);
        startService(intet);

        waitProgressBar("please wait while loading places");
    }


    private void searchPlacesByText() {

        String textFromTextBox = mySearch.getText().toString();
        textFromTextBox = textFromTextBox.replaceAll(" ", "+");
        Intent intet = new Intent(this, SearchPlacesServices.class);
        intet.putExtra("search", textFromTextBox);
        startService(intet);

        waitProgressBar("please wait while loading places");

    }


    //this method is showing a progressBar that tell the user the data is loading.
    public void waitProgressBar(String message) {
        progress = new ProgressDialog(this);
        progress.setTitle("loading");
        progress.setMessage(message);
        progress.show();
    }


    @Override
    public void onLocationSelected(Places places) {
        //   Toast.makeText(this, "on click" + places, Toast.LENGTH_LONG).show();
        fm = getFragmentManager();
        // get the map object from the fragment:
        mapFragment = MyMapFragment.newInstance(places);

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmantContainer, mapFragment, "map");
        ft.addToBackStack(null);

        ft.commit();

    }


    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Ready to map", Toast.LENGTH_LONG).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (currentLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            currentLatitude = currentLocation.getLatitude();
            currentLongitude = currentLocation.getLongitude();

            Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("connection failed", connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        // Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        String[] tabs;

        public MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
            tabs = getResources().getStringArray(R.array.tabs);
        }

        public CharSequence getPagerTitle(int position) {
            return tabs[position];
        }

        @Override
        public Fragment getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater placesMenuInflater = getMenuInflater();
        placesMenuInflater.inflate(R.menu.place_context_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        palcesInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit_id:
                    sharePlaces(palcesInfo.position);
                return true;
            case R.id.delet_id:
                //      deletedFavorite(palcesInfo.position);
                return true;
            default:
                return super.onContextItemSelected(item);


        }

    }
    //this method share the place via facebook or whatsup or mail
    private void sharePlaces(int position) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Here is the share content body";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.km_or_mile_and_favorite_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

// Handle item selection
        switch (item.getItemId()) {
            case R.id.deletAllMoviesId:
                new AlertDialog.Builder(this)
                        .setTitle("Delete Movies")
                        .setMessage("Are you sure you want to delete all movies?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                chooseKmOrMiles();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            case R.id.exitId:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void chooseKmOrMiles() {
        //the user can choose between km or miles
    }

    public class AnalyticsApplication extends Application {


        /**
         * Gets the default {@link Tracker} for this {@link Application}.
         * @return tracker
         */
        synchronized public Tracker getDefaultTracker() {
            if (mTracker == null) {
                GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
                // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
                mTracker = analytics.newTracker(R.xml.global_tracker);
            }
            return mTracker;
        }
    }

}



