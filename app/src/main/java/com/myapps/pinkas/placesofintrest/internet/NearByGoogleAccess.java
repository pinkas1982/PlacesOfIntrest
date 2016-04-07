package com.myapps.pinkas.placesofintrest.internet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by pinkas on 4/3/2016.
 */
public class NearByGoogleAccess {

    public static String searchPlaceNearBy(String q) {
        String url3 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="; //search by location
        String url4 = "&radius=500&";
        String urlKey = "key=AIzaSyBrsPDbnaQFd4aHAUNFkpwQZDtWnK0-zw0";
        String nearByUrl = url3 + q + url4 + urlKey;

        BufferedReader input = null;
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();

        try {
            //create a url:
            URL url = new URL(nearByUrl);

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

}
