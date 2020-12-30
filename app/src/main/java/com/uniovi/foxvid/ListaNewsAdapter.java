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
import com.uniovi.foxvid.modelo.News;
import com.uniovi.foxvid.modelo.Post;
import com.uniovi.foxvid.vista.igu.CircleTransform;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListaNewsAdapter extends RecyclerView.Adapter<ListaNewsAdapter.NewsViewHolder>{

        //Interfaz para manejar el evento de click
        public interface OnItemClickListener{
            void onItemClick(News news);
        }

        private List<News> news;
        private final OnItemClickListener listener;

        public ListaNewsAdapter(List<News> news, OnItemClickListener listener) {
            this.news = news;
            Collections.reverse(this.news);
            this.listener = listener;
        }

        @NonNull
        @Override
        public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recycler_view_news,parent,false);
            return new NewsViewHolder(itemView);
        }

    @Override
    public void onBindViewHolder(@NonNull ListaNewsAdapter.NewsViewHolder holder, int position) {
        News news = this.news.get(position);

        Log.i("Lista", "Visualiza elemento: "+news);
        holder.bindUser(news, listener);

    }



        @Override
        public int getItemCount() {
            return news.size();
        }

        public static class NewsViewHolder extends  RecyclerView.ViewHolder{

            private ImageView imgNews;
            private TextView txtTitle;
            private TextView txtSummary;

            public NewsViewHolder(View itemView) {
                super(itemView);

                imgNews = itemView.findViewById(R.id.imgNews);
                txtSummary = itemView.findViewById(R.id.txtSummaryNews);
                txtTitle = itemView.findViewById(R.id.txtTitleNews);

            }

            // asignar valores a los componentes
            public void bindUser(final News news, final OnItemClickListener listener) {
                txtSummary.setText(news.getSummary());
                txtTitle.setText(news.getTitle());

                if(news.getImage()!=null && !news.getImage().isEmpty())
                    Picasso.get().load(news.getImage()).into(imgNews);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        Log.i("Hola", "Hola");
                        listener.onItemClick(news);
                    }
                });
            }

        }






}
