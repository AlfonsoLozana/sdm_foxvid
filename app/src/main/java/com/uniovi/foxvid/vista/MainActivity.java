
package com.uniovi.foxvid.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;


import com.google.android.material.bottomnavigation.BottomNavigationView;


import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
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
    private Toolbar toolbar;
    private ImageButton btProfile;
    private FirebaseAuth mAuth;

    private User user;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        user = new User(mAuth.getCurrentUser());

       //Gestion de la botonera
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        drawerLayout = (DrawerLayout)findViewById(R.id.idDrawerLayout);

        btProfile = (ImageButton) findViewById(R.id.btProfile);
        Picasso.get().load(user.getPhoto()).into(btProfile);
      btProfile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                }
        );

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




    private void loadPostView(){
        //Creamos el framento de información
        PostFragment info = new PostFragment();
        Bundle args = new Bundle();
        info.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.new_post_menu, menu);
       // getMenuInflater().inflate(R.menu.toll_bar_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            /*case R.id.home:
                this.finish();
                return true;*/
            default:
                return false;
        }
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