
package com.uniovi.foxvid.vista;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

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
import com.uniovi.foxvid.vista.fragment.NewPostFragment;
import com.uniovi.foxvid.vista.fragment.PostFragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button btLogOut;




    private  List<Post> listPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Gestion de la botonera
        BottomNavigationView navView = findViewById(R.id.nav_view);
        //Le añado un listener
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        loadPostView();

        btLogOut = (Button)findViewById(R.id.idLogOut);




        btLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        User user = this.getIntent().getParcelableExtra(Login.USER_EMAIL);
        TextView email =  (TextView)findViewById(R.id.idEmail);
        email.setText(user.getEmail());





    }
    private void loadPostView(){
        //Creamos el framento de información
        PostFragment info = new PostFragment();
        Bundle args = new Bundle();
        info.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();

    }
    private void loadNewPostView(){
        //Creamos el framento de información
        NewPostFragment info = new NewPostFragment();
        Bundle args = new Bundle();
        info.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();

    }


    protected void logOut(){
        FirebaseAuth.getInstance().signOut();
        loadLogActivity();
    }
    private void loadLogActivity(){
        Intent intent = new Intent(this,Login.class);
        startActivity(intent);
        finish();
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_post:
                                loadPostView();
                        return true;
                    case R.id.nav_new_post:
                        loadNewPostView();
                        return true;


            }
            return false;
        }
    };
}