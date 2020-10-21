package com.uniovi.foxvid;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.uniovi.foxvid.modelo.Post;

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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.linear_recycler_view_post,parent,false);
            //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.linea_recycler_view_pelicula, parent, false);
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
            private TextView user;

            public PostViewHolder(View itemView) {
                super(itemView);

                postTxt = (TextView)itemView.findViewById(R.id.txtPost);
                fecha = (TextView)itemView.findViewById(R.id.txtDatePost);
                user = (TextView)itemView.findViewById(R.id.txtUserPublisher);


            }

            // asignar valores a los componentes
            public void bindUser(final Post post, final OnItemClickListener listener) {
                postTxt.setText(post.getText());
                //fecha.setText(pelicula.getFecha());
                fecha.setText(getTime(post.getDate()));
                user.setText(post.getUser().getEmail());
                // cargar imagen
                //Picasso.get().load(pelicula.getUrlCaratula()).into(imagen);
                //Uri myUri = Uri.parse(pelicula.getUrlCaratula());
                //imagen.setImageURI(myUri);

            }

            private String getTime(Timestamp t){
                long diferencia=new Date().getTime()- t.toDate().getTime();
                long segundos = TimeUnit.MILLISECONDS.toSeconds(diferencia);
                if(segundos <= 60){
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
