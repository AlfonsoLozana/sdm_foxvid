package com.uniovi.foxvid.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;


import com.google.android.material.bottomnavigation.BottomNavigationView;


import com.squareup.picasso.Picasso;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.SettingsActivity;
import com.uniovi.foxvid.modelo.Post;
import com.uniovi.foxvid.modelo.User;

import com.uniovi.foxvid.vista.fragment.NewsFragment;
import com.uniovi.foxvid.vista.fragment.PostFragment;
import com.uniovi.foxvid.vista.fragment.StatisticsFragment;
import com.uniovi.foxvid.vista.igu.CircleTransform;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    //get access to location permission
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    boolean hasAccessToLocation = false;

    private Button btLogOut;

    private List<Post> listPost;
    private Toolbar toolbar;
    private ImageButton btProfile;
    private ImageButton btSettings;
    private FirebaseAuth mAuth;

    private User user;
    Dialog customDialog = null;


    //////// ALfonso //////
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        user = new User(mAuth.getCurrentUser());

        //Gestion de la botonera
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        btProfile = (ImageButton) findViewById(R.id.btProfile);


        Picasso.get().load(user.getPhoto()).fit().transform(new CircleTransform()).into(btProfile);

        btProfile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openPopUpWindow();
                    }
                }
        );

        btSettings = (ImageButton) findViewById(R.id.btSettings);
        btSettings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);

                        startActivity(intentSettings);
                    }
                }
        );


        //Metodo que carga los posts si se dan permisos de ubicacion
//        hasAccessToLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        locationRequest = new LocationRequest();
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//            }
//        };
//
//        getLocation();
        askForPermissions();
    }

    public void askForPermissions(){
        hasAccessToLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
            }
        };

        getLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasAccessToLocation=true;
                    getLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(this, R.string.location_permission_not_given_message, Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //Get location
    public void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
        else {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            loadPostView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.bar_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.settings){
            Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intentSettings);

        }
        return super.onOptionsItemSelected(item);

    }

    private void openPopUpWindow(){
        customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(true);
        customDialog.setContentView(R.layout.fragment_log_out);
        customDialog.getWindow().setLayout(1050,600);

        TextView titulo = (TextView) customDialog.findViewById(R.id.txtUserDialog);
        ImageView imagenUser = (ImageView) customDialog.findViewById(R.id.idImgUserDialog);

        if(user!=null){
            Picasso.get().load(user.getPhoto()).fit().into(imagenUser);
            titulo.setText(user.getName());
        }

        else
            titulo.setText("Usuario");


        ((Button) customDialog.findViewById(R.id.btnLogOut)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                logOut();

            }
        });

        ((Button) customDialog.findViewById(R.id.cancelar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.cancel();
            }
        });

        customDialog.show();
    }

    private void loadPostView() {
        //Creamos el framento de información
        PostFragment info = new PostFragment();
        Bundle args = new Bundle();
        info.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();
        btSettings.setVisibility(View.VISIBLE);

    }

    private void loadNewsView() {
        NewsFragment info = new NewsFragment();
        Bundle args = new Bundle();
        info.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();
        btSettings.setVisibility(View.INVISIBLE);
    }

    private void loadStatistics() {
        StatisticsFragment info = new StatisticsFragment();
        Bundle args = new Bundle();
        info.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();
        btSettings.setVisibility(View.INVISIBLE);
    }

    protected void logOut() {
        FirebaseAuth.getInstance().signOut();
        loadLogActivity();
    }

    private void loadLogActivity() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_post:
                    if(hasAccessToLocation)
                        loadPostView();
                    return true;
                case R.id.nav_statistics:
                    loadStatistics();
                    return true;
                case R.id.nav_news:
                    loadNewsView();
                    return true;

            }
            return false;
        }
    };
}