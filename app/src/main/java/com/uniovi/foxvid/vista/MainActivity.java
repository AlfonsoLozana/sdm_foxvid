package com.uniovi.foxvid.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
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
    boolean hasAccessToLocation=false;

    private Button btLogOut;

    private List<Post> listPost;
    private Toolbar toolbar;
    private ImageButton btProfile;
    private ImageButton btSettings;
    private FirebaseAuth mAuth;

    private User user;
    Dialog customDialog = null;


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
        getLocation();




       /* btLogOut = (Button)findViewById(R.id.idLogOut);




        btLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });


        //User user = this.getIntent().getParcelableExtra(Login.USER_EMAIL);

        User user = new User(FirebaseAuth.getInstance();
        TextView email =  (TextView)findViewById(R.id.idEmail);
        email.setText(user.getEmail());
        */


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
            loadPostView();
            /*Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (myLocation == null) {
                myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            }*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.new_post_menu, menu);
        // getMenuInflater().inflate(R.menu.toll_bar_menu, menu);
        getMenuInflater().inflate(R.menu.bar_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /* switch (menuItem.getItemId()) {
            case R.id.home:
                this.finish();
                return true;
            default:
                return false;
        }*/

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

    }

    private void loadNewsView() {
        NewsFragment info = new NewsFragment();
        Bundle args = new Bundle();
        info.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();
    }

    private void loadStatistics() {
        StatisticsFragment info = new StatisticsFragment();
        Bundle args = new Bundle();
        info.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();
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