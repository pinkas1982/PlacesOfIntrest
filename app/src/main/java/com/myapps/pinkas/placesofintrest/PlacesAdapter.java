package com.myapps.pinkas.placesofintrest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.myapps.pinkas.placesofintrest.internet.GoogleAccess;
import com.myapps.pinkas.placesofintrest.places.Places;
import com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst;
import com.myapps.pinkas.placesofintrest.utils.PlacesFragmentListenerr;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static android.support.v4.app.ActivityCompat.startActivity;
import static com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst.CurrentPlaces.*;

//import static com.myapps.pinkas.placesofintrest.constans.PlacesContract.Places.*;


/**
 * Created by pinkas on 3/21/2016.
 */
public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceHolder>  {

    public Cursor cursor;
    private static Context context;
    private static TextView placeName, address, distance;
    public static ImageView imgplace;
    public static PlaceHolder.ClickListener clickListener;
    private static Places place;
    private DataSetObserver mDataSetObserver;
    private boolean mDataValid;

    AdapterView.AdapterContextMenuInfo palcesInfo;


    public double d;

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public PlacesAdapter(Cursor cursor, Context context) {
        this.context = context;
        this.cursor = cursor;

    }

    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View myView = inflater.inflate(R.layout.single_place, parent, false);

        PlaceHolder placeHolder = new PlaceHolder(myView, new PlaceHolder.PlacesFragmantListener() {
            @Override
            public void onLocationSelected(Places places) {

            }
        }, cursor);

        return placeHolder;
    }


    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public void onBindViewHolder(final PlacesAdapter.PlaceHolder holder, int position) {

        if (cursor.moveToPosition(position)) {


            int column_number = cursor.getColumnIndex(PLACES_NAME);
            String name = cursor.getString(column_number);
            Log.e("place name", name);
            placeName.setText(name);

            int column_number2 = cursor.getColumnIndex(PLACES_ADDRESS);
            String adr = cursor.getString(column_number2);
            address.setText(adr);


            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String distanceUnitStr = pref.getString(PlacesDbconstanst.DISTANCE_UNIT, PlacesDbconstanst.KM);
            int column_number3 = cursor.getColumnIndex(PLACES_DISTANEC);
            Double dis = cursor.getDouble(column_number3);
            NumberFormat formatter = new DecimalFormat("#0.00");
            String distanceStr = formatter.format(dis);
            distance.setText(distanceStr);


            int column_number4 = cursor.getColumnIndex(PLACE_PHOTO);
            String photo = cursor.getString(column_number4);
            if(photo!=null) {
                if (!photo.equals("")) {
                    GoogleAccess.myImageDownloader loader = new GoogleAccess.myImageDownloader(imgplace);
                    loader.execute(photo);
                }
            }

        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });

    }



    public void setClickListener(PlaceHolder.ClickListener clickListener) {
        this.clickListener = clickListener;
    }


    @Override
    public int getItemCount() {
        return cursor.getCount();
    }



    public static class PlaceHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener {

        PlacesFragmantListener listener;
        Cursor c;

        public PlaceHolder(View itemView, PlacesFragmantListener placesFragmantListener, Cursor cursor) {
            super(itemView);
            listener = placesFragmantListener;
            placeName = (TextView) itemView.findViewById(R.id.placeNametextView);
            address = (TextView) itemView.findViewById(R.id.addressTextView);
            distance = (TextView) itemView.findViewById(R.id.distanceTextView);
            imgplace = (ImageView) itemView.findViewById(R.id.placesImageViewId);
            itemView.setOnClickListener(this);
            c = cursor;
            itemView.setOnCreateContextMenuListener(this);

        }
  @Override
        public void onClick(View v) {
            PlacesFragmentListenerr listenerr = (PlacesFragmentListenerr) context;
            if (c.moveToPosition(getPosition())) {
                Places p = new Places(0, c.getString(5), null, null, null);
                listenerr.onLocationSelected(p);
                if (clickListener != null) {
                    clickListener.itemClicked(v, getPosition());
                }
            }
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle("Select The Action");
            menu.add(Menu.NONE, R.id.share_id,
                    Menu.NONE, R.string.share_place);
            menu.add(Menu.NONE, R.id.waze_id,
                    Menu.NONE, R.string.open_waze);
            menu.add(Menu.NONE, R.id.save_id,
                    Menu.NONE, R.string.save_favorite);
            menu.add(Menu.NONE, R.id.favorite_id,
                    Menu.NONE, R.string.open_favorite);

        }


        public static interface PlacesFragmantListener {
            void onLocationSelected(Places places);
        }

        public interface ClickListener {
            public void itemClicked(View view, int position);
        }
    }


}

