package com.uniovi.foxvid.vista.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.uniovi.foxvid.ListaPostAdapter;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.modelo.Coordinate;
import com.uniovi.foxvid.modelo.Post;
import com.uniovi.foxvid.modelo.User;
import com.uniovi.foxvid.vista.MainActivity;
import com.uniovi.foxvid.vista.NewPostActivity;

import java.util.ArrayList;
import java.util.List;


public class PostFragment extends Fragment {

    public static final String POSTS = "posts";
    public static final String MAIN = "main";

    private List<Post> listPost;
    private Coordinate coordinate;

    RecyclerView listPostView;
    View root;
    FloatingActionButton btnNewPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateLocate();
        root =  inflater.inflate(R.layout.fragment_post, container, false);

        if(listPost == null) listPost= new ArrayList<Post>();
        coordinate = new Coordinate(0.0,0.0);
        listPostView = (RecyclerView) root.findViewById(R.id.idRvPost);


        Bundle args = getArguments();
        if (args != null) {

        }

        loadPost();


        //Floating button -> new post
        btnNewPost = root.findViewById(R.id.btnNewPost);

        btnNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNewPost();
            }
        });

        return root;
    }


    protected void loadPost(){
        listPostView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        listPostView.setLayoutManager(layoutManager);
        updateValues();
    }

    protected void loadNewPost(){
        Intent newPostIntent = new Intent(getActivity(), NewPostActivity.class);
        startActivity(newPostIntent);
    }

    private void updateValues(){
        List listActualPost = new ArrayList();
        for(Post p:listPost){
            listActualPost.add(p.getDate());
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("post")

                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    if(coordinate.checkDistancia(new Double(dc.getDocument().get("lat").toString()),new Double(dc.getDocument().get("lon").toString())))
                                        listPost.add(0,new Post(null,
                                                dc.getDocument().get("post").toString(),
                                                //public User(String uid, String name, String email, Uri photo)
                                                new User(dc.getDocument().get("userUid").toString(),null, dc.getDocument().get("userEmail").toString(), dc.getDocument().get("userImage").toString()),
                                                (Timestamp)dc.getDocument().get("date"),
                                                new Coordinate(new Double(dc.getDocument().get("lat").toString()),new Double(dc.getDocument().get("lat").toString()))
                                        ));
                                    listPostView.setAdapter(new ListaPostAdapter(listPost,null));
                                    break;
                                case MODIFIED:
                                    //Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    //Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

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