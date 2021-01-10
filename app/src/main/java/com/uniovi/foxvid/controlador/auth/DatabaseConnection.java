package com.uniovi.foxvid.controlador;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniovi.foxvid.modelo.Post;
import com.uniovi.foxvid.modelo.User;

import java.util.HashMap;
import java.util.Map;

public class DatabaseConnection {

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public void addUserDB(User user) {
        Map<String, String> users = new HashMap<>();
        users.put("uid", user.getUid());
        users.put("email", user.getEmail());
        users.put("name", user.getName());
        users.put("photo", user.getPhoto().toString());


        db.collection("users").document(user.getUid())
                .set(users)
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
