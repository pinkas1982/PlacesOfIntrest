package com.myapps.pinkas.placesofintrest.fragmants;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.myapps.pinkas.placesofintrest.PlacesAdapter;
import com.myapps.pinkas.placesofintrest.R;
//import com.myapps.pinkas.placesofintrest.constans.PlacesContract;
import com.myapps.pinkas.placesofintrest.places.Places;
import com.myapps.pinkas.placesofintrest.utils.ClickListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import static com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst.CurrentPlaces.*;
//import static com.myapps.pinkas.placesofintrest.constans.PlacesContract.Places.*;


public class PlacesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> , AdapterView.OnItemClickListener{

    ClickListener listener;
    static Places places;
    PlacesAdapter adapter;
    public FragmentManager fm;
    MyMapFragment mapFragment;

    public PlacesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myFragView= inflater.inflate(R.layout.fragment_places, container, false);


        RecyclerView rv= (RecyclerView)myFragView.findViewById(R.id.placesRecyclerView);
        //this create the line beetween every list to do so i have import to the build gradle a flexible divider
        rv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(Color.BLACK).build());

        Cursor cursor = getContext().getContentResolver().query(CONTENT_URI,null, null, null, null);

        adapter= new PlacesAdapter(cursor, getContext());
     //   adapter.setClickListener(this);
        rv.setAdapter(adapter);
       // adapter.notifyItemRangeChanged(getId(), list.size());
       /* adapter.notifyItemRemoved(getId());
        adapter.notifyDataSetChanged();*/

       // adapter.notifyDataSetChanged();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rv, new ClickListener() {

                    @Override
                    public void onPlaceClick(View view, int position) {
                        //when i click on a place it will go to the map fragment
                        Toast.makeText(getActivity(), "on click" + position, Toast.LENGTH_LONG).show();
                        FragmentManager fm = getFragmentManager();

                        // get the map object from the fragment:
                        mapFragment = MyMapFragment.newInstance(places);

                        FragmentTransaction ft = fm.beginTransaction();


                        ft.replace(R.id.fragmantContainer, mapFragment, "map");
                        ft.addToBackStack(null);

                        ft.commit();

                    }
                })
        );
     //   rv.indexOfChild(getView());

//        rv.removeViewAt(getId());


    //    adapter.notifyItemRangeChanged(getId(), list.size());



     //   getLoaderManager().initLoader(1, null,);



        return myFragView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener =  (ClickListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException("context " + context.toString()
                    + "must implement PlacesFragmantListener!");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //  CursorLoader c=getContext().getContentResolver().query(PlacesContract.Places.CONTENT_URI, null, null,null, null);

        return new CursorLoader(getActivity(),CONTENT_URI,  null, null,null, null );
    }
//TODO: i need to write this method
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

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



    public static interface PlacesFragmantListener {
       public  void onLocationSelected(Places places);
    }
//this class handel the on click listener
     class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private ClickListener clickListener;
        MyMapFragment mapFragment;

        public RecyclerTouchListener(Context context, RecyclerView recyclerView, ClickListener clickListener){
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                }
            });
            this.clickListener = clickListener;
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clickListener != null && gestureDetector.onTouchEvent(e)){
                clickListener.onPlaceClick(child, rv.getChildPosition(child));

            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            Log.d("PlaceAdapter", "onTouchEvent"+e);
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


}

/*    public static interface ClickListener{
        public void onClick(View view, int position);
     //   public void onLongClick(View view, int position);
    }*/
}
