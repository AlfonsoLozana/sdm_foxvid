package com.uniovi.foxvid.modelo;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;

import com.uniovi.foxvid.MainActivity;

import java.util.Locale;

public class Coordinate {

    public static float  DISTANCE = 4000;
    private Double lat;
    private Double lon;


    public Coordinate(Double lat, Double lon){
        this.lon = lon;
        this.lat = lat;
    }



    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLat(Double lat) {

        this.lat = lat;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }


    public boolean checkDistancia(Double lat1, Double lon1) {
        Location locationValue = new Location("location value.");
        locationValue.setLatitude(getLat());
        locationValue.setLongitude(getLon());
        Location locationValue1 = new Location("location value.");
        locationValue1.setLatitude(lat1);
        locationValue1.setLongitude(lon1);
        System.out.println("---------------------------------");
        System.out.println(locationValue1.distanceTo(locationValue));
        System.out.println(locationValue1);
        System.out.println(locationValue);
        return  locationValue1.distanceTo(locationValue) < DISTANCE ;
    }
}



