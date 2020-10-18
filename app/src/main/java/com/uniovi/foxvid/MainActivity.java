package com.uniovi.foxvid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button btLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btLogOut = (Button)findViewById(R.id.idLogOut);
        btLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        FirebaseUser user = this.getIntent().getParcelableExtra(Login.USER_EMAIL);
        TextView email =  (TextView)findViewById(R.id.idEmail);
        email.setText(user.getEmail());


    }

    protected void logOut(){
        FirebaseAuth.getInstance().signOut();
        loadLogActivity();
    }
    private void loadLogActivity(){
        Intent intent = new Intent(this,Login.class);
        startActivity(intent);
    }

}