package com.uniovi.foxvid;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.uniovi.foxvid.modelo.Coordinate;
import com.uniovi.foxvid.vista.NewPostActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LocationHandler {

    private static LocationHandler handler = null;

    private int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static final int MAX_NUMBER_OF_TRIES = 3;

    private Dialog customDialog;

    private Coordinate coordinate;
    private Coordinate beforeCoordinate;
    private int numeroDeIntentosCordenadas = 0;
    private Activity callingActivity;



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
            showSnackbar(activity, R.string.permission_rationale, android.R.string.ok, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions( callingActivity,
                            new String[]{ACCESS_FINE_LOCATION},
                            REQUEST_PERMISSIONS_REQUEST_CODE);
                }
            });

        }
        else {

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
                            coordinate.setLat(beforeCoordinate.getLat());
                            coordinate.setLon(beforeCoordinate.getLon());

                        }
                    }
                }
            };
            if(activityListener!=null)
                fusedLocationClient.getLastLocation().addOnSuccessListener(activity, listener).addOnSuccessListener(activityListener);
            else
                fusedLocationClient.getLastLocation().addOnSuccessListener(activity, listener);
        }

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
     * @param snackStrId, id de la cadena que se desea mostrar, de tipo int
     * @param actionStrId, id de la cadena del botón que ejecuta la acción del listener, de tipo int
     * @param listener, listener con la funcionalidad del botón, de tipo View.OnClickListener
     */
    private void showSnackbar(Activity activity, int snackStrId, int actionStrId, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content),
                activity.getString(snackStrId),
                BaseTransientBottomBar.LENGTH_INDEFINITE);

        if (actionStrId != 0 && listener != null) {
            snackbar.setAction(activity.getString(actionStrId), listener);
        }

        snackbar.show();
    }


}
