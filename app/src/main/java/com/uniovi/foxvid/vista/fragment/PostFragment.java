package com.uniovi.foxvid.vista.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;
import com.uniovi.foxvid.ListaPostAdapter;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.modelo.Coordinate;
import com.uniovi.foxvid.modelo.Post;
import com.uniovi.foxvid.modelo.User;
import com.uniovi.foxvid.vista.MainActivity;
import com.uniovi.foxvid.vista.NewPostActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Double.*;


public class PostFragment extends Fragment {

    public static final String POSTS = "posts";
    public static final String MAIN = "main";
    public static final int MAX_NUMBER_OF_INTENTES = 3;

    private List<Post> listPost;
    private Coordinate coordinate;
    private Coordinate beforeCoordinate;
    private int numeroDeIntentosCordenados;
    public int distancia;
    private ListaPostAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Dialog customDialog;
    private int numOfPost = -1;
    private boolean cargando = false;


    RecyclerView listPostView;
    View root;
    FloatingActionButton btnNewPost;


    // Layout de refresco
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        coordinate = new Coordinate(0.0, 0.0);
        beforeCoordinate = new Coordinate(0.0, 0.0);
        numeroDeIntentosCordenados = 0;

        root = inflater.inflate(R.layout.fragment_post, container, false);

        if (listPost == null) listPost = new ArrayList<Post>();

        listPostView = (RecyclerView) root.findViewById(R.id.idRvPost);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);

       /* listPostView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    if(!cargando){
                        numOfPost += 5;
                        cargando = true;
                        //System.out.println("Tamos en las ultimas");
                        cargarPost();
                    }

                }
            }
        });*/

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Esto se ejecuta cada vez que se realiza el gesto
                cargarPost();
            }
        });

        //Se carga el gesto de los posts para dar like y dislike
        createGesture();


        //TODO Esto que ta comentao fae falta??
