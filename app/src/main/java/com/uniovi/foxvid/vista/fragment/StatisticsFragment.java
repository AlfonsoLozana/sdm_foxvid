package com.uniovi.foxvid.vista.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.uniovi.foxvid.ListaPostAdapter;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.modelo.Coordinate;
import com.uniovi.foxvid.modelo.Post;
import com.uniovi.foxvid.modelo.User;

import org.json.JSONException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatisticsFragment extends Fragment implements OnMapReadyCallback {

    private View root;
    SupportMapFragment mapFragment;

    private GoogleMap mMap;
    List<LatLng> latLngs = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = inflater.inflate(R.layout.fragment_statistics, container, false);

        initMap();

        return root;
    }


    /**
     * Método  que inicia el mapa y lo asigna al componente del fragment destinado para el
     */
    private void initMap() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Se centra la posición en las coordenadas del usuario ademas de acercar el mapa para que se
     * visualice correctamente.
     * Tambien se carga la capa para hacer el mapa de calor.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng centro = new LatLng(40.346695, -3.064177);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centro, 5.2F));

        createHeatmapLayer();
    }

    /**
     * Método que crea la capa de calor y la asigna al mapa.
     * Se obtienen los posts realizados en las últimas 24 horas para visualizar en qué zonas se
     * están realizando más publicaciones.
     */
    private void createHeatmapLayer() {
        Timestamp yesterday = new Timestamp(new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("post")
                .orderBy("date", Query.Direction.ASCENDING)
                .startAt(yesterday)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                latLngs.add(
                                        new LatLng(
                                                new Double(document.getData().get("lat").toString()),
                                                new Double(document.getData().get("lon").toString())
                                        )
                                );
                            }
                            HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                                    .data(latLngs)
                                    .build();

                            TileOverlay overlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
                        } else {
                            Log.d("PostsMapa", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}