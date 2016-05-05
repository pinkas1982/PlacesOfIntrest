package com.myapps.pinkas.placesofintrest.placesDb;

import android.net.Uri;

/**
 * Created by pinkas on 3/20/2016.
 */
public class PlacesDbconstanst {
    public final static String AUTHORITY = "com.myapps.pinkas.placesofintrest.placesDb.PlacesProvider";
    public final static String DISTANCE_UNIT = "distance unit";
    public final static String KM = "km";


    public static class CurrentPlaces {
        public static final String PLACES_TABLE_NAME = "places";
        public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PLACES_TABLE_NAME);
        public static final String DATABASE_NAME = "places.db";
        public static final int DATABASE_VERSION = 1;
        public static final String PLACES_ID = "_id";
        public static final String PLACES_NAME = "place_name";
        public static final String PLACES_ADDRESS = "address";
        public static final String PLACES_DISTANEC = "distance";
        public static final String PLACE_PHOTO = "photo";
        public static final String LOCATION = "location";
        public static final String LOG_TAG = "PlacesDb";

    }

    public static class History {
        public static final String PLACES_TABLE_NAME = "history";
        public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PLACES_TABLE_NAME);
        public static final String PLACES_ID = "_id";
        public static final String PLACES_NAME = "place_name";
        public static final String PLACES_ADDRESS = "address";
        public static final String PLACES_DISTANEC = "distance";
        public static final String LOCATION = "location";
        public static final String PLACE_PHOTO = "photo";
        public static final String LOG_TAG = "PlacesDb";
    }

    public static class Favorite {
        public static final String PLACES_TABLE_NAME = "favorite";
        public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PLACES_TABLE_NAME);
        public static final String PLACES_ID = "_id";
        public static final String PLACES_NAME = "place_name";
        public static final String PLACES_ADDRESS = "address";
        public static final String PLACES_DISTANEC = "distance";
        public static final String LOCATION = "location";
        public static final String PLACE_PHOTO = "photo";
        public static final String LOG_TAG = "PlacesDb";
    }


}