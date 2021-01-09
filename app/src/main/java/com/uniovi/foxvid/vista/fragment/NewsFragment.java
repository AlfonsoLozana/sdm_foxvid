package com.uniovi.foxvid.vista.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uniovi.foxvid.ListaNewsAdapter;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.controlador.News.FeedDatabase;
import com.uniovi.foxvid.modelo.News;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class NewsFragment extends Fragment {

    private static final String TAG = "Error " ;
    private static final String URL_FEED = "https://www.abc.es/rss/feeds/abc_SociedadSalud.xml";
    private FeedDatabase feedDatabase;

    private List<News> newsList;

    RecyclerView newsListView;
    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedDatabase = feedDatabase.getInstance(getContext());

        root = inflater.inflate(R.layout.fragment_news, container, false);

        if (newsList == null) newsList = new ArrayList<>();
        newsListView = (RecyclerView) root.findViewById(R.id.idRvNews);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        newsListView.setLayoutManager(layoutManager);

        //Cargar ultimas noticias
        loadLastNews();
        return root;
    }


    /**
     * Metodo que carga las ultimas noticias a la lista de noticias
     */
    private void loadLastNews() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="http://www.google.com";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_FEED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        feedDatabase.sincronizarEntradas(parseXML(response));
                        createAdapter();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Log.d(TAG,"Se a producido un error al cargar las noticias");
            }
        });

        queue.add(stringRequest);
        createAdapter();

    }

    /**
     * Metodo que crea el adapter con todas las noticias obtenidas, una vez cargadas en la app
     */
    private void createAdapter(){
        //Crear el adapter con la lista de noticias cargada
        ListaNewsAdapter newsAdapter = new ListaNewsAdapter(feedDatabase.getNews() , new ListaNewsAdapter.OnItemClickListener() {
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

    /**
     * Parser para las noticias obtenidas del proovedor, en este caso ABC
     * @param xml
     * @return
     */
    private List<News> parseXML(String xml){
        List<News> news = new ArrayList<News>();
        DateFormat dateFormatterRssPubDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("item");
            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String title = eElement.getElementsByTagName("title").item(0).getTextContent();
                    String URL = eElement.getElementsByTagName("link").item(0).getTextContent();
                    String content[] = eElement.getElementsByTagName("description").item(0).getChildNodes().item(0).getTextContent().split("src=\"")[1].split("\">");
                    String img = content[0];
                    String description = content[1].substring(0,150) + "...";
                    Date date = dateFormatterRssPubDate.parse(eElement.getElementsByTagName("pubDate").item(0).getTextContent());
                    news.add(new News(title,description,URL,img,date));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return news;

    }


}