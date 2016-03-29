package com.myapps.pinkas.placesofintrest.placesDb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.myapps.pinkas.placesofintrest.places.Places;

import java.util.ArrayList;
import java.util.List;

import static com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst.CurrentPlaces.*;
import static com.myapps.pinkas.placesofintrest.places.Places.*;


/**
 * Created by pinkas on 3/20/2016.
 */
public class PlacesDbHandler {

    private PlacesDbHelper placesDbHelper;

    public PlacesDbHandler(Context context) {
        placesDbHelper = new PlacesDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //add a new place to the Db
    public static void addPlace(Places places, Context context) {
     //   SQLiteDatabase db = placesDbHelper.getWritableDatabase();
        PlacesDbHelper db = new PlacesDbHelper(context);
        ContentValues newPlaceValues = new ContentValues();
        newPlaceValues.put(PlacesDbconstanst.CurrentPlaces.PLACES_NAME, places.getPlaceName());
        newPlaceValues.put(PlacesDbconstanst.CurrentPlaces.PLACES_ADDRESS, places.getPlaceAddress());
        newPlaceValues.put(PlacesDbconstanst.CurrentPlaces.PLACES_DISTANEC, places.getDistance());
        newPlaceValues.put(PlacesDbconstanst.CurrentPlaces.PLACE_PHOTO, places.getPhoto());
        newPlaceValues.put(PlacesDbconstanst.CurrentPlaces.LOCATION, places.getLocation());

        try {
            db.getWritableDatabase().insertOrThrow(PLACES_TABLE_NAME, null, newPlaceValues);
        }catch (SQLiteException ex) {
            Log.e(LOG_TAG, ex.getMessage());
            throw ex;
        } finally {
            db.close();
        }
    }
    //update movie on the Db
    public void updateMovieList(Places places) {
        SQLiteDatabase db = placesDbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(PLACES_NAME, places.getPlaceName());

        } catch (SQLiteException ex) {
            Log.e(LOG_TAG, ex.getMessage());
            throw ex;
        }
        finally {
            db.close();
        }
    }

    //get all places from the Db


    public List<Places> getAllPlaces() {
        List<Places> movieList = new ArrayList<Places>();
        SQLiteDatabase db = placesDbHelper.getReadableDatabase();

        Cursor cursor = db.query(PLACES_TABLE_NAME, null, null,
                null, null, null, null, null);


        while (cursor.moveToNext()) {
            int placesId = cursor.getInt(0);
            String placeName = cursor.getString(1);
            String placeAddress = cursor.getString(2);
            String distance = cursor.getString(3);


         /*   Places newPlace = new Places(placeName, placeAddress, distance);
            movieList.add(newPlace);*/

        }
        db.close();
        return movieList;

    }
//TODO: write a method which delet a single place
    // Delete a place from the Db
    public void deletePlaces(Places place) {
        SQLiteDatabase db = placesDbHelper.getWritableDatabase();
        try {
         /*   db.delete(PLACES_TABLE_NAME, PLACES_ID + "=?",
                   new String[] { String.valueOf(place.getplacesId()) } );*/
        } catch (SQLiteException ex) {
       //     Log.e(LOG_TAG, ex.getMessage());
            throw ex;
        }
        finally {
            db.close();
        }
    }
    //delet all places from db
    public void deletAllPlaces() {
        SQLiteDatabase db = placesDbHelper.getWritableDatabase();
        db.execSQL("DELETE from " + PLACES_TABLE_NAME);
    }
}
