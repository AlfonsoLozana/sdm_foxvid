package com.uniovi.foxvid.modelo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;
import com.uniovi.foxvid.controlador.auth.DatabaseConnection;

public class User implements Parcelable {

    private String uid;
    private String name;
    private String email;
    private String photo;

    public User() {
    }

    public User(String uid, String name, String email, String photo) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.photo = photo;
    }

    public User(FirebaseUser currentUser) {
        if(currentUser != null) {
            this.uid = currentUser.getUid();
            this.name = currentUser.getDisplayName();
            this.email = currentUser.getEmail();
            this.photo = currentUser.getPhotoUrl().toString(); //FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()
        }
    }

    protected User(Parcel in) {
        uid = in.readString();
        name = in.readString();
        email = in.readString();
        photo = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(photo);
    }

    public void setUser(FirebaseUser currentUser) {

    }

    public void saveUser() {
        DatabaseConnection db = new DatabaseConnection();
        db.addUserDB(this);
    }
}