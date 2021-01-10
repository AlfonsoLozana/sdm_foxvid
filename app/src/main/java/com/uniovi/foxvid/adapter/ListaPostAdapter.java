package com.uniovi.foxvid.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.utils.PostsDatabaseHandler;
import com.uniovi.foxvid.modelo.Post;
import com.uniovi.foxvid.utils.CircleTransform;

import java.util.List;

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

        holder.setPosition(position);
        holder.bindUser(post);

    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        private FirebaseFirestore db;

        private TextView postTxt;
        private TextView fecha;
        private TextView uuid;
        private TextView user;
        private TextView nLike;
        private TextView nDislike;
        private ImageView userImage;
        private ImageButton btLikes;
        private ImageButton btDislikes;
        private int position =0;
        PostsDatabaseHandler postsHandler = PostsDatabaseHandler.getPostsDatabaseHandler();

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
                    postsHandler.updateLikes(position, 1);
                }
            });

            btDislikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postsHandler.updateLikes(position, -1);

                }
            });

        }

        public void setPosition(int position){
            this.position=position;
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
