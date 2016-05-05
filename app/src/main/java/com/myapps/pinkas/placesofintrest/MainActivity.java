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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.myapps.pinkas.placesofintrest.fragmants.MyMapFragment;
import com.myapps.pinkas.placesofintrest.fragmants.SearchPlaceFragment;
import com.myapps.pinkas.placesofintrest.internet.SearchPlacesNearBy;
import com.myapps.pinkas.placesofintrest.internet.SearchPlacesServices;
import com.myapps.pinkas.placesofintrest.places.Places;
import com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst;
import com.myapps.pinkas.placesofintrest.tabs.SlidingTabsLayout;
import com.myapps.pinkas.placesofintrest.utils.PlacesFragmentListenerr;

//import com.myapps.pinkas.placesofintrest.utils.ClickListener;


public class MainActivity extends ActionBarActivity implements View.OnClickListener, PlacesFragmentListenerr,SearchPlaceFragment.SearchPlaceFragmentListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    FragmentManager fm;
    MyMapFragment mapFragment;
    public static ProgressDialog progress;
  //  private PlacesAdapter placesAdapter;
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
    public static GoogleAnalytics analytics;
  //  PlacesAdapter adapter;
   public static Boolean radiusType = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    //    typeOfScreen();
        initComponent();
        registerForContextMenu(rv);
        buildGoogleApiClient();

        analytics = GoogleAnalytics.getInstance(this); // google analytics
        analytics.setLocalDispatchPeriod(1800);
        mTracker = analytics.newTracker("UA-76184631-1");
        mTracker.enableExceptionReporting(true);
        mTracker.enableAutoActivityTracking(true);

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

/*    private void typeOfScreen() {
        // check if single fragment view (phone)
        // or dual fragment view (tablet)
        if (isInSingleFragment()) {
            //hide the B fragment:
            FragmentManager fm = getFragmentManager();
            //find the fragment (by id):
            android.app.Fragment fragmentB = fm.findFragmentById(R.id.map);
            //do the hide:
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(fragmentB);
            ft.commit();
        }
    }*/

/*        // helper method: are we in a single fragment (like a phone) ?
    protected boolean isInSingleFragment() {
        //try to find the layout with id : layout_singleLayout
        View layout = findViewById(R.id.layout_singleLayout);

        // this will be null on a dual fragment layout

        if (layout != null) {
            // found - we are
            return true;
        } else {
            // not found - we are not.
            return false;
        }
    }*/


    private void initComponent() {
        goBtn = (Button) findViewById(R.id.placeButton);
        goBtn.setOnClickListener(this);
        mySearch = (EditText) findViewById(R.id.placeEditText);
      //  placesAdapter = new PlacesAdapter(cursor, this);
        nearBycB = (CheckBox) findViewById(R.id.nearBycheckBox);
        nearBycB.setOnClickListener(this);
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (SlidingTabsLayout) findViewById(R.id.tabs);
//        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
//        mTabs.setViewPager(mPager);
        rv = (RecyclerView) findViewById(R.id.placesRecyclerView);
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
        getContentResolver().delete(PlacesDbconstanst.CurrentPlaces.CONTENT_URI, null, null);

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
        Toast.makeText(this, "connection failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

    }

    @Override
    public void onSearchPresed() {

    }

    //this method is for the tabs
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
            MyFragment myFragment = MyFragment.getInstance(position);

            return myFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


    public static class MyFragment extends Fragment {
        RecyclerView recyclerView;

        public static MyFragment getInstance(int position) {
            MyFragment myFragment = new MyFragment();
            Bundle arg = new Bundle();
            arg.putInt("position", position);
            myFragment.setArguments(arg);
            return myFragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.fragment_places, container, false);
            recyclerView = (RecyclerView) layout.findViewById(R.id.placesRecyclerView);
            Bundle bundle = getArguments();

            return layout;
        }
    }


    //when I want to return from a fragment I use this method in the main activity
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

//this method create the menu
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
            case R.id.kmOrMileId:
                new AlertDialog.Builder(this)
                        .setTitle("Choose Parameter of Distance")
                        .setMessage("Km or Mile")
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
            case R.id.deletFavorite:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
//TODO i need give the user the option to choose between km & mile
    private void chooseKmOrMiles() {
        //the user can choose between km or miles
    }

}



