package com.uniovi.foxvid.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.modelo.Post;
import com.uniovi.foxvid.utils.CircleTransform;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ListaPostAdapter extends RecyclerView.Adapter<ListaPostAdapter.PostViewHolder> {

    //Interfaz para manejar el evento de click


    private List<Post> posts;

    public ListaPostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recycler_view_post, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaPostAdapter.PostViewHolder holder, int position) {
        Post post = posts.get(position);

        Log.i("Lista", "Visualiza elemento: " + post);
        holder.bindUser(post);

    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        private FirebaseFirestore db;

        String postId;
        private TextView postTxt;
        private TextView fecha;
        private TextView uuid;
        private TextView user;
        private TextView nLike;
        private TextView nDislike;
        private ImageView userImage;
        private ImageButton btLikes;
        private ImageButton btDislikes;

        public PostViewHolder(View itemView) {
            super(itemView);

            db = FirebaseFirestore.getInstance();


            postTxt = (TextView) itemView.findViewById(R.id.txtPost);
            fecha = (TextView) itemView.findViewById(R.id.txtDatePost);
            user = (TextView) itemView.findViewById(R.id.txtUserPublisher);
            userImage = (ImageView) itemView.findViewById(R.id.idImagePost);
            uuid = (TextView) itemView.findViewById(R.id.idUuidLikes);
            btLikes = (ImageButton) itemView.findViewById(R.id.btLike);
            btDislikes = (ImageButton) itemView.findViewById(R.id.btDislike);
            nLike = (TextView) itemView.findViewById(R.id.txtLike);
            nDislike = (TextView) itemView.findViewById(R.id.txtDislike);


            btLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateLikes(1);
//                        like(uuid.getText().toString(),Integer.parseInt(nLike.getText().toString()));
                }
            });

            btDislikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateLikes(-1);
//                        disLike(uuid.getText().toString(),Integer.parseInt(nDislike.getText().toString()));
                }
            });

        }

        private void updateLikes(int like) {
            postId = uuid.getText().toString();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DocumentReference postRef = db.collection("post").document(postId)
                    .collection("interactions").document(userId);


            Map<String, Object> data = new HashMap<>();
            data.put("like", like);


            postRef.set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("UpdateLikes", "Empieza update");
                            updateNumberOfLikes();
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


        private void updateNumberOfLikes() {
            final CollectionReference likeRef = db.collection("post").document(postId).collection("interactions");
            final Query queryLike = likeRef.whereEqualTo("like", 1);
            final Query queryDisike = likeRef.whereEqualTo("like", -1);

            queryLike.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        int numberOfLikes = task.getResult().size();
                        nLike.setText(numberOfLikes+"");

                    } else {
                        //Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });

            queryDisike.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        int numberOfDislikes = task.getResult().size();
                        nDislike.setText(numberOfDislikes+"");

                    } else {
                        //Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }


        // asignar valores a los componentes
        public void bindUser(final Post post) {
            postTxt.setText(post.getText());
            fecha.setText(getTime(post.getDate()));
            user.setText(post.getUser().getEmail().split("@")[0]);
            nLike.setText(post.getnLikes() + "");
            nDislike.setText(post.getnDislikes() + "");
            uuid.setText(post.getUuid());
            Picasso.get().load(post.getUser().getPhoto()).transform(new CircleTransform()).into(userImage);


        }

        private String getTime(Timestamp t) {
            long diferencia = new Date().getTime() - t.toDate().getTime();
            long segundos = TimeUnit.MILLISECONDS.toSeconds(diferencia);
            if (segundos < 0) {
                return 0 + " s";
            } else if (segundos <= 60) {
                return segundos + " s";
            } else if (segundos <= 3600)
                return TimeUnit.MILLISECONDS.toMinutes(diferencia) + " min";
            else if (segundos <= 86400)
                return TimeUnit.MILLISECONDS.toHours(diferencia) + " h";
            else
                return TimeUnit.MILLISECONDS.toDays(diferencia) + " d";

        }


    }


}
