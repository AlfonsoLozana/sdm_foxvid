
package com.uniovi.foxvid.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import com.uniovi.foxvid.R;
import com.uniovi.foxvid.modelo.Post;
import com.uniovi.foxvid.modelo.User;

import com.uniovi.foxvid.vista.fragment.NewsFragment;
import com.uniovi.foxvid.vista.fragment.PostFragment;
import com.uniovi.foxvid.vista.fragment.StatisticsFragment;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btLogOut;




    private  List<Post> listPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Gestion de la botonera
        BottomNavigationView navView = findViewById(R.id.nav_view);
        //Le añado un listener
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        loadPostView();

       /* btLogOut = (Button)findViewById(R.id.idLogOut);




        btLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });



        //User user = this.getIntent().getParcelableExtra(Login.USER_EMAIL);

        User user = new User(FirebaseAuth.getInstance();
        TextView email =  (TextView)findViewById(R.id.idEmail);
        email.setText(user.getEmail());
        */
     








    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toll_bar_menu, menu);
        return true;
    }


    private void loadPostView(){
        //Creamos el framento de información
        PostFragment info = new PostFragment();
        Bundle args = new Bundle();
        info.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();

    }
    private void loadNewPostView(){
//        //Creamos el framento de información
//        NewPostFragment info = new NewPostFragment();
//        Bundle args = new Bundle();
//        info.setArguments(args);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();

    }

    private void loadNewsView(){
        NewsFragment info = new NewsFragment();
        Bundle args = new Bundle();
        info.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();
    }

    private void loadStatistics(){
        StatisticsFragment info = new StatisticsFragment();
        Bundle args = new Bundle();
        info.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();
    }

    protected void logOut(){
        FirebaseAuth.getInstance().signOut();
        loadLogActivity();
    }
    private void loadLogActivity(){
        Intent intent = new Intent(this,Login.class);
        startActivity(intent);
        finish();
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_post:
                                loadPostView();
                        return true;
                    case R.id.nav_statistics:
                        loadStatistics();
                        return true;
                    case R.id.nav_news:
                        loadNewsView();
                        return true;

            }
            return false;
        }
    };
}