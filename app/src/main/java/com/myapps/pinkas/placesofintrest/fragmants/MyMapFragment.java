package com.myapps.pinkas.placesofintrest.fragmants;

import android.app.Fragment;
import android.app.FragmentManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.myapps.pinkas.placesofintrest.R;
import com.myapps.pinkas.placesofintrest.places.Places;

/**
 * Created by pinkas on 3/25/2016.
 */
public class MyMapFragment extends Fragment {


    public static MyMapFragment newInstance(Places places) {

        // in case of null create a dummy city
        if (places == null) {
            places = new Places(0,"no Location selected","","", "");
        }

        // the arguments to pass
        Bundle args = new Bundle();
        args.putString("location", places.getLocation());

/*        args.putDouble("lat", location.getLat());
        args.putDouble("lon", location.getLon());*/


        // create a fragment:
        MyMapFragment mapFragmant = new MyMapFragment();



        // set the arguments:
        mapFragmant.setArguments(args);


        // return the new fragment:
        return mapFragmant;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mapfragment, container, false);

        Bundle b=getArguments();


        FragmentManager fm = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        // get the map object from the fragment:

        GoogleMap map = mapFragment.getMap();

        if(map!= null) {
            // setup the map type:
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            // setup map position and zoom
            LatLng position = new LatLng(b.getDouble("lat"), b.getDouble("lon"));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(position, 15);
            map.moveCamera(update);
        }


        return view;
    }
}
