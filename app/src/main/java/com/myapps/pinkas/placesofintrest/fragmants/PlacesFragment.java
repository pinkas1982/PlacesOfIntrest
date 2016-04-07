package com.myapps.pinkas.placesofintrest.fragmants;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.myapps.pinkas.placesofintrest.PlacesAdapter;
import com.myapps.pinkas.placesofintrest.R;

import com.myapps.pinkas.placesofintrest.places.Places;
import com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import static com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst.CurrentPlaces.*;


public class PlacesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {


    static Places places;
    PlacesAdapter adapter;
    public FragmentManager fm;
    MyMapFragment mapFragment;
    RecyclerView rv;


    public PlacesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myFragView = inflater.inflate(R.layout.fragment_places, container, false);
        rv = (RecyclerView) myFragView.findViewById(R.id.placesRecyclerView);
        //this create the line beetween every list to do so i have import to the build gradle a flexible divider
        rv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(Color.BLACK).build());

        final Cursor cursor = getActivity().getContentResolver().query(CONTENT_URI, null, null, null, null);
        adapter = new PlacesAdapter(cursor, getActivity());
        //   adapter.setClickListener(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

       /* // record this value before making any changes to the existing list
        int curSize = adapter.getItemCount();

        // replace this line with wherever you get new records
        Cursor newItems = getActivity().getContentResolver().query(CONTENT_URI, null, null, null, null);

        // update the existing list
        rv.addAll(newItems);
        // curSize should represent the first element that got added
        // newItems.size() represents the itemCount
        adapter.notifyItemRangeInserted(curSize, newItems.size());*/


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
        //  CursorLoader c=getContext().getContentResolver().query(PlacesContract.Places.CONTENT_URI, null, null,null, null);

        return new CursorLoader(getActivity(), CONTENT_URI, null, null, null, null);
    }

    //TODO: i need to write this method
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.setNotificationUri(getActivity().getContentResolver(), PlacesDbconstanst.CurrentPlaces.CONTENT_URI);
        adapter = new PlacesAdapter(cursor, getActivity());
        //   adapter.setClickListener(this);
        rv.setAdapter(adapter);
        //  adapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //      adapter.swapCursor(null);
    }

    //as soon as i click on an item in the recyclerview it will open a map with the location
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


    }

}
