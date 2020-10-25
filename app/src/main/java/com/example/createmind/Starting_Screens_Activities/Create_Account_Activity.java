package com.example.createmind.Starting_Screens_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.createmind.MainActivity;
import com.example.createmind.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Create_Account_Activity extends AppCompatActivity implements View.OnClickListener {

    private final static int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;

    private Button googleSignInButton, signUp_Button;
    private TextInputLayout email_EditText, password_EditText, username_EditText;
    private ProgressBar progressBar;
    private String username, email, password;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__account_signup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        googleSignInButton = findViewById(R.id.google_signIn_Button_id_signUp);
        progressBar = findViewById(R.id.progressBar_signUp_screen);
        signUp_Button = findViewById(R.id.signUp_Button_id);
        email_EditText = findViewById(R.id.email_editText_signUp);
        password_EditText = findViewById(R.id.password_editText_signUp);
        username_EditText = findViewById(R.id.username_editText_signUp);


        signUp_Button.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);
        progressBar.setVisibility(View.INVISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();



        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUp_Button_id:
                username = Objects.requireNonNull(username_EditText.getEditText()).getText().toString().trim();
                email = Objects.requireNonNull(email_EditText.getEditText()).getText().toString().trim();
                password = Objects.requireNonNull(password_EditText.getEditText()).getText().toString().trim();

                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    progressBar.setVisibility(View.VISIBLE);
                    createUserEmailandPassword(username, email, password);
                } else {
                    Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.google_signIn_Button_id_signUp:
                signIn();
                break;
        }
    }

    private void createUserEmailandPassword(final String username, String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = firebaseAuth.getCurrentUser();
                            final String userId = currentUser.getUid();

                            Map<String, String> usernameObj = new HashMap<>();
                            usernameObj.put("userName", username);
                            usernameObj.put("userId", userId);
                            collectionReference.add(usernameObj)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                            documentReference.get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.getResult().exists()){
                                                                String name = task.getResult().getString("userName");
                                                                Api api = Api.getInstance();
                                                                api.setUserName(name);
                                                                api.setUserId(userId);

                                                                Toast toast =new Toast(getApplicationContext());
                                                                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.toast_welcome_back,null);
                                                                TextView toastTextView = view.findViewById(R.id.toast_TextView);
                                                                toastTextView.setText("WELCOME "+api.getUserName());
                                                                toast.setView(view);
                                                                toast.setDuration(Toast.LENGTH_SHORT);
                                                                toast.show();
                                                                toast.setGravity(Gravity.CENTER,0,0);

                                                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                                                finish();
                                                            }else{
                                                                Toast.makeText(Create_Account_Activity.this, "getResult Not Found", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Create_Account_Activity.this, "USER Not Added" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Create_Account_Activity.this, "Error Occour" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.d("abc", "before Try");
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                Log.d("abc", "firebaseAuthWithGoogle:" + account.getId());
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.d("abc", "Google sign in failed" + e.toString());
                Toast.makeText(this, "Google sign in failed in On Result", Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acc) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("abc", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            String name_user = user.getDisplayName();
                            String id_user = user.getUid();
                            Api api = Api.getInstance();
                            api.setUserName(name_user);
                            api.setUserId(id_user);
                            progressBar.setVisibility(View.INVISIBLE);

                            Toast toast =new Toast(getApplicationContext());
                            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.toast_welcome_back,null);
                            TextView toastTextView = view.findViewById(R.id.toast_TextView);
                            toastTextView.setText("WELCOME "+api.getUserName());
                            toast.setView(view);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                            toast.setGravity(Gravity.CENTER,0,0);

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(Create_Account_Activity.this, "Fail To Authenticate", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

}