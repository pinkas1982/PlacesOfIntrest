package com.myapps.pinkas.placesofintrest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.myapps.pinkas.placesofintrest.placesDb.PlacesDbHandler;
import com.myapps.pinkas.placesofintrest.placesDb.PlacesDbHelper;

import java.net.URI;
import java.util.List;

/**
 * Created by pinkas on 3/24/2016.
 */
public class PlacesProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return false;
    }

    String getTableName (Uri uri){
        List<String>pathsegmets = uri.getPathSegments();
        return  pathsegmets.get(0);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
       PlacesDbHelper helper = new PlacesDbHelper(getContext());
       Cursor cursor = helper.getReadableDatabase().query(getTableName(uri),projection,selection,selectionArgs, null,null, sortOrder );
        getContext().getContentResolver().notifyChange(uri,null);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        getContext().getContentResolver().notifyChange(uri,null);

        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        getContext().getContentResolver().notifyChange(uri,null);

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        PlacesDbHelper helper = new PlacesDbHelper(getContext());
        helper.getWritableDatabase().delete(getTableName(uri), selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return 0;
    }




    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        getContext().getContentResolver().notifyChange(uri,null);

        return 0;
    }
}
