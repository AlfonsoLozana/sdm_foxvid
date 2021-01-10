package com.uniovi.foxvid.adapter;

import android.annotation.SuppressLint;
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


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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


            postTxt = itemView.findViewById(R.id.txtPost);
            fecha = itemView.findViewById(R.id.txtDatePost);
            user = itemView.findViewById(R.id.txtUserPublisher);
            userImage = itemView.findViewById(R.id.idImagePost);
            uuid = itemView.findViewById(R.id.idUuidLikes);
            btLikes = itemView.findViewById(R.id.btLike);
            btDislikes = itemView.findViewById(R.id.btDislike);
            nLike = itemView.findViewById(R.id.txtLike);
            nDislike = itemView.findViewById(R.id.txtDislike);


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
            String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
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
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        int numberOfLikes = Objects.requireNonNull(task.getResult()).size();
                        nLike.setText(numberOfLikes+"");

                    }

                }
            });

            queryDisike.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        int numberOfDislikes = Objects.requireNonNull(task.getResult()).size();
                        nDislike.setText(numberOfDislikes+"");

                    }

                }
            });
        }


        // asignar valores a los componentes
        @SuppressLint("SetTextI18n")
        public void bindUser(final Post post) {
            postTxt.setText(post.getText());
            user.setText(post.getUser().getEmail().split("@")[0]);
            fecha.setText(post.getTime());
            nLike.setText(post.getnLikes() + "");
            nDislike.setText(post.getnDislikes() + "");
            uuid.setText(post.getUuid());
            Picasso.get().load(post.getUser().getPhoto()).transform(new CircleTransform()).into(userImage);

        }

    }


}
