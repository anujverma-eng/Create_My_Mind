package com.example.createmind.Starting_Screens_Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class Login_SignUp extends AppCompatActivity implements View.OnClickListener {

    private final static int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;

    private static final String TAG = "GoogleSignInAct";
    private Button googleSignInButton, createAccount_Button, login_Button;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ProgressBar progressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__sign_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);



        googleSignInButton = findViewById(R.id.google_signIn_Button_id);
        progressBar = findViewById(R.id.progressBar_Login_signUp_screen);
        createAccount_Button = findViewById(R.id.create_new_account_id);
        login_Button = findViewById(R.id.logIn_Button_id);
        final TextInputLayout email_EditText = findViewById(R.id.email_editText_login);
        final TextInputLayout password_EditText = findViewById(R.id.password_editText_login);


        createAccount_Button.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);
        login_Button.setOnClickListener(this);
        progressBar.setVisibility(View.INVISIBLE);



        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(email_EditText.getEditText().getText().toString().trim()) && !TextUtils.isEmpty(password_EditText.getEditText().getText().toString().trim())) {
                    final String email = email_EditText.getEditText().getText().toString().trim();
                    final String password = password_EditText.getEditText().getText().toString().trim();
                    Login_with_EmailandPassword(email, password);
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_signIn_Button_id:
                signIn();
                break;
            case R.id.create_new_account_id:
                startActivity(new Intent(getApplicationContext(),Create_Account_Activity.class));
                break;

        }


    }

    private void Login_with_EmailandPassword(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            currentUser = mAuth.getCurrentUser();
                            assert currentUser != null;
                            String curentuserID = currentUser.getUid();


                        collectionReference.whereEqualTo("userId",curentuserID)
                                           .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                               @Override
                                               public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                                   if (error!=null){
                                                       return;
                                                   }
                                                   if (!value.isEmpty()){
                                                       for (QueryDocumentSnapshot snapshot:value){

                                                           Api api = Api.getInstance();
                                                           api.setUserName(snapshot.getString("userName"));
                                                           api.setUserId(snapshot.getString("userId"));


                                                           // creating our own custom Toast
                                                           Toast toast =new Toast(getApplicationContext());
                                                           View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.toast_welcome_back,null);
                                                           TextView toastTextView = view.findViewById(R.id.toast_TextView);
                                                           toastTextView.setText("WELCOME BACK");
                                                           toast.setView(view);
                                                           toast.setDuration(Toast.LENGTH_SHORT);
                                                           toast.show();
                                                           toast.setGravity(Gravity.CENTER,0,0);

                                                           startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                           finish();
                                                           progressBar.setVisibility(View.INVISIBLE);
                                                       }
                                                   }
                                               }
                                           });

                        }else{
                            progressBar.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(getApplicationContext(), Create_Account_Activity.class));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login_SignUp.this, "You Don't have an Account,\n Please Create a New Account", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Create_Account_Activity.class));
                Toast.makeText(Login_SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Log.d(TAG, "before Try");
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG, "Google sign in failed" + e.toString());
                Toast.makeText(this, "Google sign in failed in On Result", Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acc) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            progressBar.setVisibility(View.INVISIBLE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            String name_user = user.getDisplayName();
                            String id_user = user.getUid();
                            Api api = Api.getInstance();
                            api.setUserName(name_user);
                            api.setUserId(id_user);

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
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login_SignUp.this, "Fail To Authenticate", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser current_User = mAuth.getCurrentUser();
        if (current_User != null) {



            String nameCheck = current_User.getDisplayName();
            String curentuserID = current_User.getUid();

            if (nameCheck.isEmpty()){
                //Toast.makeText(this, "is Empty", Toast.LENGTH_SHORT).show();
                collectionReference.whereEqualTo("userId",curentuserID)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                if (error!=null){
                                    return;
                                }
                                if (!value.isEmpty()){
                                    for (QueryDocumentSnapshot snapshot:value){

                                        Api api = Api.getInstance();
                                        api.setUserName(snapshot.getString("userName"));
                                        api.setUserId(snapshot.getString("userId"));
                                        //passuserName(api.getUserName(),api.getUserId());
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                        Toast toast =new Toast(getApplicationContext());
                                        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.toast_welcome_back,null);
                                        TextView toastTextView = view.findViewById(R.id.toast_TextView);
                                        toastTextView.setText(" ! Hi "+api.getUserName()+" ! ");
                                        toast.setView(view);
                                        toast.setDuration(Toast.LENGTH_LONG);
                                        toast.show();
                                        toast.setGravity(Gravity.TOP,0,0);

                                        finish();
                                    }
                                }
                            }
                        });
            }
            else{
                //Toast.makeText(this, "Google", Toast.LENGTH_SHORT).show();
                Api api = Api.getInstance();
                String name_user = current_User.getDisplayName();
                String id_user = current_User.getUid();
                api.setUserName(name_user);
                api.setUserId(id_user);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

                Toast toast =new Toast(getApplicationContext());
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.toast_welcome_back,null);
                TextView toastTextView = view.findViewById(R.id.toast_TextView);
                toastTextView.setText(" ! Hi "+api.getUserName()+" ! ");
                toast.setView(view);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
                toast.setGravity(Gravity.TOP,0,0);

                finish();
            }





        }

    }


}

