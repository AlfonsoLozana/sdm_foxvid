package com.uniovi.foxvid.controlador.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.uniovi.foxvid.modelo.News;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public final class FeedDatabase extends SQLiteOpenHelper {

    // Mapeado rápido de indices
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_TITULO = 1;
    private static final int COLUMN_DESC = 2;
    private static final int COLUMN_URL = 3;
    private static final int COLUMN_URL_MINIATURA = 4;
    private static final int COLUMN_FECHA = 5;

    // Número de noticias mostradas
    private static  final int NUM_NEWS = 20;

    /*
    Instancia singleton
    */
    private static FeedDatabase singleton;

    /*
    Etiqueta de depuración
     */
    private static final String TAG = FeedDatabase.class.getSimpleName();


    /*
    Nombre de la base de datos
     */
    public static final String DATABASE_NAME = "Feed.db";

    /*
    Versión actual de la base de datos
     */
    public static final int DATABASE_VERSION = 1;

     //Array que contiene las palabras que debe contener una publicación para determinar que una
    // noticia esta relaccionada con el covid
    public static final String[] keyword = {"covid","sars","vacuna","mascarilla","coronavirus"};

    private FeedDatabase(Context context) {
        super(context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION);

    }

    /**
     * Retorna la instancia unica del singleton
     *
     * @param context contexto donde se ejecutarán las peticiones
     * @return Instancia
     */
    public static synchronized FeedDatabase getInstance(Context context) {
        if (singleton == null) {
            singleton = new FeedDatabase(context.getApplicationContext());
        }
        return singleton;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla 'entrada'
        db.execSQL(ScriptDatabase.CREAR_ENTRADA);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Añade los cambios que se realizarán en el esquema
        db.execSQL("DROP TABLE IF EXISTS " + ScriptDatabase.ENTRADA_TABLE_NAME);
        onCreate(db);
    }

    /**
     * Obtiene todos los registros de la tabla entrada
     *
     * @return cursor con los registros
     */
    public Cursor obtenerEntradas() {
        // Seleccionamos todas las filas de la tabla 'entrada'
        return getWritableDatabase().rawQuery(
                "select * from " + ScriptDatabase.ENTRADA_TABLE_NAME +
                        " ORDER BY date DESC", null);
    }

    public List<News> getNews() {
        int id;
        String titulo;
        String descripcion;
        String url;
        String urlImage;
        Date date;
        List<News> salida = new ArrayList<News>();
        Cursor c = obtenerEntradas();
        while (c.moveToNext()) {
            id = c.getInt(COLUMN_ID);
            titulo = c.getString(COLUMN_TITULO);
            descripcion = c.getString(COLUMN_DESC);
            url = c.getString(COLUMN_URL);
            urlImage = c.getString(COLUMN_URL_MINIATURA);
            date = new Date(c.getLong(COLUMN_FECHA));
            salida.add(new News(titulo, descripcion, url, urlImage,date));
        }

        return salida;
    }

    /**
     * Inserta un registro en la tabla entrada
     *
     * @param titulo        titulo de la entrada
     * @param descripcion   desripcion de la entrada
     * @param url           url del articulo
     * @param url_miniatura url de la miniatura
     */
    public void insertarEntrada(
            String titulo,
            String descripcion,
            String url,
            String url_miniatura, Date date) {

        ContentValues values = new ContentValues();
        values.put(ScriptDatabase.ColumnEntradas.TITULO, titulo);
        values.put(ScriptDatabase.ColumnEntradas.DESCRIPCION, descripcion);
        values.put(ScriptDatabase.ColumnEntradas.URL, url);
        values.put(ScriptDatabase.ColumnEntradas.URL_MINIATURA, url_miniatura);
        values.put(ScriptDatabase.ColumnEntradas.FECHA, date.getTime());

        // Insertando el registro en la base de datos
        getWritableDatabase().insert(
                ScriptDatabase.ENTRADA_TABLE_NAME,
                null,
                values
        );
    }

    private void borrarEntrada(String titulo){
        System.out.println("Debug: número de noticia borrada: " + titulo );
        getWritableDatabase().delete(ScriptDatabase.ENTRADA_TABLE_NAME, "titulo = " + "\"" + titulo + "\"", null);
    }

    /**
     * Procesa una lista de items para su almacenamiento local
     * y sincronización.
     *
     * @param entries lista de items
     */
    public void sincronizarEntradas(List<News> entries) {

    /*
    #1  Mapear temporalemente las entradas nuevas para realizar una
        comparación con las locales
    */
        HashMap<String, News> entryMap = new HashMap<String, News>();

        for (News e : entries) {
            /*
            #3.1 Comprobar que las noticias tienen que ver con el COVID
            */
            if(comprobarKeyWords(e.getTitle())){
                entryMap.put(e.getTitle(), e);
                Log.i(TAG, "Se ha añadido " + e.getTitle());
            }else if (comprobarKeyWords(e.getSummary())){
                entryMap.put(e.getTitle(), e);
                Log.i(TAG, "Se ha añadido " + e.getTitle());
            }
        }

        Log.i(TAG, "Se encontraron " + entryMap.size() + " nuevas entradas, computando...");

    /*
    #2  Obtener las entradas locales
     */
        Log.i(TAG, "Consultar items actualmente almacenados");
        Cursor c = obtenerEntradas();
        assert c != null;
        Log.i(TAG, "Se encontraron " + c.getCount() + " entradas, computando...");

    /*
    #3  Comenzar a comparar las entradas
     */
        int id;
        String titulo;
        String descripcion;
        String url;
        String urlImage;

        while (c.moveToNext()) {
            id = c.getInt(COLUMN_ID);
            titulo = c.getString(COLUMN_TITULO);
            descripcion = c.getString(COLUMN_DESC);
            url = c.getString(COLUMN_URL);

            Log.i(TAG, "Titulo:" + titulo);
            Log.i(TAG, "Date:" + new Date(c.getLong(COLUMN_FECHA)));
            News match = entryMap.get(titulo);
            if (match != null) {
                // Filtrar entradas existentes. Remover para prevenir futura inserción
                Log.i(TAG, "Borramos entrada con el titulo:" + titulo);
                entryMap.remove(titulo);
            }
        }

        int numberOfNews = 0;
        c = obtenerEntradas();
        System.out.println("Debug: número de noticias: " + entryMap.size() );
        while (c.moveToNext()) {
            System.out.println("Debug: id: " +c.getColumnName(COLUMN_ID) );
            if(entryMap.size() +  numberOfNews >= NUM_NEWS){
                    System.out.println("Debug: Entrar entro");
                    borrarEntrada(c.getString(COLUMN_TITULO));
            }
            numberOfNews ++;
            System.out.println("Debug: número de noticias recorridas: " + numberOfNews );
        }
        c.close();

    /*
    #4 Añadir entradas nuevas
    */
        for (News e : entryMap.values()) {

            Log.i(TAG, "Insertado: titulo=" + e.getTitle());
            insertarEntrada(
                    e.getTitle(),
                    e.getSummary(),
                    e.getUrlNews(),
                    e.getImage(),
                    e.getDate()
            );
        }


    }

    private boolean comprobarKeyWords(String text){
        for(String key:keyword){
            if(text.toLowerCase().contains(key)){
                return true;
            }
        }
        return false;
    }

}