//        Bundle args = getArguments();
//        if (args != null) {
//
//        }

        cargarPost();

        //Floating button que carga la activity para crear nuevos posts
        btnNewPost = root.findViewById(R.id.btnNewPost);
        btnNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadNewPostActivity();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        numeroDeIntentosCordenados = 0;
        cargarPost();
    }


    /**
     * Método que carga los posts en funcion de la distancia definida en las preferencias
     */
    public void cargarPost() {
        SharedPreferences sharedPreferencesMainRecycler = PreferenceManager.getDefaultSharedPreferences(getContext());
        distancia = sharedPreferencesMainRecycler.getInt("Key_Seek_KM", 0);

        OnSuccessListener<Location> listener = new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    coordinate.setLat(location.getLatitude());
                    coordinate.setLon(location.getLongitude());
                    beforeCoordinate.setLat(location.getLatitude());
                    beforeCoordinate.setLon(location.getLongitude());
                    numeroDeIntentosCordenados = 0;
                    loadPost();
                } else {
                    if (beforeCoordinate.getLon() == 0 && beforeCoordinate.getLat() == 0 && numeroDeIntentosCordenados < MAX_NUMBER_OF_INTENTES) {
                        preguntarPorUbicacion();
                        numeroDeIntentosCordenados++;
                    } else {
                        coordinate.setLat(beforeCoordinate.getLat());
                        coordinate.setLon(beforeCoordinate.getLon());
                        loadPost();
                    }

                }
            }
        };

        swipeRefreshLayout.setRefreshing(false);
        cargando = false;
        updateLocate(listener);
    }

    protected void loadPost() {
        listPostView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        listPostView.setLayoutManager(layoutManager);
        updateValues();

    }

    protected void loadNewPostActivity() {
        Intent newPostIntent = new Intent(getActivity(), NewPostActivity.class);
        startActivity(newPostIntent);
    }

    private void updateValues() {
        listPost = new ArrayList<>();
        db.collection("post")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //Log.w(TAG, "listen:error", e);
                            return;
                        }
                        addPost(snapshots);

                        adapter = new ListaPostAdapter(listPost);
                        listPostView.setAdapter(adapter);

                        for (int i = 0; i < listPost.size(); i++) {
                            updateNumberOfLikes(i);
                        }
                    }
                });
    }

    /**
     * Método que recorre los posts de la base de datos y los añade a la lista de post
     * @param snapshots
     */
    private void addPost(QuerySnapshot snapshots){
        for (DocumentChange dc : snapshots.getDocumentChanges()) {
            switch (dc.getType()) {
                case ADDED:
                    if(!checkPost(dc)) return;
                    break;
                default:
                    break;
            }
        }
    }

    private boolean checkPost(DocumentChange dc){
        if (checkDistancia(dc)) {
            boolean existe = false;
            for (Post p : listPost) {
                if (p.getUuid().equals(dc.getDocument().get("uid")))
                    existe = true;
            }
            if (!existe) {
                if(listPost.size() > numOfPost && numOfPost != -1){
                    return false;
                }
                listPost.add(crearPost(dc));
            }
        }
        return true;
    }

    private Post crearPost(DocumentChange dc){
        return new Post(dc.getDocument().get("uid").toString(),
                dc.getDocument().get("post").toString(),
                //public User(String uid, String name, String email, Uri photo)
                new User(dc.getDocument().get("userUid").toString(), null, dc.getDocument().get("userEmail").toString(),
                        dc.getDocument().get("userImage").toString()),
                (Timestamp) dc.getDocument().get("date"),
                new Coordinate(valueOf(dc.getDocument().get("lat").toString()), Double.valueOf(dc.getDocument().get("lat").toString())),
                0,
                0);
    }

    private boolean checkDistancia(DocumentChange dc){
        return coordinate.checkDistancia(Double.valueOf(dc.getDocument().get("lat").toString()),
                Double.valueOf(dc.getDocument().get("lon").toString()), distancia);
    }
    /**
     * Método que obtiene la última localización conocida del usuario.
     * Si no tiene los permisos necesarios, le muestra un mensaje para poder darlos.
     * @param listener, listener con la funcionalidad que se espera al obtener la última ubicación del usuario
     */
    private void updateLocate(OnSuccessListener<Location> listener) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), listener);

    }

    /**
     * Método que carga un mensaje de diálogo en caso de que la ubicación no esté activada.
     */
    private void preguntarPorUbicacion() {
        if (numeroDeIntentosCordenados == 0) {
            customDialog = new Dialog(getContext());
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customDialog.setCancelable(true);
            customDialog.setContentView(R.layout.fragment_location);
            customDialog.getWindow().setLayout(1070, 850);


            ((Button) customDialog.findViewById(R.id.btLocation)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customDialog.cancel();
                    numeroDeIntentosCordenados = -2;
                    cargarPost();

                }
            });

            customDialog.show();
        }
    }


    /**
     * Crea el gesto de deslizar la tarjeta con un post a izquierda o derecha para dar like o dislike respectivamente.
     */
    private void createGesture() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (swipeDir == ItemTouchHelper.LEFT)
                    //Si se desliza a la izquierda, se da dislike
                    updateLikes(viewHolder.getLayoutPosition(), -1);
                else
                    //Si se desliza a la derecha, se da like
                    updateLikes(viewHolder.getLayoutPosition(), 1);
                System.out.println(listPost.get(viewHolder.getLayoutPosition()).getText());
                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
            }

            @Override
            public void onMoved(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, int fromPos, @NotNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
            }

            @Override
            public void onChildDraw(@NotNull Canvas c, @NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                //Se definen los colores e imágenes que se muestran al deslizar la tarjeta a izquierda o derecha.
                Bitmap icon;
                Paint p = new Paint();
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;


                    if (dX > 0) {
                        //Si se desliza a la derecha, se muestra un fondo verde y una mano indicando like
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_thumb_up_black_18dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else if (dX < 0) {
                        //Si se desliza a la izquierda, se muestra un fondo rojo y una mano indicando dislike
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_thumb_down_black_18dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(listPostView);
    }


    /**
     * Método que actualiza los likes o dislikes que tiene un post
     * @param position, posición del post dentro del adapter, de tipo int
     * @param like, indica si se ha dado like (1) o dislike (-1), de tipo int.
     */
    private void updateLikes(final int position, int like) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Referencia al documento que guarda la interacción de un usuario en un post
        DocumentReference postRef = db.collection("post").document(listPost.get(position).getUuid())
                .collection("interactions").document(userId);


        Map<String, Object> data = new HashMap<>();
        data.put("like", like);

        //Se hace la petición de escritura/actualización a firebase
        postRef.set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("UpdateLikes", "Empieza update");
                        //Se actualizan los likes del post en cuestión
                        updateNumberOfLikes(position);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UpdateLikes", "Mal update" + e);
                        Log.w("Error:", "Error al actualizar los likes", e);
                    }
                });
    }


    /**
     * Cuenta el número de likes de cada post y actualiza los contadores que aparecen en la tarjeta de la pantalla
     * @param postPosition, posición del post del que se desean obtener las interacciones, de tipo int.
     */
    private void updateNumberOfLikes(final int postPosition) {
        //Referencia a la colección de interacciones de un post
        CollectionReference likeRef = db.collection("post").document(listPost.get(postPosition).getUuid()).collection("interactions");

        //Query para obtener el numero de likes
        Query queryLike = likeRef.whereEqualTo("like", 1);

        //Query para obtener el numero de dislikes
        Query queryDisike = likeRef.whereEqualTo("like", -1);

            //Llamada a la query para obtener los likes y contarlos
            queryLike.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && listPost.size()!= 0) {
                        int numberOfLikes = task.getResult().size();
                        listPost.get(postPosition).setnLikes(numberOfLikes);
                        adapter.notifyItemChanged(postPosition);
                    } else {
                        Log.d("LikeCount", "Error getting documents: ", task.getException());
                    }
                }
            });

            //Llamada a la query para obtener los likes y contarlos
            queryDisike.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && listPost.size()!= 0) {
                        int numberOfDislikes = task.getResult().size();
                        listPost.get(postPosition).setnDislikes(numberOfDislikes);
                        adapter.notifyItemChanged(postPosition);
                    } else {
                        Log.d("LikeCount", "Error getting documents: ", task.getException());
                    }
                }
            });


    }


}