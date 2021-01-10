package com.uniovi.foxvid.controlador.posts;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.adapter.ListaPostAdapter;
import com.uniovi.foxvid.modelo.Coordinate;
import com.uniovi.foxvid.modelo.Post;
import com.uniovi.foxvid.modelo.User;
import com.uniovi.foxvid.utils.LocationHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.Double.valueOf;

public class PostsDatabaseHandler {

    LocationHandler handler = LocationHandler.getLocationHandler();
    private int distancia;
    List<Post> listPost;
    int numberOfDislikes = 0;
    int numberOfLikes = 0;
    private int numOfPost = -1;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ListaPostAdapter adapter;

    public List<Post> getPosts() {
        return this.listPost;
    }

    public int getNDislikes() {
        return this.numberOfDislikes;
    }

    public int getNLikes() {
        return this.numberOfLikes;
    }

    public void updateValues(int distancia, final OnCompleteListener listener) {
        this.distancia = distancia;
        listPost = new ArrayList<>();
        Query query =db.collection("post").orderBy("date");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    addPost(task.getResult());

                    adapter = new ListaPostAdapter(listPost);
                    Log.d("PostsHandler", listPost.size() + "");
                }
            }
        });

        db.collection("post")
                .orderBy("date", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    addPost(task.getResult());

                    adapter = new ListaPostAdapter(listPost);
                    Log.d("PostsHandler", listPost.size()+"");
//                    for (int i = 0; i < listPost.size(); i++) {
//                            updateNumberOfLikes(i);
//                    }
                }
            }
        }).addOnCompleteListener(listener);
    }


    public void updatePosts() {
        for (int i = 0; i < listPost.size(); i++) {
            updateNumberOfLikes(i);
        }
    }

    /**
     * Método que recorre los posts de la base de datos y los añade a la lista de post
     *
     * @param snapshots respuesta de la query ejecutada, de tipo QuerySnapshot
     */
    private void addPost(QuerySnapshot snapshots) {
        for (DocumentChange dc : snapshots.getDocumentChanges()) {
            if (dc.getType() == DocumentChange.Type.ADDED) {
                if (checkPost(dc)) return;
            }
        }
    }


    private boolean checkPost(DocumentChange dc) {
        if (handler.checkDistancia(dc, distancia)) {
            boolean existe = false;
            for (Post p : listPost) {
                if (p.getUuid().equals(dc.getDocument().get("uid")))
                    existe = true;
            }
            if (!existe) {
                if (listPost.size() > numOfPost && numOfPost != -1) {
                    return false;
                }
                listPost.add(crearPost(dc));
            }
        }
        return true;
    }


    private Post crearPost(DocumentChange dc) {
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


    /**
     * Método que actualiza los likes o dislikes que tiene un post
     *
     * @param position, posición del post dentro del adapter, de tipo int
     * @param like,     indica si se ha dado like (1) o dislike (-1), de tipo int.
     */
    public void updateLikes(final int position, int like/*, final OnCompleteListener listener*/) {
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
                        updateNumberOfLikes(position/*, listener*/);
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
     *
     * @param postPosition, posición del post del que se desean obtener las interacciones, de tipo int.
     *
     */
    private void updateNumberOfLikes(final int postPosition/*, OnCompleteListener listener*/) {
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
                if (task.isSuccessful() && listPost.size() != 0) {
                    numberOfLikes = task.getResult().size();
                    listPost.get(postPosition).setnLikes(numberOfLikes);
                    adapter.notifyItemChanged(postPosition);

                } else {
                    Log.d("LikeCount", "Error getting documents: ", task.getException());
                }
            }
        });/*.addOnCompleteListener(listener);*/

        //Llamada a la query para obtener los likes y contarlos
        queryDisike.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && listPost.size() != 0) {
                    numberOfDislikes = task.getResult().size();
                    listPost.get(postPosition).setnDislikes(numberOfDislikes);
                    adapter.notifyItemChanged(postPosition);
                } else {
                    Log.d("LikeCount", "Error getting documents: ", task.getException());
                }
            }
        });/*.addOnCompleteListener(listener);*/
    }




    public void publishPost(String text, OnSuccessListener successListener, OnFailureListener failureListener) {

        System.out.println("Debug: todo bien");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //se obtienen todos los datos necesarios para hacer el post y se añaden en un mapa.
        String uuid = UUID.randomUUID().toString();
        Map<String, Object> posts = new HashMap<>();
        posts.put("uid", uuid);
        posts.put("post", text);
        posts.put("userUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        posts.put("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        posts.put("userImage", FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
        posts.put("date", Timestamp.now());
        posts.put("lat", handler.getUserCoordinate().getLat());
        posts.put("lon", handler.getUserCoordinate().getLon());
        posts.put("nLikes", 0);
        posts.put("nDislikes", 0);


        //Se hace la llamada a la base de datos para añadir un nuevo post.
        db.collection("post").document(uuid)
                .set(posts)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }


    public ListaPostAdapter getAdapter() {
        return this.adapter;
    }
}
