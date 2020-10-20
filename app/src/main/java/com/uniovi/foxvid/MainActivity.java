
package com.uniovi.foxvid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniovi.foxvid.modelo.User;
import com.uniovi.foxvid.vista.Login;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button btLogOut;
    private Button btPost;

    private TextView txtPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btLogOut = (Button)findViewById(R.id.idLogOut);
        btPost = (Button)findViewById(R.id.idBtPost);
        txtPost = (TextView)findViewById(R.id.idTxtPost);

        btLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post();
            }
        });

        User user = this.getIntent().getParcelableExtra(Login.USER_EMAIL);
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
        finish();
    }

    private void post(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uuid = UUID.randomUUID().toString();
        Map<String, String> posts = new HashMap<>();
        posts.put("uid",uuid );
        posts.put("post", txtPost.getText().toString());
        posts.put("user", FirebaseAuth.getInstance().getCurrentUser().getUid());
        posts.put("fecha", Timestamp.now().toString());


        db.collection("post").document(uuid)
                .set(posts)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error writing document", e);
                    }
                });
    }

}