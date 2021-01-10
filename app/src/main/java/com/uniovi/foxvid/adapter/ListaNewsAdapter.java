package com.uniovi.foxvid.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.modelo.News;

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
            //Collections.reverse(this.news);
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
            private TextView txtDate;

            public NewsViewHolder(View itemView) {
                super(itemView);

                imgNews = itemView.findViewById(R.id.imgNews);
                txtSummary = itemView.findViewById(R.id.txtSummaryNews);
                txtTitle = itemView.findViewById(R.id.txtTitleNews);
                txtDate = itemView.findViewById(R.id.idDate);

            }

            // asignar valores a los componentes
            public void bindUser(final News news, final OnItemClickListener listener) {
                txtSummary.setText(news.getSummary());
                txtTitle.setText(news.getTitle());
                txtDate.setText("Hace " + getTime(news.getDate()));

                if(news.getImage()!=null && !news.getImage().isEmpty())
                    Picasso.get().load(news.getImage()).into(imgNews);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        listener.onItemClick(news);
                    }
                });
            }

            private String getTime(Date date) {
                long diferencia = new Date().getTime() - date.getTime();
                long segundos = TimeUnit.MILLISECONDS.toSeconds(diferencia);
                if (segundos < 0) {
                    return 0 + " segundos";
                }else if (segundos == 1){
                    return segundos + " segundo";
                } else if (segundos <= 60) {
                    return segundos + " segundos";
                } else if (segundos <= 3600)
                    return TimeUnit.MILLISECONDS.toMinutes(diferencia) + " minuto" + ((TimeUnit.MILLISECONDS.toMinutes(diferencia)==1)?"" :"s");
                else if (segundos <= 86400)
                    return TimeUnit.MILLISECONDS.toHours(diferencia) + " hora" + ((TimeUnit.MILLISECONDS.toHours(diferencia)==1)?"" :"s");
                else
                    return TimeUnit.MILLISECONDS.toDays(diferencia) + " dia" + ((TimeUnit.MILLISECONDS.toDays(diferencia)==1)?"" :"s");

            }

        }






}
