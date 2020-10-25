package com.uniovi.foxvid.modelo;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniovi.foxvid.ListaPostAdapter;

public class Post implements Parcelable {

    private String title;
    private String text;
    private User user;
    private Timestamp date;
    private Coordinate localization;

    public Post() {
    }

    public Post(String title, String text, User user, Timestamp  date, Coordinate localization) {
        this.title = title;
        this.text = text;
        this.user = user;
        this.date = date;
        this.localization = localization;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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
        parcel.writeString(title);
        parcel.writeString(text);
        parcel.writeParcelable(user,i);
        parcel.writeParcelable(date,i);
        parcel.writeParcelable(localization,i);
    }



}
