package com.uniovi.foxvid.modelo;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class Post implements Parcelable {

    private String uuid;
    private String text;
    private User user;
    private Timestamp date;
    private Coordinate localization;
    private int nLikes;
    private int nDislikes;

    public Post() {
    }

    public Post(String uuid, String text, User user, Timestamp  date , Coordinate localization, int nLikes, int nDislikes) {
        this.uuid = uuid;
        this.text = text;
        this.user = user;
        this.date = date;
        this.localization = localization;
        this.nDislikes = nDislikes;
        this.nLikes = nLikes;
    }


    protected Post(Parcel in) {
        uuid = in.readString();
        text = in.readString();
        nLikes = in.readInt();
        nDislikes = in.readInt();
        user = in.readParcelable(User.class.getClassLoader());
        date = in.readParcelable(Timestamp.class.getClassLoader());
        localization = in.readParcelable(Coordinate.class.getClassLoader());
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getUuid() {
        return uuid;
    }

    public int getnDislikes() {
        return nDislikes;
    }

    public int getnLikes() {
        return nLikes;
    }

    public void setnDislikes(int nDislikes) {
        this.nDislikes = nDislikes;
    }

    public void setnLikes(int nLikes) {
        this.nLikes = nLikes;
    }

    public Coordinate getLocalization() {
        return localization;
    }

    public void setLocalization(Coordinate localization) {
        this.localization = localization;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeString(text);
        parcel.writeInt(nLikes);
        parcel.writeInt(nDislikes);
        parcel.writeParcelable(user,i);
        parcel.writeParcelable(date,i);
        parcel.writeParcelable(localization,i);
    }



}