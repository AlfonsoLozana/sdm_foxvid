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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.modelo.Coordinate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class NewPostActivity extends AppCompatActivity {

    private int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private TextView txtPost;
    private Coordinate coordinate;

    private Toolbar toolbar;
    OnSuccessListener<Location> listener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);


        toolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtPost = (TextView) findViewById(R.id.txtNewPost);
        coordinate = new Coordinate(0.0, 0.0);

        listener = new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    coordinate.setLat(location.getLatitude());
                    coordinate.setLon(location.getLongitude());

                }
            }
        };

        updateLocate(listener);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_post_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
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


    /**
     * Método que crea un nuevo post a la base de datos.
     */
    private void post() {
        //Si no se ha escrito nada
        if (txtPost.getText().toString().isEmpty()) {
            showSnackbar(R.string.post_text_empty, 0, null);
        }
        //Si la localización delusuario no se ha obtenido correctamente
        else if (coordinate.getLat().isNaN() || coordinate.getLat().isInfinite() || coordinate.getLat() == 0) {
            updateLocate(listener);
        }
        //Si no hay errores
        else {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            //se obtienen todos los datos necesarios para hacer el post y se añaden en un mapa.
            String uuid = UUID.randomUUID().toString();
            Map<String, Object> posts = new HashMap<>();
            posts.put("uid", uuid);
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

            //Se hace la llamada a la base de datos para añadir un nuevo post.
            db.collection("post").document(uuid)
                    .set(posts)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showSnackbar(R.string.successful_post_upload, 0, null);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showSnackbar(R.string.failure_post_upload, 0, null);

                        }
                    });
        }
    }

    /**
     * Método que obtiene la última localización conocida del usuario.
     * Si no tiene los permisos necesarios, le muestra un mensaje para poder darlos.
     * @param listener, listener con la funcionalidad que se espera al obtener la última ubicación del usuario
     */
    private void updateLocate(OnSuccessListener<Location> listener) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //En caso de que no se hayan concedido los permisos, pedir al usuario que los active
            showSnackbar(R.string.permission_rationale, android.R.string.ok, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions( NewPostActivity.this,
                            new String[]{ACCESS_FINE_LOCATION},
                            REQUEST_PERMISSIONS_REQUEST_CODE);
                }
            });

        }
        else {
            //En caso de tener los permisos, obtener la última localización del usuario y almacenarla
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, listener);
        }

    }


    /**
     * Método que muestra un snackbar con un mensaje que se le pasa por parámetro,
     * además también se le puede asignar una texto y un listener para el botón de acción.
     * @param snackStrId, id de la cadena que se desea mostrar, de tipo int
     * @param actionStrId, id de la cadena del botón que ejecuta la acción del listener, de tipo int
     * @param listener, listener con la funcionalidad del botón, de tipo View.OnClickListener
     */
    private void showSnackbar(int snackStrId, int actionStrId, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                getString(snackStrId),
                BaseTransientBottomBar.LENGTH_INDEFINITE);

        if (actionStrId != 0 && listener != null) {
            snackbar.setAction(getString(actionStrId), listener);
        }

        snackbar.show();
    }


}