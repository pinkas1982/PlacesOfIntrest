package com.myapps.pinkas.placesofintrest.internet;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.myapps.pinkas.placesofintrest.MainActivity;
import com.myapps.pinkas.placesofintrest.PlacesAdapter;
import com.myapps.pinkas.placesofintrest.internet.GoogleAccess;
import com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchPlacesServices extends IntentService {

    public static final String URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&photoreference=";
    public static final String KEY = "&key=AIzaSyBrsPDbnaQFd4aHAUNFkpwQZDtWnK0-zw0";

    PlacesAdapter adapter;

    public SearchPlacesServices() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        String query= intent.getStringExtra("search");
        query = query.replace(" ","+");
        String JSON = GoogleAccess.searchPlace(query);

        //before parsing the JSON let's delete all records
        getContentResolver().delete(PlacesDbconstanst.CurrentPlaces.CONTENT_URI, null, null);

        try {

            JSONObject completeResponseObject = new JSONObject(JSON);
            JSONArray arrayFromObject=   completeResponseObject.getJSONArray("results");

            for(int i=0; i<arrayFromObject.length();i++)
            {
                JSONObject currentObject =arrayFromObject.getJSONObject(i);
                String name=  currentObject.getString("name");
                String address=   currentObject.getString("formatted_address");
                JSONObject geometryObject = currentObject.getJSONObject("geometry");
                JSONObject locationObject = geometryObject.getJSONObject("location");
                String lat = locationObject.getString("lat");
                String lng = locationObject.getString("lng");
                String location = ("lat:" + lat + ","+ "lng:"+ lng);
                Log.d("myapp", name + " " + address);


                String photoURL ="";
               //trying to get photos there is a case when there are no photos.
                try {
                    JSONArray photosArray = currentObject.getJSONArray("photos");
                    JSONObject photo1 = photosArray.getJSONObject(0);
                    photoURL = photo1.getString("photo_reference");
                    photoURL = URL + photoURL + KEY;
                } catch (JSONException ex){
                       Log.e("error", ex.getMessage());
                    }


                ContentValues arguments= new ContentValues();
                arguments.put(PlacesDbconstanst.CurrentPlaces.PLACES_NAME, name);
                arguments.put(PlacesDbconstanst.CurrentPlaces.PLACES_ADDRESS, address);
                arguments.put(PlacesDbconstanst.CurrentPlaces.PLACES_DISTANEC, location);
                arguments.put(PlacesDbconstanst.CurrentPlaces.PLACE_PHOTO, photoURL);

                getContentResolver().insert(PlacesDbconstanst.CurrentPlaces.CONTENT_URI, arguments);
                MainActivity.progress.dismiss();

            }
        }
        catch(Exception ee)
        {
            ee.printStackTrace();
        }

    }


}
