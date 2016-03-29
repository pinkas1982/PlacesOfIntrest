package com.myapps.pinkas.placesofintrest;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.myapps.pinkas.placesofintrest.internet.GoogleAccess;
import com.myapps.pinkas.placesofintrest.places.Places;
import static com.myapps.pinkas.placesofintrest.placesDb.PlacesDbconstanst.CurrentPlaces.*;

//import static com.myapps.pinkas.placesofintrest.constans.PlacesContract.Places.*;


/**
 * Created by pinkas on 3/21/2016.
 */
public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceHolder> {

    private Cursor cursor;
    private Context context;
    private static TextView placeName, address, distance, url;
    public  static ImageView imgplace;
    public  static PlaceHolder.ClickListener clickListener;
    private  static Places place;
    private DataSetObserver mDataSetObserver;
    private boolean mDataValid;



    public PlacesAdapter(Cursor cursor, Context context) {
        this.context = context;
        this.cursor = cursor;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (cursor != null) {
            cursor.registerDataSetObserver(mDataSetObserver);
        }

    }

   @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //   cursor.setNotificationUri(context.getContentResolver(), CONTENT_URI);
        LayoutInflater inflater = LayoutInflater.from(context);
        View myView = inflater.inflate(R.layout.single_place, parent, false);


        PlaceHolder placeHolder = new PlaceHolder(myView, new PlaceHolder.PlacesFragmantListener() {
            @Override
            public void onLocationSelected(Places places) {

            }
        });

        return placeHolder;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public void onBindViewHolder(PlacesAdapter.PlaceHolder holder, int position) {

        if (cursor.moveToPosition(position)) {


            int column_number = cursor.getColumnIndex(PLACES_NAME);
            String name = cursor.getString(column_number);
            placeName.setText(name);

            int column_number2 = cursor.getColumnIndex(PLACES_ADDRESS);
            String adr = cursor.getString(column_number2);
            address.setText(adr);

            int column_number3 = cursor.getColumnIndex(PLACES_DISTANEC);
            String dis = cursor.getString(column_number3);
            distance.setText(dis);

            int column_number4 = cursor.getColumnIndex(PLACE_PHOTO);
            String photo = cursor.getString(column_number4);

            if(!photo.equals(""))
            {
                GoogleAccess.myImageDownloader loader= new GoogleAccess.myImageDownloader(imgplace);
                loader.execute(photo);
            }

        }

    }

        public void setClickListener(PlaceHolder.ClickListener clickListener){
             this.clickListener = clickListener;
}

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }



    public static class PlaceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        PlacesFragmantListener listener;

        public PlaceHolder(View itemView, PlacesFragmantListener placesFragmantListener) {
            super(itemView);
            listener = placesFragmantListener;
            placeName = (TextView) itemView.findViewById(R.id.placeNametextView);
            address = (TextView) itemView.findViewById(R.id.addressTextView);
            distance = (TextView) itemView.findViewById(R.id.distanceTextView);
            imgplace = (ImageView) itemView.findViewById(R.id.placesImageViewId);
            imgplace.setOnClickListener(this);
            itemView.setOnClickListener(this);


        }
        RecyclerView rv;
        @Override
        public void onClick(View v) {

            if(clickListener!=null){
                clickListener.itemClicked(v, getPosition());
            }

        }
        public static interface PlacesFragmantListener {
            void onLocationSelected(Places places);
        }

        public interface ClickListener{
            public void itemClicked (View view, int position);
        }
    }
    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();

        }
    }


}
