package com.myapps.pinkas.placesofintrest.placesDb;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by pinkas on 3/21/2016.
 */
public class PlacesProvider extends ContentProvider {

    PlacesDbHelper placesDbHelper;

    @Override
    public boolean onCreate() {

        placesDbHelper = new PlacesDbHelper(getContext());


        return false;
    }


    protected String getTableName(Uri uri) {
        List<String> pathSegments = uri.getPathSegments();
        return pathSegments.get(0);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String currentTableToSearch= getTableName(uri);

        //here we can interfere and change values of the query
        //if(selection.equals)...


        Cursor c=    placesDbHelper.getReadableDatabase().query(currentTableToSearch, projection,selection ,selectionArgs,null, null, sortOrder );

     //   getContext().getContentResolver().notifyChange(uri, null);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {


        //the provided is requested to insert data - so we'll put it in the db:
        SQLiteDatabase db = placesDbHelper.getWritableDatabase();
        long id = db.insertWithOnConflict(getTableName(uri), null, values, SQLiteDatabase.CONFLICT_REPLACE);

		/*
         * NOTE : even though it's a getWritableDatabase - we won't close it here
		 * if we do, we'll raise errors later when trying to re-query the data.
		 * it's OK. it works.
		 * the db will get closed the provider is closed.
		 */

        // notify the change

        getContext().getContentResolver().notifyChange(uri, null);

        // this methods has to return the inserted row's Uri
        // that uri is the given uri (the table uri - content://com.example.demo_providers.provider)
        // and return the specific uri (content://com.example.demo_providers.provider/id)
        // which is just the same uri but with an appended id.

        if (id > 0) {
            return ContentUris.withAppendedId(uri, id);
        } else {
            //or null if nothing was inserted
            return null;
        }


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //the provided is requested to delete data - so we'll delete it in the db:

        SQLiteDatabase db = placesDbHelper.getWritableDatabase();
        int result = db.delete(getTableName(uri), selection, selectionArgs);

		/*
		 * NOTE : even though it's a getWritableDatabase - we won't close it here
		 * if we do, we'll raise errors later when trying to re-query the data.
		 * it's OK. it works.
		 * the db will get closed the provider is closed.
		 */

        // notify the change
        getContext().getContentResolver().notifyChange(uri, null);

        //return the number of rows deleted
        //it's what we got from the db.delete
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
