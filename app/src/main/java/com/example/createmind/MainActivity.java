package com.example.createmind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.createmind.All_Fragments_Under_Main_Activity.Home_Fragment;
import com.example.createmind.Starting_Screens_Activities.Api;
import com.example.createmind.Starting_Screens_Activities.Login_SignUp;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TextView username;
    static final float END_SCALE = 0.7f;
    private AlertDialog dialog ,dialog_exit;
    private AlertDialog.Builder builder ,builder_exit;
    private LayoutInflater layoutInflater ,layoutInflater_exit;
    //Drawer Menu
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    FrameLayout contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar_main_activity);
        contentView = findViewById(R.id.container_frame_layout);

        setSupportActionBar(toolbar);

        drawerLayout.setDrawerElevation(8);
        View header = navigationView.getHeaderView(0);
        username = header.findViewById(R.id.username_header_layout_in_navigation_drawer);

        Api api = Api.getInstance();
        username.setText(api.getUserName());

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        drawerLayout.setScrimColor(getResources().getColor(R.color.color_Transparent));

        getSupportFragmentManager().beginTransaction().replace(R.id.container_frame_layout, new Home_Fragment()).commit();
        navigationView.setCheckedItem(R.id.home_menu_button);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            Fragment temp;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_menu_button:
                        temp = new Home_Fragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_frame_layout, temp).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.favorite_menu_button:
                        Toast.makeText(MainActivity.this,"FavouriteList" , Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);

                        break;
                    case R.id.like_menu_button:
                        Toast.makeText(MainActivity.this,"Like" , Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);

                        break;
                    case R.id.author_menu_button:
                        Toast.makeText(MainActivity.this,"Authors" , Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);

                        break;
                    case R.id.logout_menu_button:
                        builder = new AlertDialog.Builder(MainActivity.this);
                        layoutInflater = LayoutInflater.from(MainActivity.this);
                        View view = layoutInflater.inflate(R.layout.logout_popup,null);
                        Button yes_Button = view.findViewById(R.id.yes_button_pressed_to_logOUT);
                        Button no_Button = view.findViewById(R.id.no_button_pressed_to_logOUT);
                        builder.setView(view);
                        dialog = builder.create();

                        dialog.show();
                        yes_Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getApplicationContext(), Login_SignUp.class));
                                finish();
                            }
                        });
                        no_Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return true;
            }
        });

        animateNavigationDrawer();



    }

    //  ************************* METHOD TO ANIMATE NAVIGATION VIEW **************************** //
    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {


                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });  // *************** HERE METHOD TO ANIMATE NAVIGATION VIEW ENDS *********************//

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else{
            builder_exit = new AlertDialog.Builder(MainActivity.this);
            layoutInflater_exit = LayoutInflater.from(MainActivity.this);
            View view = layoutInflater_exit.inflate(R.layout.exit_from_app_popup,null);
            Button yes_Button = view.findViewById(R.id.yes_button_pressed_to_EXIT_FROM_APP);
            Button no_Button = view.findViewById(R.id.no_button_pressed_to_EXIT_FROM_APP);
            builder_exit.setView(view);
            dialog_exit = builder_exit.create();

            dialog_exit.show();
            yes_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.super.onBackPressed();
                }
            });
            no_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_exit.dismiss();
                }
            });

        }

    }
}


//        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
//        if (signInAccount!=null){
//            username.setText(signInAccount.getDisplayName());
//            user.setText(signInAccount.getEmail());
//        }

//    Api api = Api.getInstance();
//        username.setText(api.getUserName());
//        user.setText(api.getUserId());
//
//        logOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                user.setText("");
//                username.setText("");
//                startActivity(new Intent(getApplicationContext(), Login_SignUp.class));
//                finish();
//            }
//        });
//        sharedPreferences_save_userName = getSharedPreferences("sharedPreferences_save_userName",MODE_PRIVATE);
//        String toSave_userName = sharedPreferences_save_userName.getString("userName",api.getUserName());
//        SharedPreferences.Editor editor = sharedPreferences_save_userName.edit();
//        editor.putString("userName",api.getUserName());
//        editor.apply();