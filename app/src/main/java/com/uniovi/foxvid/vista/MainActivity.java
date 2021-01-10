package com.uniovi.foxvid.vista;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.uniovi.foxvid.utils.LocationHandler;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.modelo.User;
import com.uniovi.foxvid.utils.SettingsActivity;
import com.uniovi.foxvid.vista.fragment.NewsFragment;
import com.uniovi.foxvid.vista.fragment.PostFragment;
import com.uniovi.foxvid.vista.fragment.StatisticsFragment;
import com.uniovi.foxvid.utils.CircleTransform;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    //get access to location permission
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private ImageButton btProfile;
    private ImageButton btSettings;

    private User user;
    private Dialog customDialog = null;


    LocationHandler handler = LocationHandler.getLocationHandler();


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


        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    loadPostView();
                }
                else
                    return;
            }
        };

        handler.askForPermissions(this, locationCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.stopLocationUpdates();
        if(customDialog!=null)
            customDialog.dismiss();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handler.setHasAccessToLocation(true);
                handler.getLocation(this);
            } else {
                // Permission Denied
                handler.showPermissionMessage(this);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

        if (id == R.id.settings) {
            Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intentSettings);

        }
        return super.onOptionsItemSelected(item);

    }


    private void openPopUpWindow() {
        customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(true);
        customDialog.setContentView(R.layout.fragment_log_out);
        //customDialog.getWindow().setLayout(1050, 600);

        TextView titulo = (TextView) customDialog.findViewById(R.id.txtUserDialog);
        TextView email = (TextView) customDialog.findViewById(R.id.txtUserEmailDialog);
        ImageView imagenUser = (ImageView) customDialog.findViewById(R.id.idImgUserDialog);

        if (user != null) {
            Picasso.get().load(user.getPhoto()).fit().transform(new CircleTransform()).into(imagenUser);;
            titulo.setText(user.getName());
            email.setText(user.getEmail());
        } else
            titulo.setText("Usuario");


        ((Button) customDialog.findViewById(R.id.btnLogOut)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                customDialog.dismiss();
                logOut();

            }
        });

        ((Button) customDialog.findViewById(R.id.cancelar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
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

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_post:
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