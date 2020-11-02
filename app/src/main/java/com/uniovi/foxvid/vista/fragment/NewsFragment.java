package com.uniovi.foxvid.vista.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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

public class NewsFragment extends Fragment {

    private Button btPost;
    private TextView txtPost;
    private Coordinate coordinate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root =  inflater.inflate(R.layout.activity_new_post, container, false);
        btPost = (Button)root.findViewById(R.id.idBtPost);
        txtPost = (TextView)root.findViewById(R.id.txtNewPost);
        coordinate = new Coordinate(0.0,0.0);
        updateLocate();

        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post();
                updateLocate();
            }
        });

        return root;
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
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
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