package com.uniovi.foxvid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = this.getIntent().getParcelableExtra(Login.USER_EMAIL);
        TextView email =  (TextView)findViewById(R.id.idEmail);
        email.setText(user.getEmail());

    }
}