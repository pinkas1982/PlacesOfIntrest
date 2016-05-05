package com.myapps.pinkas.placesofintrest.fragmants;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.myapps.pinkas.placesofintrest.PlacesAdapter;
import com.myapps.pinkas.placesofintrest.R;

import com.myapps.pinkas.placesofintrest.places.Places;
import com.myapps.pinkas.placesofintrest.placesDb.PlacesDbHandler;
import com.myapps.pinkas.placesofintrest.placesDb.PlacesDbHelper;
import com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import static com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst.CurrentPlaces.*;
//import static com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst.Favorite.*;





public class PlacesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    static Places places;
    PlacesAdapter adapter;
    public FragmentManager fm;
    MyMapFragment mapFragment;
    RecyclerView rv;
    Cursor c;
    Button showFavoritebtn;
  //  ListFragmentListener myFragmentListener;
    AdapterView.AdapterContextMenuInfo palcesInfo;

    public PlacesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View myFragView = inflater.inflate(R.layout.fragment_places, container, false);
        rv = (RecyclerView) myFragView.findViewById(R.id.placesRecyclerView);
        final Cursor cursor = getActivity().getContentResolver().query(CONTENT_URI, null, null, null, null);

        adapter = new PlacesAdapter(cursor, getActivity());
        rv.setAdapter(adapter);
        getLoaderManager().initLoader(1, null, this);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.notifyDataSetChanged();

/*
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(myFragView.getWindowToken(), 0);*/
        return myFragView;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            //   listener = (ClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("context " + context.toString()
                    + "must implement PlacesFragmantListener!");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
       return new CursorLoader(getActivity(), CONTENT_URI, null, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
if(!cursor.moveToPosition((0)))
{
    adapter = new PlacesAdapter(cursor, getActivity());
    rv.setAdapter(adapter);
    rv.getAdapter().notifyItemRangeChanged(0,0);
}
        adapter = new PlacesAdapter(cursor, getActivity());
        cursor.setNotificationUri(getActivity().getContentResolver(), PlacesDbconstanst.CurrentPlaces.CONTENT_URI);
      //  rv.setAdapter(adapter);
        rv.swapAdapter(adapter, true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        rv.swapAdapter(null, true);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
     //   int position = -1;
        try {
     //       position = ((PlacesAdapter) rv.getAdapter()).getPosition();
        } catch (Exception e) {
            Log.d("", e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.share_id:
                // share the place
                shareIt();
                break;
            case R.id.waze_id:
                // open waze
                openWaze();
                break;
            case R.id.save_id:
                // save to favorite
                saveToFavorite();
                break;
            case R.id.favorite_id:
                // open favorite
                openFavorite();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void openFavorite() {
        final Cursor cursor = getActivity().getContentResolver().query(PlacesDbconstanst.Favorite.CONTENT_URI, null, null, null, null);
        adapter = new PlacesAdapter(cursor, getActivity());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        registerForContextMenu(rv);

    }

    //TODO i need to add a distance
    private void saveToFavorite() {
        Toast.makeText(getActivity(),"save favorite",Toast.LENGTH_LONG).show();

        c = adapter.cursor;
        c.moveToPosition(c.getPosition()-1);
        String name = c.getString(c.getColumnIndex(PlacesDbconstanst.CurrentPlaces.PLACES_NAME));
        String adr = c.getString(c.getColumnIndex(PlacesDbconstanst.CurrentPlaces.PLACES_ADDRESS));
        Places p = new Places(0,null,null,adr,name);
        PlacesDbHandler.addPlaceToFavorite(p, getActivity());

    }



    private void openWaze() {
        Cursor c = adapter.cursor;
        try
        {
            String url = "waze://?q=";
            String url1= c.getString(c.getColumnIndex(PlacesDbconstanst.CurrentPlaces.PLACES_NAME));
            String url2="&navigate=yes";
            String u = url + url1 + url2;
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( u ) );
            startActivity( intent );
        }
        catch ( ActivityNotFoundException ex  )
        {
            Intent intent =
                    new Intent( Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze") );
            startActivity(intent);
        }
    }
//TODO i need to add a picture of the place
//this is the share intent method
    private void shareIt() {
        c = adapter.cursor;
        c.moveToPosition(c.getPosition());
        String name = c.getString(c.getColumnIndex(PlacesDbconstanst.CurrentPlaces.PLACES_NAME));
        String adr = c.getString(c.getColumnIndex(PlacesDbconstanst.CurrentPlaces.PLACES_ADDRESS));
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "check out this place"+", "+ name +", " + adr);
        sharingIntent.setType("text/plain");
        startActivity(Intent.createChooser(sharingIntent, getResources().getText(R.string.send_to)));

    }

}
