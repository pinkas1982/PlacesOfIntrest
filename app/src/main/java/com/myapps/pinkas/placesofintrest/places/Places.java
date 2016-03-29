package com.myapps.pinkas.placesofintrest.places;

/**
 * Created by pinkas on 3/20/2016.
 */
public class Places {

    private String placeName;
    private String placeAddress;
    private String location;
    private String photo;
    private double distance;

    public Places(double distance, String location, String photo, String placeAddress, String placeName) {
        this.distance = distance;
        this.location = location;
        this.photo = photo;
        this.placeAddress = placeAddress;
        this.placeName = placeName;
    }




    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}