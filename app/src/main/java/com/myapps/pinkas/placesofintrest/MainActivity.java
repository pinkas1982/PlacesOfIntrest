package com.myapps.pinkas.placesofintrest;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.myapps.pinkas.placesofintrest.fragmants.MyMapFragment;
import com.myapps.pinkas.placesofintrest.internet.SearchPlacesServices;
import com.myapps.pinkas.placesofintrest.places.Places;
import com.myapps.pinkas.placesofintrest.utils.ClickListener;
import com.myapps.pinkas.placesofintrest.utils.PlacesFragmentListener;

public class MainActivity extends AppCompatActivity implements ClickListener,View.OnClickListener, PlacesFragmentListener {

    FragmentManager fm;
    MyMapFragment mapFragment;
    public static ProgressDialog progress;
    private PlacesAdapter placesAdapter;
    Button goBtn;
    EditText mySearch;
    Places places;

    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();


    }

    private void initComponent(){
        goBtn  = (Button) findViewById(R.id.placeButton);
        goBtn.setOnClickListener(this);
        mySearch = (EditText) findViewById(R.id.placeEditText);
        placesAdapter = new PlacesAdapter(cursor,this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.placeButton) {

            String textFromTextBox = mySearch.getText().toString();
            textFromTextBox = textFromTextBox.replaceAll(" ", "+");
            Intent intet = new Intent(this, SearchPlacesServices.class);
            //intet.setAction("myapp.example.com.action.SEARCH");
            intet.putExtra("search", textFromTextBox);
            startService(intet);

            waitProgressBar("please wait while loading places");
            //TODO: find by location....

            //do search and enter the results to  the db
        }
    }
//this method is showing a progressBar that tell the user the data is loading.
    public void waitProgressBar (String message){
        progress = new ProgressDialog(this);
        progress.setTitle("loading");
        progress.setMessage(message);
        progress.show();
    }


    @Override
    public void onPlaceClick(View view, int position) {
        Toast.makeText(this, "on click" + position, Toast.LENGTH_LONG).show();
        fm = getFragmentManager();
        // get the map object from the fragment:
        mapFragment = MyMapFragment.newInstance(places);

        FragmentTransaction ft = fm.beginTransaction();


        ft.replace(R.id.fragmantContainer, mapFragment, "map");
        ft.addToBackStack(null);

        ft.commit();
    }

    @Override
    public void onLocationSelected(Places places) {

    }
}



