package com.uniovi.foxvid.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.modelo.Coordinate;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

public class LocationHandler {

    @SuppressLint("StaticFieldLeak")
    private static LocationHandler handler = null;

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static final int MAX_NUMBER_OF_TRIES = 3;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private Dialog customDialog;

    private Coordinate coordinate;
    private Coordinate beforeCoordinate;
    private int numeroDeIntentosCordenadas = 0;
    private Activity callingActivity;


    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    boolean hasAccessToLocation = false;


    public static LocationHandler getLocationHandler() {
        if (handler==null) {
            handler=new LocationHandler();
        }
        return handler;
    }

    private LocationHandler(){
        coordinate = new Coordinate(0.0, 0.0);
        beforeCoordinate = new Coordinate(0.0, 0.0);
    }


    /**
     * Método que obtiene la última localización conocida del usuario.
     * Si no tiene los permisos necesarios, le muestra un mensaje para poder darlos.
     */
    public void updateLocate(Activity activity, OnSuccessListener<Location> activityListener) {

        callingActivity = activity;

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(callingActivity);
        if (ActivityCompat.checkSelfPermission(callingActivity.getApplicationContext(),
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(callingActivity.getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //En caso de que no se hayan concedido los permisos, pedir al usuario que los active
            Log.d("LocationHandler", "UpdateLocate");
            showPermissionMessage(activity);


        }
        else {
            Log.d("LocationHandler", "Success");
            OnSuccessListener<Location> listener = new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        coordinate.setLat(location.getLatitude());
                        coordinate.setLon(location.getLongitude());
                        beforeCoordinate.setLat(location.getLatitude());
                        beforeCoordinate.setLon(location.getLongitude());
                        numeroDeIntentosCordenadas = 0;

                    } else {
                        if (beforeCoordinate.getLon() == 0 && beforeCoordinate.getLat() == 0 && numeroDeIntentosCordenadas < MAX_NUMBER_OF_TRIES) {
                            preguntarPorUbicacion(callingActivity);
                            numeroDeIntentosCordenadas++;
                        } else {
                            showSnackbar(callingActivity, R.string.location_deactivated, 0, null);
                            coordinate.setLat(beforeCoordinate.getLat());
                            coordinate.setLon(beforeCoordinate.getLon());

                        }
                    }
                }
            };
            if(activityListener!=null) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(activity, listener).addOnSuccessListener(activityListener);
            }
            else
                fusedLocationClient.getLastLocation().addOnSuccessListener(activity, listener);
        }

    }

    public void showPermissionMessage(Activity activity) {
        callingActivity=activity;
        showSnackbar(activity, R.string.permission_rationale, android.R.string.ok, new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions( callingActivity,
                        new String[]{ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSIONS_REQUEST_CODE);
            }
        });
    }


    /**
     * Método que carga un mensaje de diálogo en caso de que la ubicación no esté activada.
     */
    private void preguntarPorUbicacion(Activity activity) {
        if (numeroDeIntentosCordenadas == 0) {
            customDialog = new Dialog(activity);
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customDialog.setCancelable(true);
            customDialog.setContentView(R.layout.fragment_location);
            customDialog.getWindow().setLayout(1070, 850);


            ((Button) customDialog.findViewById(R.id.btLocation)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customDialog.cancel();
                    numeroDeIntentosCordenadas = -2;

                }
            });

            customDialog.show();
        }
    }

    public boolean checkDistancia(DocumentChange dc, int distancia){
        return coordinate.checkDistancia(Double.valueOf(dc.getDocument().get("lat").toString()),
                Double.valueOf(dc.getDocument().get("lon").toString()), distancia);
    }

    public Coordinate getUserCoordinate(){
        return coordinate;
    }


    public void reset() {
        this.numeroDeIntentosCordenadas=0;
    }


    /**
     * Método que muestra un snackbar con un mensaje que se le pasa por parámetro,
     * además también se le puede asignar una texto y un listener para el botón de acción.
     * @param listener, listener con la funcionalidad del botón, de tipo View.OnClickListener
     */
    private void showSnackbar(Activity activity, int snackStrId, int actionStrId, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content),
                activity.getString(snackStrId),
                BaseTransientBottomBar.LENGTH_LONG);

        if (actionStrId != 0 && listener != null) {
            snackbar.setAction(activity.getString(android.R.string.ok), listener);
        }

        snackbar.show();
    }



    public void askForPermissions(Activity activity, LocationCallback callback){
        //Metodo que carga los posts si se dan permisos de ubicacion
        hasAccessToLocation = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        locationRequest = new LocationRequest();
        locationCallback = callback;

        getLocation(activity);
    }

    //Get location
    public void getLocation(Activity activity) {
        activity.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
        else {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        }
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void setHasAccessToLocation(boolean b) {
        this.hasAccessToLocation=b;
    }
}
