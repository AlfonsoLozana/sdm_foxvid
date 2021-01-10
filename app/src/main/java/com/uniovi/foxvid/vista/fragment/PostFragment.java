package com.uniovi.foxvid.vista.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.uniovi.foxvid.adapter.ListaPostAdapter;
import com.uniovi.foxvid.controlador.posts.PostsDatabaseHandler;
import com.uniovi.foxvid.utils.LocationHandler;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.modelo.Coordinate;
import com.uniovi.foxvid.modelo.Post;
import com.uniovi.foxvid.modelo.User;
import com.uniovi.foxvid.vista.NewPostActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Double.valueOf;


public class PostFragment extends Fragment {

    private static final int MIN_DISTANCE = 10;

    private List<Post> listPost;

    public int distancia = MIN_DISTANCE;
    private RecyclerView.LayoutManager layoutManager;


    RecyclerView listPostView;
    View root;
    FloatingActionButton btnNewPost;


    // Layout de refresco
    private SwipeRefreshLayout swipeRefreshLayout;


    LocationHandler handler = LocationHandler.getLocationHandler();
    PostsDatabaseHandler postsHandler = new PostsDatabaseHandler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        root = inflater.inflate(R.layout.fragment_post, container, false);

        if (listPost == null) listPost = new ArrayList<>();

        listPostView = (RecyclerView) root.findViewById(R.id.idRvPost);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Esto se ejecuta cada vez que se realiza el gesto
                cargarPost();
            }
        });

        //Se carga el gesto de los posts para dar like y dislike
        createGesture();

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
        handler.reset();
        //numeroDeIntentosCordenados = 0;
        cargarPost();
    }


    /**
     * Método que carga los posts en funcion de la distancia definida en las preferencias
     */
    public void cargarPost() {
        SharedPreferences sharedPreferencesMainRecycler = PreferenceManager.getDefaultSharedPreferences(getContext());
        distancia = sharedPreferencesMainRecycler.getInt("Key_Seek_KM", MIN_DISTANCE);

        OnSuccessListener<Location> listener = new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    loadPost();
                }
            }
        };

        swipeRefreshLayout.setRefreshing(false);
        handler.updateLocate(getActivity(), listener);
    }


    protected void loadPost() {
        listPostView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        listPostView.setLayoutManager(layoutManager);
        postsHandler.updateValues(distancia, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                listPostView.setAdapter(postsHandler.getAdapter());
                postsHandler.updatePosts();
            }
        });

    }

    protected void loadNewPostActivity() {
        Intent newPostIntent = new Intent(getActivity(), NewPostActivity.class);
        startActivity(newPostIntent);
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
            public void onSwiped(@NotNull final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (swipeDir == ItemTouchHelper.LEFT) {
                    //Si se desliza a la izquierda, se da dislike
                    postsHandler.updateLikes(viewHolder.getLayoutPosition(), -1);

                } else {
                    //Si se desliza a la derecha, se da like
                    postsHandler.updateLikes(viewHolder.getLayoutPosition(), 1);
                }
                postsHandler.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
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


//    /**
//     * Método que actualiza los likes o dislikes que tiene un post
//     * @param position, posición del post dentro del adapter, de tipo int
//     * @param like, indica si se ha dado like (1) o dislike (-1), de tipo int.
//     */
//    private void updateLikes(final int position, int like) {
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        //Referencia al documento que guarda la interacción de un usuario en un post
//        DocumentReference postRef = db.collection("post").document(listPost.get(position).getUuid())
//                .collection("interactions").document(userId);
//
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("like", like);
//
//        //Se hace la petición de escritura/actualización a firebase
//        postRef.set(data, SetOptions.merge())
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("UpdateLikes", "Empieza update");
//                        //Se actualizan los likes del post en cuestión
//                        updateNumberOfLikes(position);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("UpdateLikes", "Mal update" + e);
//                        Log.w("Error:", "Error al actualizar los likes", e);
//                    }
//                });
//    }


//    /**
//     * Cuenta el número de likes de cada post y actualiza los contadores que aparecen en la tarjeta de la pantalla
//     * @param postPosition, posición del post del que se desean obtener las interacciones, de tipo int.
//     */
//    private void updateNumberOfLikes(final int postPosition) {
//        //Referencia a la colección de interacciones de un post
//        CollectionReference likeRef = db.collection("post").document(listPost.get(postPosition).getUuid()).collection("interactions");
//
//        //Query para obtener el numero de likes
//        Query queryLike = likeRef.whereEqualTo("like", 1);
//
//        //Query para obtener el numero de dislikes
//        Query queryDisike = likeRef.whereEqualTo("like", -1);
//
//            //Llamada a la query para obtener los likes y contarlos
//            queryLike.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful() && listPost.size()!= 0) {
//                        int numberOfLikes = task.getResult().size();
//                        listPost.get(postPosition).setnLikes(numberOfLikes);
//                        adapter.notifyItemChanged(postPosition);
//                    } else {
//                        Log.d("LikeCount", "Error getting documents: ", task.getException());
//                    }
//                }
//            });
//
//            //Llamada a la query para obtener los likes y contarlos
//            queryDisike.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful() && listPost.size()!= 0) {
//                        int numberOfDislikes = task.getResult().size();
//                        listPost.get(postPosition).setnDislikes(numberOfDislikes);
//                        adapter.notifyItemChanged(postPosition);
//                    } else {
//                        Log.d("LikeCount", "Error getting documents: ", task.getException());
//                    }
//                }
//            });
//    }
}