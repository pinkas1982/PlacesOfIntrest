package com.myapps.pinkas.placesofintrest.placesDb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst.CurrentPlaces.*;
import static com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst.History.*;
import static com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst.Favorite.*;

/**
 * Created by pinkas on 3/20/2016.
 */
public class PlacesDbHelper extends SQLiteOpenHelper {


    public PlacesDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public PlacesDbHelper(Context context) {
        super(context, "places.db", null, 1);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(PlacesDbconstanst.CurrentPlaces.LOG_TAG, "Creating All The Tables");

        String sql =
                "CREATE TABLE " + PlacesDbconstanst.CurrentPlaces.PLACES_TABLE_NAME +
                        "(" + PlacesDbconstanst.CurrentPlaces.PLACES_ID + " INTEGER PRIMARY KEY autoincrement,"
                        + PlacesDbconstanst.CurrentPlaces.PLACES_NAME + " TEXT,"
                        + PlacesDbconstanst.CurrentPlaces.PLACES_ADDRESS + " TEXT,"
                        + PlacesDbconstanst.CurrentPlaces.PLACE_PHOTO + " TEXT,"
                        + PlacesDbconstanst.CurrentPlaces.LOCATION + " TEXT,"
                        + PlacesDbconstanst.CurrentPlaces.PLACES_DISTANEC + " REAL)";

        try {
            db.execSQL(sql);
        } catch (SQLiteException ex) {
            Log.e(PlacesDbconstanst.CurrentPlaces.LOG_TAG, "Create table exception: " +
                    ex.getMessage());

        }

        sql = "CREATE TABLE " + PlacesDbconstanst.History.PLACES_TABLE_NAME +
                "(" + PlacesDbconstanst.History.PLACES_ID + " INTEGER PRIMARY KEY autoincrement,"
                + PlacesDbconstanst.History.PLACES_NAME + " TEXT,"
                + PlacesDbconstanst.History.PLACES_ADDRESS + " TEXT,"
                + PlacesDbconstanst.History.LOCATION + " TEXT,"
                + PlacesDbconstanst.History.PLACES_DISTANEC + " REAL,"
                + PlacesDbconstanst.History.PLACE_PHOTO + " TEXT)";
        try {
            db.execSQL(sql);
        } catch (SQLiteException ex) {
            Log.e(PlacesDbconstanst.History.LOG_TAG, "Create table exception: " +
                    ex.getMessage());

        }

        sql = "CREATE TABLE " + PlacesDbconstanst.Favorite.PLACES_TABLE_NAME +
                "(" + PlacesDbconstanst.Favorite.PLACES_ID + " INTEGER PRIMARY KEY autoincrement,"
                + PlacesDbconstanst.Favorite.PLACES_NAME + " TEXT,"
                + PlacesDbconstanst.Favorite.PLACES_ADDRESS + " TEXT,"
                + PlacesDbconstanst.Favorite.LOCATION + " TEXT,"
                + PlacesDbconstanst.Favorite.PLACES_DISTANEC + " REAL,"
                + PlacesDbconstanst.Favorite.PLACE_PHOTO + " TEXT)";
        try {
            db.execSQL(sql);
        } catch (SQLiteException ex) {
            Log.e(PlacesDbconstanst.Favorite.LOG_TAG, "Create table exception: " +
                    ex.getMessage());

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
