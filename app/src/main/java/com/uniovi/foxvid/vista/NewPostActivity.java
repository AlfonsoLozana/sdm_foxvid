package com.uniovi.foxvid.vista;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.modelo.Coordinate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {

    private Button btPost;
    private TextView txtPost;
    private Coordinate coordinate;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

//        Toolbar myToolbar = (Toolbar) findViewById(R.id.topAppBarNewPost);
//        setSupportActionBar(myToolbar);

        toolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        View root =  inflater.inflate(R.layout.fragment_new_post, container, false);
        //btPost = (Button)findViewById(R.id.btn_publish_post);
        txtPost = (TextView)findViewById(R.id.txtNewPost);
        coordinate = new Coordinate(0.0,0.0);
        updateLocate();



//        btPost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                post();
//                updateLocate();
//            }
//        });

//        return root;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_post_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.home:
                this.finish();
                return true;

            case R.id.btn_publish_post:
                post();
                this.finish();
                return true;
            default:
                return false;
        }
    }


    private void post(){
        //updateLocate();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uuid = UUID.randomUUID().toString();
        Map<String, Object> posts = new HashMap<>();
        posts.put("uid",uuid );
        posts.put("post", txtPost.getText().toString());
        posts.put("userUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        posts.put("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        posts.put("userImage", FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
        posts.put("date", Timestamp.now());
        posts.put("lat", coordinate.getLat());
        posts.put("lon", coordinate.getLon());
        posts.put("nLikes", 0);
        posts.put("nDislikes", 0);
        txtPost.setText("");


        db.collection("post").document(uuid)
                .set(posts)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void updateLocate() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            coordinate.setLat(location.getLatitude());
                            coordinate.setLon(location.getLongitude());

                        }

                    }
                });

    }





}