package com.uniovi.foxvid.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.uniovi.foxvid.R;
import com.uniovi.foxvid.modelo.User;

public class Login extends AppCompatActivity {

    private final static int RC_SIGN_IN = 1;
    public final static String USER_EMAIL = "user_email";


    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FusedLocationProviderClient fusedLocationClient;

    //Componentes
    Button loginButton;
    Button githubLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();


        if (user != null) {

            loadMainActivity(new User(user));
        } else {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mGoogleSignInClient.signOut();
            //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


            loginButton = (Button) findViewById(R.id.btGoogle);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });

            githubLoginButton = (Button) findViewById(R.id.btGithub);
            githubLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    githubSignIn();
                }
            });
        }

    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void githubSignIn() {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    firebaseAuthWithGoogle(authResult.getCredential());
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                    Log.d("GithubLogin", "GithubLoginErr1");
                                }
                            });
        } else {
            mAuth
                    .startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().
                                    firebaseAuthWithGoogle(authResult.getCredential());
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("GithubLogin", "GithubLoginErr2:");
                                    e.printStackTrace();
                                    // Handle failure.

                                    if (e instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(getApplicationContext(), "Ha ocurrido un error en el inicio de sesión con Github: el correo electrónico ya existe",
                                                Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(getApplicationContext(), "Ha ocurrido un error en el inicio de sesión con Github, vuelva a intentarlo",
                                                Toast.LENGTH_SHORT).show();

                                }
                            });

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = null;
            try {
                account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuthWithGoogle(credential);
            } catch (ApiException e) {
                e.printStackTrace();
            }


        }
    }

    private void firebaseAuthWithGoogle(AuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();

                            User user1 = new User(user);
                            user1.saveUser();

                            loadMainActivity(user1);
                            System.out.println("Usuario: " + user.getEmail());

                        } else {
                            // If sign in fails, display a message to the user.
                            System.out.println("Error");

                        }

                    }
                });
    }

    private void loadMainActivity(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(USER_EMAIL, user);
        startActivity(intent);
        finish();
    }


}