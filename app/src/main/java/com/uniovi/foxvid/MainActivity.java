
package com.uniovi.foxvid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;




import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firestore.v1.StructuredQuery;
import com.uniovi.foxvid.modelo.Post;
import com.uniovi.foxvid.modelo.User;
import com.uniovi.foxvid.vista.Login;


import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button btLogOut;
    private Button btPost;

    private TextView txtPost;
    private  List<Post> listPost;
    private  List<Post> reverselistPost;

    RecyclerView listPostView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btLogOut = (Button)findViewById(R.id.idLogOut);
        btPost = (Button)findViewById(R.id.idBtPost);
        txtPost = (TextView)findViewById(R.id.idTxtPost);
        listPostView = (RecyclerView) findViewById(R.id.rvPost);

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

        listPost= new ArrayList<Post>();

        loadPost();


        User user = this.getIntent().getParcelableExtra(Login.USER_EMAIL);
        TextView email =  (TextView)findViewById(R.id.idEmail);
        email.setText(user.getEmail());




    }

    protected void loadPost(){

        listPostView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listPostView.setLayoutManager(layoutManager);


        //rellenarList();
        updateValues();
        //readPostDabase();

       /** ListaPostAdapter lpAdapter = new ListaPeliculaAdapter(listPeli,
                new ListaPeliculaAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Post post) {
                        c//lickOnItem(pelicula);
                    }
                });


        listaPeliView.setAdapter(ldAdapter);**/


    }

    private void updateValues(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("post")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    listPost.add(new Post(null,
                                            dc.getDocument().get("post").toString(),
                                            //public User(String uid, String name, String email, Uri photo)
                                            new User(dc.getDocument().get("userUid").toString(),null, dc.getDocument().get("userEmail").toString() ,null),
                                            (Timestamp)dc.getDocument().get("date"),
                                            null
                                    ));
                                    listPostView.setAdapter(new ListaPostAdapter(listPost,null));
                                    break;
                                case MODIFIED:
                                    //Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    //Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }


   /* private void readPostDabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("post")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            System.out.println("--------------------------------------------");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Post(String title, String text, User user, Timestamp date, String localization)
                                listPost.add(new Post(null,
                                        document.get("post").toString(),
                                        //public User(String uid, String name, String email, Uri photo)
                                        new User(document.get("userUid").toString(),null, document.get("userEmail").toString() ,null),
                                        document.get("date").toString(),
                                        null
                                        ));
                            }


                            listPostView.setAdapter(new ListaPostAdapter(listPost,null));
                            listPost= new ArrayList<Post>();
                            updateValues();


                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }*/


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
        Map<String, Object> posts = new HashMap<>();
        posts.put("uid",uuid );
        posts.put("post", txtPost.getText().toString());
        posts.put("userUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        posts.put("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        posts.put("date", Timestamp.now());

        txtPost.setText("");


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


   /* private void filtarPorUbicacion(long lat1, long lon1, long lat2, long lon2){
            let rad = function (x) { return x * Math.PI / 180; }
            var R = 6378.137; //Radio de la tierra en km
            var dLat = rad(lat2 - lat1);
            var dLong = rad(lon2 - lon1);
            var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(rad(lat1)) * Math.cos(rad(lat2)) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
            var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            var d = R * c;
            return d;
        }

    }*/
}