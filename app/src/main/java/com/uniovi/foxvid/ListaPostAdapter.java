package com.uniovi.foxvid;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.uniovi.foxvid.modelo.Post;
import com.uniovi.foxvid.vista.igu.CircleTransform;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListaPostAdapter extends RecyclerView.Adapter<ListaPostAdapter.PostViewHolder>{

        //Interfaz para manejar el evento de click
        public interface OnItemClickListener{
            void onItemClick(Post post);
        }

        private List<Post> posts;
        private final OnItemClickListener listener;

        public ListaPostAdapter(List<Post> posts, OnItemClickListener listener) {
            this.posts = posts;
            this.listener = listener;
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.linear_recycler_view_post,parent,false);
            //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.linea_recycler_view_pelicula, parent, false);
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recycler_view_post,parent,false);
            return new PostViewHolder(itemView);
        }

    @Override
    public void onBindViewHolder(@NonNull ListaPostAdapter.PostViewHolder holder, int position) {
        Post post = posts.get(position);

        Log.i("Lista", "Visualiza elemento: "+post);
        holder.bindUser(post, listener);

    }



        @Override
        public int getItemCount() {
            return posts.size();
        }

        public static class PostViewHolder extends  RecyclerView.ViewHolder{

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

                postTxt = (TextView)itemView.findViewById(R.id.txtPost);
                fecha = (TextView)itemView.findViewById(R.id.txtDatePost);
                user = (TextView)itemView.findViewById(R.id.txtUserPublisher);
                userImage = (ImageView)itemView.findViewById(R.id.idImagePost);
                uuid = (TextView)itemView.findViewById(R.id.idUuidLikes);
                btLikes = (ImageButton)itemView.findViewById(R.id.btLike);
                btDislikes = (ImageButton)itemView.findViewById(R.id.btDislike);
                nLike = (TextView)itemView.findViewById(R.id.txtLike);
                nDislike = (TextView)itemView.findViewById(R.id.txtDislike);


                btLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        like(uuid.getText().toString(),Integer.parseInt(nLike.getText().toString()));
                    }
                });

                btDislikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        disLike(uuid.getText().toString(),Integer.parseInt(nDislike.getText().toString()));
                    }
                });

            }

            private void like(String uuid, int like){
                like ++;
                updateNumberOfLikes(uuid,"nLikes", like);
                nLike.setText(like + "");


            }

            private void disLike(String uuid, int dislike){
                dislike ++;
                updateNumberOfLikes(uuid,"nDislikes", dislike);
                nDislike.setText(dislike + "");
            }

            private void updateNumberOfLikes(String uuid, String campo, int value){
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference washingtonRef = db.collection("post").document(uuid);
                washingtonRef
                        .update(campo,value)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Error:" , "Error al actualizar los likes", e);
                            }
                        });
            }

            // asignar valores a los componentes
            public void bindUser(final Post post, final OnItemClickListener listener) {
                postTxt.setText(post.getText());
                fecha.setText(getTime(post.getDate()));
                user.setText(post.getUser().getEmail());
                nLike.setText(post.getnLikes() + "");
                nDislike.setText(post.getnDislikes() + "");
                uuid.setText(post.getUuid());
                Picasso.get().load(post.getUser().getPhoto()).transform(new CircleTransform()).into(userImage);


            }

            private String getTime(Timestamp t){
                long diferencia=new Date().getTime()- t.toDate().getTime();
                long segundos = TimeUnit.MILLISECONDS.toSeconds(diferencia);
                if(segundos < 0){
                    return 0 + " s";
                }
                else if(segundos <= 60){
                    return segundos + " s";
                }else if(segundos <= 3600)
                    return TimeUnit.MILLISECONDS.toMinutes(diferencia) + " min";
                else if(segundos <= 86400)
                    return TimeUnit.MILLISECONDS.toHours(diferencia) + " h";
                else
                    return TimeUnit.MILLISECONDS.toDays(diferencia) + " d";

            }


        }






}
