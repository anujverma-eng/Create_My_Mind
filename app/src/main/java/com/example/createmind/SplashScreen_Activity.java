package com.example.createmind;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.createmind.Starting_Screens_Activities.Login_SignUp;
import com.example.createmind.Starting_Screens_Activities.OnBoarding_Activity;

public class SplashScreen_Activity extends AppCompatActivity {

    SharedPreferences onBoardinSharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);



        //**************************************************************************************************************************************
        //TODO: Find ALl Id's Here  Below ||||||||
        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        ImageView logo_imageView = findViewById(R.id.logo_image_view);
        ImageView logo_stick_imageView = findViewById(R.id.logo_stick_image_view);

        //**************************************************************************************************************************************











        //**************************************************************************************************************************************
        //TODO: Below is the handle of splash screen and also checks wheather the user install the app for first time or not i.e onBoarding screens
        // Animations are also written below for splash screen ||||||||
        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onBoardinSharedPrefs = getSharedPreferences("onBoardinSharedPrefs",MODE_PRIVATE);
                boolean isFirstTime = onBoardinSharedPrefs.getBoolean("firstTime",true);

                if(isFirstTime){

                    SharedPreferences.Editor editor = onBoardinSharedPrefs.edit();
                    editor.putBoolean("firstTime",false);
                    editor.apply();
                    startActivity(new Intent(SplashScreen_Activity.this, OnBoarding_Activity.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashScreen_Activity.this, Login_SignUp.class));
                    finish();
                }
            }
        },3000);
        Animation animation1 = AnimationUtils.loadAnimation(this,R.anim.splash_animation);
        Animation animation2 = AnimationUtils.loadAnimation(this,R.anim.splash_stick);

        logo_imageView.startAnimation(animation1);
        logo_stick_imageView.startAnimation(animation2);

        //**************************************************************************************************************************************
        //TODO: ------------------------------>>>>>>>>>>>>>>>>.
        //**************************************************************************************************************************************
        // TODO: This is for the Full Screen
        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        //**************************************************************************************************************************************

    } // TODO: last bracket of OnCreate

} // TODO: last bracket of class ie activity


//    model_thoughtsList = new ArrayList<>();
//        recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//
//        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
//                    Model_Thoughts model_thoughts = snapshot.toObject(Model_Thoughts.class);
//                    model_thoughtsList.add(model_thoughts);
//                    Log.d("abc","=======>>>>>>>>>> "+model_thoughts.getThought());
//                    Log.d("abc","=======>>>>>>>>>> "+model_thoughts.getAuthor());
//                }
//                adapter = new Adapter(MainActivity.this,model_thoughtsList);
//                recyclerView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("abc","======>>> Fails <<<====="+ e.getMessage());
//            }
//        });