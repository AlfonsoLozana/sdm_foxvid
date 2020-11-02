package com.uniovi.foxvid.vista;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

//        View root =  inflater.inflate(R.layout.fragment_new_post, container, false);
        btPost = (Button)findViewById(R.id.idBtPost);
        txtPost = (TextView)findViewById(R.id.txtNewPost);
        coordinate = new Coordinate(0.0,0.0);
        updateLocate();



        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post();
                updateLocate();
            }
        });

//        return root;
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
        posts.put("date", Timestamp.now());
        posts.put("lat", coordinate.getLat());
        posts.put("lon", coordinate.getLon());
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