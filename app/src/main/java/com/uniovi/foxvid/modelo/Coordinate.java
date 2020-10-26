package com.uniovi.foxvid.modelo;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class Coordinate implements Parcelable {

    public static float  DISTANCE = 4000;
    private Double lat;
    private Double lon;


    public Coordinate(Double lat, Double lon){
        this.lon = lon;
        this.lat = lat;
    }


    protected Coordinate(Parcel in) {
        if (in.readByte() == 0) {
            lat = null;
        } else {
            lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            lon = null;
        } else {
            lon = in.readDouble();
        }
    }

    public static final Creator<Coordinate> CREATOR = new Creator<Coordinate>() {
        @Override
        public Coordinate createFromParcel(Parcel in) {
            return new Coordinate(in);
        }

        @Override
        public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }
    };

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
        //return  locationValue1.distanceTo(locationValue) < DISTANCE ;
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(lon);
        parcel.writeDouble(lat);
    }
}