/*



         <RelativeLayout
        android:id="@+id/header"
        android:layout_width="match_parent"
        android:layout_height="150dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">

        <ImageView
            android:layout_width="match_parent"
            android:layout_height="150dp"
            android:src="@drawable/ic_username"
            android:tint="@color/colorAccent" />
    </RelativeLayout>

    <RelativeLayout
        android:layout_width="220dp"
        android:layout_height="480dp"
        android:layout_marginRight="198dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/header">

        <androidx.cardview.widget.CardView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@color/fui_transparent"
            android:backgroundTint="@color/fui_transparent"
            android:elevation="8dp"
            app:cardUseCompatPadding="true">

            <ImageView
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:src="@drawable/shape_left_pannel" />
        </androidx.cardview.widget.CardView>
    </RelativeLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="350dp"
        android:layout_margin="16dp"
        android:layout_marginTop="60dp"
        app:layout_constraintTop_toBottomOf="@+id/header">

        <androidx.cardview.widget.CardView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:cardElevation="8dp"
            app:cardUseCompatPadding="true">

            <com.google.android.material.tabs.TabLayout
                android:id="@+id/tabLayout"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                app:tabBackground="@color/fui_transparent"
                app:tabIndicatorColor="#141e30"
                app:tabRippleColor="#ff5b55"
                app:tabSelectedTextColor="#141e30">

                <com.google.android.material.tabs.TabItem
                    android:id="@+id/tab_id_login"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Login" />

                <com.google.android.material.tabs.TabItem
                    android:id="@+id/tab_item_signup"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:contextClickable="true"
                    android:text="SignUp "
                    android:textAlignment="center" />

            </com.google.android.material.tabs.TabLayout>

            <androidx.viewpager.widget.ViewPager
                android:id="@+id/viewPager_id_show_signup_login"
                android:layout_width="match_parent"
                android:layout_height="match_parent">

            </androidx.viewpager.widget.ViewPager>


        </androidx.cardview.widget.CardView>
    </LinearLayout>












    ******************************************************************






    <ImageView
        android:id="@+id/imageView2"
        android:layout_width="1104dp"
        android:layout_height="620dp"
        android:src="@color/fui_transparent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="1.0" />

    <androidx.cardview.widget.CardView
        android:id="@+id/cardView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        android:backgroundTint="@color/fui_transparent"
        android:background="@color/fui_transparent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">

        <com.google.android.material.tabs.TabLayout
            android:id="@+id/tabLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:tabBackground="@color/fui_transparent"
            app:tabIndicatorColor="#141e30"
            app:tabRippleColor="#ff5b55"
            app:tabSelectedTextColor="#141e30">

            <com.google.android.material.tabs.TabItem
                android:id="@+id/tab_id_login"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Login" />

            <com.google.android.material.tabs.TabItem
                android:id="@+id/tab_item_signup"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:contextClickable="true"
                android:text="SignUp "
                android:textAlignment="center" />

        </com.google.android.material.tabs.TabLayout>

        <androidx.viewpager.widget.ViewPager
            android:id="@+id/viewPager_id_show_signup_login"
            android:layout_width="match_parent"
            android:layout_height="300dp"
            android:layout_marginTop="51dp">

        </androidx.viewpager.widget.ViewPager>
    </androidx.cardview.widget.CardView>

    <ImageView
        android:id="@+id/imageView"
        android:layout_width="199dp"
        android:layout_height="158dp"
        android:layout_marginTop="16dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:srcCompat="@drawable/welcome_ellipse" />

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@android:color/transparent"
        android:text="WELCOME !"
        android:textSize="26sp"
        android:textStyle="bold"
        app:layout_constraintBottom_toBottomOf="@+id/imageView"
        app:layout_constraintEnd_toEndOf="@+id/imageView"
        app:layout_constraintStart_toStartOf="@+id/imageView"
        app:layout_constraintTop_toTopOf="@+id/imageView" />

    <LinearLayout
        android:id="@+id/linearLayout3"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="1.0"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/cardView"
        app:layout_constraintVertical_bias="0.0">

        <View
            android:layout_width="170dp"
            android:layout_height="1dp"
            android:layout_marginTop="12dp"
            android:background="#a2a3ab" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="@drawable/circle_of_or_in_signup_login"
            android:backgroundTint="#141e30"
            android:padding="5dp"
            android:text="OR"
            android:textColor="#ffffff"
            android:textStyle="bold" />

        <View
            android:layout_width="192dp"
            android:layout_height="1dp"
            android:layout_marginTop="12dp"
            android:background="#a2a3ab" />
    </LinearLayout>

    <androidx.cardview.widget.CardView
        app:cardCornerRadius="100dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="30dp"
        android:background="@color/fui_transparent"
        android:backgroundTint="@color/fui_transparent"
        android:elevation="8dp"
        app:cardUseCompatPadding="true"
        app:layout_constraintBottom_toBottomOf="@+id/imageView2"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.0"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/linearLayout3"
        app:layout_constraintVertical_bias="0.119">

        <Button
            android:id="@+id/google_signIn_button"
            style="@style/FirebaseUI.Button.AccountChooser.GoogleButton"
            android:layout_width="300dp"
            android:layout_height="match_parent"
            android:background="@drawable/design_login_signup_button"
            android:backgroundTint="#ffffff" />
    </androidx.cardview.widget.CardView>


 */