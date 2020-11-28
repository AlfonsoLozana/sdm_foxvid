package com.uniovi.foxvid.vista.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uniovi.foxvid.ListaNewsAdapter;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.controlador.db.FeedDatabase;
import com.uniovi.foxvid.controlador.db.VolleySingleton;
import com.uniovi.foxvid.controlador.xml.Rss;
import com.uniovi.foxvid.controlador.xml.XmlRequest;
import com.uniovi.foxvid.modelo.Coordinate;
import com.uniovi.foxvid.modelo.News;
import com.uniovi.foxvid.modelo.Post;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private static final String TAG = "Error " ;
    private static final String URL_FEED = "https://feeds.elpais.com/mrss-s/pages/ep/site/elpais.com/portada";
    private Button btPost;
    private TextView txtPost;

    private List<News> newsList;

    RecyclerView newsListView;
    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = inflater.inflate(R.layout.fragment_news, container, false);

        if (newsList == null) newsList = new ArrayList<>();
        newsListView = (RecyclerView) root.findViewById(R.id.idRvNews);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        newsListView.setLayoutManager(layoutManager);

        //Cargar ultimas noticias
        loadLastNews();

        prueba();



        return root;
    }

    public void prueba(){
        System.out.println("Entra");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new XmlRequest<>(
                        URL_FEED,
                        Rss.class,
                        null,
                        new Response.Listener<Rss>() {
                            @Override
                            public void onResponse(Rss response) {
                                System.out.println("Noticias");
                                System.out.println(response.getChannel().getItems());
                                // Caching
                               // FeedDatabase.getInstance(getContext()).
                                 //       sincronizarEntradas(response.getChannel().getItems());
                                // Carga inicial de datos...

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, " Volley: " + error.getMessage());
                            }
                        }
                )
        );
    }


    /**
     * Metodo que carga las ultimas noticias a la lista de noticias
     */
    private void loadLastNews() {
        newsList.add(new News("Prueba", "Esto es una prueba de noticia", "", "https://elpais.com/sociedad/2020-11-26/ultimas-noticias-del-coronavirus-en-espana-y-en-el-mundo-en-directo.html"));

        createAdapter();
    }

    /**
     * Metodo que crea el adapter con todas las noticias obtenidas, una vez cargadas en la app
     */
    private void createAdapter(){
        //Crear el adapter con la lista de noticias cargada
        ListaNewsAdapter newsAdapter = new ListaNewsAdapter(newsList, new ListaNewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(News clickedNew) {
                clickOnItem(clickedNew);
            }
        });
        newsListView.setAdapter(newsAdapter);
    }

    /**
     * Metodo que abre la noticia en el navegador
     * @param clickedNew, noticia en la que se ha pulsado
     */
    private void clickOnItem(News clickedNew) {
        Log.d("URLNoticia", "URL:"+clickedNew.getUrlNews());

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickedNew.getUrlNews()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }


}