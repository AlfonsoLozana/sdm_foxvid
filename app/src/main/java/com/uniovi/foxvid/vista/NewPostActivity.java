package com.uniovi.foxvid.vista;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.uniovi.foxvid.utils.PostsDatabaseHandler;
import com.uniovi.foxvid.utils.LocationHandler;
import com.uniovi.foxvid.R;

public class NewPostActivity extends AppCompatActivity {

    private TextView txtPost;

    LocationHandler locationHandler = LocationHandler.getLocationHandler();
    PostsDatabaseHandler postsHandler = PostsDatabaseHandler.getPostsDatabaseHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);


        Toolbar toolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtPost = findViewById(R.id.txtNewPost);
        locationHandler.updateLocate(this, null);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_post_menu, menu);
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home:
                this.finish();
                return true;

            case R.id.btn_publish_post:
                post();
                this.finish();
                return true;
            default:
                return false;
        }
    }


    /**
     * Método que crea un nuevo post a la base de datos.
     */
    private void post() {
        //Si no se ha escrito nada
        if (txtPost.getText().toString().isEmpty()) {
            System.out.println("Debug: todo mal");
            showSnackbar(R.string.post_text_empty);
        }
        //Si la localización delusuario no se ha obtenido correctamente
        else if (locationHandler.getUserCoordinate().getLat().isNaN() || locationHandler.getUserCoordinate().getLat().isInfinite() || locationHandler.getUserCoordinate().getLat() == 0) {
            System.out.println("Debug: todo mal");
            locationHandler.updateLocate(this, null);
        }
        //Si no hay errores
        else {

            System.out.println("Debug: todo bien");
            postsHandler.publishPost(txtPost.getText().toString(),
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showSnackbar(R.string.successful_post_upload);
                        }
                    },
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showSnackbar(R.string.failure_post_upload);

                        }
                    });

            txtPost.setText("");

        }
    }


    /**
     * Método que muestra un snackbar con un mensaje que se le pasa por parámetro,
     * además también se le puede asignar una texto y un listener para el botón de acción.
     *
     * @param snackStrId, id de la cadena que se desea mostrar, de tipo int
     */
    private void showSnackbar(int snackStrId) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                getString(snackStrId),
                BaseTransientBottomBar.LENGTH_INDEFINITE);


        snackbar.show();
    }


}