package com.myapps.pinkas.placesofintrest.internet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created by pinkas on 3/21/2016.
 */
public class GoogleAccess {

    public static String searchPlace(String q) {

        String url1 = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";//search by text
        String url2 = "&sensor=false&key=AIzaSyASuOm2zEVuVChtd647eWMKze_VsbjbqFo";
        String completeURL = url1 + q + url2;


        // String completeURL= url1+q+url2+urlKey;// need to overWrite this


        BufferedReader input = null;
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();

        try {
            //create a url:
            URL url = new URL(completeURL);

            //create a connection and open it:
            connection = (HttpURLConnection) url.openConnection();

            //status check:
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                //connection not good - return.
                return null;
            }

            //get a buffer reader to read the data stream as characters(letters)
            //in a buffered way.
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            //go over the input, line by line
            String line = "";
            while ((line = input.readLine()) != null) {
                //append it to a StringBuilder to hold the
                //resulting string
                response.append(line + "\n");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    //must close the reader
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                //must disconnect the connection
                connection.disconnect();
            }
        }

        //return the collected string:
        // this will be returned to : onPostExecute(String result)
        return response.toString();


    }

    public static class myImageDownloader extends AsyncTask<String, Void, Bitmap> {

        ImageView imgToChange;

        public myImageDownloader(ImageView imgToChange) {
            this.imgToChange = imgToChange;
        }


        @Override
        protected Bitmap doInBackground(String[] params) {

            Bitmap b = getBitmapFromURL(params[0]);

            return b;

        }

        Bitmap getBitmapFromURL(String src) {
            try {
                System.out.printf("src", src);
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                System.out.printf("Bitmap", "returned");
                myBitmap = Bitmap.createScaledBitmap(myBitmap, 100, 100, false);//This is only if u want to set the image size.
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.printf("Exception", e.getMessage());
                return null;
            }
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {

            imgToChange.setImageBitmap(bitmap);

            super.onPostExecute(bitmap);
        }
    }

}

